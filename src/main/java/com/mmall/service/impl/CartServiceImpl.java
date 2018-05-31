package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.ServerResponseCode;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Resource
    private CartMapper cartMapper;

    @Resource
    private ProductMapper productMapper;

    /**
     * 组装购物车业务对象 CartVo
     */
    private CartVo getCartVoLimit(Integer userId) {

        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0"); // 初始化总价

        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                // 组装 CartProductVo
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    // 继续组装 CartProductVo
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }

                cartProductVoList.add(cartProductVo);
            }
        }

        // 组装 cartVo
        CartVo cartVo = new CartVo();
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    /**
     * 购物车商品勾选状态
     */
    private boolean getAllCheckedStatus(Integer userId) {
        return userId != null && cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccessData(cartVo);
    }

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), ServerResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null) {
            // 这个产品不在这个购物车里,需要新增一个这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);

            cartMapper.insert(cartItem);
        } else {
            // 这个产品已经在购物车里了.数量相加并更新该记录
            count = cart.getQuantity() + count;
            cart.setQuantity(count);

            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), ServerResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count); // 重置数量
        }

        cartMapper.updateByPrimaryKey(cart); // 更新数量
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds); // 线先分割然后转成集合
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), ServerResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        cartMapper.deleteByUserIdProductIds(userId, productList);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
        return this.list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccessData(0);
        }

        return ServerResponse.createBySuccessData(cartMapper.selectCartProductCount(userId));
    }

}
