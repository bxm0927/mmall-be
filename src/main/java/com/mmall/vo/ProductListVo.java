package com.mmall.vo;

import java.math.BigDecimal;

/**
 * 商品列表VO对象，比商品详情VO对象要简洁
 * 通过 POJO对象(product) 组装业务对象 VO(ProductDetailVo)
 */
public class ProductListVo {

    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    // private String subImages; // 商品列表不需要子图
    // private String detail; // 商品列表不需要详情
    private BigDecimal price;
    // private Integer stock; // 商品列表不需要库存
    private Integer status;

    // private String createTime; // 格式化处理
    // private String updateTime; // 格式化处理

    private String imageHost; // 图片地址的前缀
    // private Integer parentCategoryId; // 父分类编号

    public ProductListVo(Integer id, Integer categoryId, String name, String subtitle, String mainImage, BigDecimal price,
                         Integer status, String imageHost) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.subtitle = subtitle;
        this.mainImage = mainImage;
        this.price = price;
        this.status = status;
        this.imageHost = imageHost;
    }

    public ProductListVo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

}
