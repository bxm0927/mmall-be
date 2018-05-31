package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * 总结：
 * 在科学计算中，通常使用 float、double
 * 在商业计算中，通常使用 BigDecimal，且一定要使用它的 String 构造器
 */
public class BigDecimalTest {

    @Test
    public void test1() {

        // 浮点型计算的问题
        System.out.println(0.01 + 0.05); // 0.060000000000000005
        System.out.println(1.0 - 0.42); // 0.5800000000000001
        System.out.println(4.015 * 100); // 401.49999999999994
        System.out.println(123.3 / 100); // 1.2329999999999999
    }

    @Test
    public void test2() {

        // double 的 BigDecimal 构造器
        BigDecimal b1 = new BigDecimal(0.01);
        BigDecimal b2 = new BigDecimal(0.05);
        System.out.println(b1.add(b2)); // 0.06000000000000000298372437868010820238851010799407958984375
    }

    @Test
    public void test3() {

        // String 的 BigDecimal 构造器
        BigDecimal b1 = new BigDecimal("0.01");
        BigDecimal b2 = new BigDecimal("0.05");
        System.out.println(b1.add(b2)); // 0.06
    }

}
