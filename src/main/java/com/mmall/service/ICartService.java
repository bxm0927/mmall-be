package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

    ServerResponse<CartVo> list(Integer userId);

    /**
     * 购物车商品选择或反选的公共方法
     */
    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
