package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取属性文件工具类
 */
public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties props;

    // 执行顺序：静态变量 -> 静态代码块 -> 静态方法 -> 成员变量 -> 成员方法 -> 构造代码块 -> 构造方法
    // 加载配置文件
    static {
        String fileName = "mmall.properties"; // 配置文件
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName), "UTF-8"));
        } catch (IOException e) {
            logger.error("加载配置文件异常", e);
        }
    }

    /**
     * 通过配置文件中的 key 获取 value
     *
     * @param key 键
     * @return 值
     */
    public static String getProperty(String key) {

        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    /**
     * 通过配置文件中的 key 获取 value，如果 key 为空，则返回一个默认值
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String getProperty(String key, String defaultValue) {

        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value.trim();
    }

}
