package com.mmall.util;

import java.math.BigDecimal;

/**
 * 浮点型工具类，解决浮点型商业运算中丢失精度的问题
 * 总结：
 * 在科学计算中，通常使用 float、double
 * 在商业计算中，通常使用 BigDecimal，且一定要使用它的 String 构造器
 */
public class BigDecimalUtil {

    private BigDecimalUtil() {
    }

    // 加
    public static BigDecimal add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    // 减
    public static BigDecimal sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }

    // 乘
    public static BigDecimal mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    // 除
    public static BigDecimal div(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP); // 四舍五入,保留2位小数
    }

}
