package com.mmall.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * MD5 加密工具类
 * 为了防止 MD5 撞库，这里使用 MD5 + salt 的方式
 */
public class MD5Util {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteArrayToHexString(byte b[]) {

        StringBuilder resultSb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {

        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 返回大写 MD5
     *
     * @param origin      原始字符串
     * @param charsetname 加密算法使用的字符集
     * @return 加密后的字符串
     */
    private static String MD5Encode(String origin, String charsetname) {

        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");

            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception e) {
            logger.error("MD5 加密异常", e);
        }

        return resultString.toUpperCase();
    }

    /**
     * utf-8 编码，对上面的方法进行封装，并且增加了 salt 值
     *
     * @param origin 原始字符串
     * @return 加密后的字符串
     */
    public static String MD5EncodeUtf8(String origin) {

        origin = origin + PropertiesUtil.getProperty("password.salt", ""); // MD5 加盐
        return MD5Encode(origin, "utf-8");
    }

}
