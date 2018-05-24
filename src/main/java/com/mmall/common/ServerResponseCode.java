package com.mmall.common;

/**
 * 服务响应的状态码
 * 枚举类：在 JDK1.5 之前，我们定义常量都是 public static fianl ....，
 * 现在好了，有了枚举，可以把相关的常量分组到一个枚举类型里，而且枚举提供了比常量更多的方法。
 * 这样调用：ServerResponseCode.SUCCESS.getCode();
 */
public enum ServerResponseCode {

    SUCCESS(0, "SUCCESS"),                      // 0 - 成功
    ERROR(1, "ERROR"),                          // 1 - 失败
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT"),    // 2 - 参数非法
    NEED_LOGIN(10, "NEED_LOGIN");               // 10 - 需要登录

    private final int code; // 响应的状态码

    private final String desc; // 响应的状态码的描述

    ServerResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
