package com.mmall.common;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 常量类
 */
public class Const {

    // session key，表示当前用户
    public static final String CURRENT_USER = "currentUser";

    // 登陆账号类型
    public interface LoginType {
        String USERNAME = "username"; // 用户名登录登录
        String EMAIL = "email"; // 邮箱登录登录
    }

    // 用户角色
    public interface Role {
        int ROLE_CUSTOMER = 0; // 普通用户
        int ROLE_ADMIN = 1; // 管理员
    }

    // 商品销售状态
    public interface ProductStatus {
        int ON_SALE = 1; // 在售
        int OFF_SALE = 2; // 下架
        int DELETE = 3; // 删除
    }

    // 商品排序规则，定义一个Set集合来存储
    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc"); // 按价格降序、按价格升序
    }

}
