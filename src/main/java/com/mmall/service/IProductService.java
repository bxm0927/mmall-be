package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse<String> save(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> manageProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> search(String productName, Integer productId, int pageNum, int pageSize);

    // 下面是前台门户网站接口业务逻辑 portal

    ServerResponse<ProductDetailVo> productDetail(Integer productId);

    ServerResponse<PageInfo> productList(Integer categoryId, String keyword, int pageNum, int pageSize, String orderBy);

}
