package com.mmall.controller.portal;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;

/**
 * 前台商品管理模块
 * 比后台商品管理的功能点少
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Resource
    private IProductService iProductService;

    // 商品详情
    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId) {

        return iProductService.productDetail(productId);
    }

    /**
     * 商品搜索、分页及动态排序，多功能接口
     *
     * @param categoryId 按分类id搜索，非必须
     * @param keyword    按关键字搜索，非必须
     * @param pageNum    页码
     * @param pageSize   页容量
     * @param orderBy    排序规则，price_asc、price_desc
     * @return json
     */
    @RequestMapping(value = "list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> list(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {

        return iProductService.productList(categoryId, keyword, pageNum, pageSize, orderBy);
    }

}
