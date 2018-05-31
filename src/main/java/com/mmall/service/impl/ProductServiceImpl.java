package com.mmall.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.ServerResponseCode;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtils;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private ICategoryService iCategoryService;

    // 通过 POJO对象(product) 组装业务对象 VO(ProductDetailVo)
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());

        // 图片地址的前缀
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.lovebxm.com/"));

        // 父分类编号，默认为根节点0
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        // createTime、updateTime 对时间戳进行格式化
        productDetailVo.setCreateTime(DateTimeUtils.date2Str(product.getCreateTime(), DateTimeUtils.STANDARD_FORMAT));
        productDetailVo.setUpdateTime(DateTimeUtils.date2Str(product.getUpdateTime(), DateTimeUtils.STANDARD_FORMAT));

        return productDetailVo;
    }

    // 通过 POJO对象(product) 组装业务对象 VO(ProductListVo)
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());

        // 图片地址的前缀
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));

        return productListVo;
    }

    @Override
    public ServerResponse<String> save(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数非法");
        }

        // 如果子图不为空，那么就取第一个子图赋给主图
        if (StringUtils.isNotBlank(product.getSubImages())) {
            String[] subImagesArr = product.getSubImages().split(",");
            if (subImagesArr.length > 0) {
                product.setMainImage(subImagesArr[0]);
            }
        }

        // 更新会传 id，新增不会传 id
        if (product.getId() != null) {
            int rows = productMapper.updateByPrimaryKey(product);
            if (rows > 0) {
                return ServerResponse.createBySuccessMsg("商品更新成功");
            }
        } else {
            int rows = productMapper.insert(product);
            if (rows > 0) {
                return ServerResponse.createBySuccessMsg("商品新增成功");
            }
        }

        return ServerResponse.createByErrorMsg("商品操作失败");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数非法");
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rows = productMapper.updateByPrimaryKeySelective(product);
        if (rows > 0) {
            return ServerResponse.createBySuccessMsg("更新商品状态成功");
        }

        return ServerResponse.createByErrorMsg("更新商品状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数非法");
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMsg("获取商品详情失败");
        }

        // 通过 POJO对象(product) 组装业务对象 VO(ProductDetailVo)
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccessData(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> manageProductList(int pageNum, int pageSize) {
        // PageHelper 分页相关计算
        PageHelper.startPage(pageNum, pageSize);

        List<Product> productList = productMapper.selectList();

        // 通过 POJO对象(product) 组装业务对象 VO(ProductListVo)
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        // PageHelper 分页相关计算
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccessData(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> search(String productName, Integer productId, int pageNum, int pageSize) {
        // PageHelper 分页相关计算
        PageHelper.startPage(pageNum, pageSize);

        // 如果传递了商品名称，构建模糊查询匹配串%like%
        if (StringUtils.isNotBlank(productName)) {
            productName = "%" + productName + "%";
        }

        // 按商品名称或商品id查询
        List<Product> productList = productMapper.selectByNameOrId(productName, productId);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        // PageHelper 分页相关计算
        PageInfo pageResult = new PageInfo<>(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccessData(pageResult);
    }

    // 下面是前台门户网站接口业务逻辑 portal

    @Override
    public ServerResponse<ProductDetailVo> productDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数非法");
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMsg("获取商品详情失败");
        }

        // 与后台不同的是，前台只显示已上架的商品
        if (product.getStatus() != Const.ProductStatus.ON_SALE) {
            return ServerResponse.createByErrorMsg("该商品已下架或删除");
        }

        // 封装业务对象
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccessData(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> productList(Integer categoryId, String keyword, int pageNum, int pageSize, String orderBy) {
        // 如果分类id和关键字都没有传递
        if (categoryId == null && StringUtils.isBlank(keyword)) {
            return ServerResponse.createByErrorCodeMsg(ServerResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数非法");
        }

        // 存放当前分类及子分类的集合，调用之前写的递归算法
        List<Integer> categoryIdList = new ArrayList<>();

        // 如果传递了分类编号 categoryId
        // 但是如果没有找到该分类，且没有传递关键字，那么就返回空的结果集，不报错
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);

            if (category == null && StringUtils.isBlank(keyword)) {
                // PageHelper 分页相关计算
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo<ProductListVo> pageInfo = new PageInfo<>(productListVoList);
                return ServerResponse.createBySuccessData(pageInfo);
            }

            categoryIdList = iCategoryService.getDeepCategory(categoryId).getData();
        }

        // 如果传递了关键字 keyword，则构建模糊查询
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + keyword + "%";
        }

        // 利用 PageHelper 实现动态排序
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArr = orderBy.split("_");
                PageHelper.orderBy(orderByArr[0] + " " + orderByArr[1]); // 如：price asc
            }
        }

        // 根据名称和分类id查询
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword,
                categoryIdList.size() == 0 ? null : categoryIdList);

        // 构建业务对象
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        // 对 SQL 的查询结果进行 pagehelper 运算，然后将结果赋值成业务对象VO
        PageInfo pageInfo = new PageInfo<>(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccessData(pageInfo);
    }

}
