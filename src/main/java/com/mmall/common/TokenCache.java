package com.mmall.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * token 缓存处理，提高安全性
 * 例如：重置密码的 Token 认证，防止横向越权漏洞
 */
public class TokenCache {

    // 日志
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    // token 前缀
    public static final String TOKEN_PREFIX = "token_";

    // 设置缓存的初始化容量、有效期（12h）
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().initialCapacity(1000)
            .maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {

                // 默认的数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载.
                @Override
                public String load(String arg0) throws Exception {
                    return "null";
                }

            });

    /**
     * 存 token
     *
     * @param key   键
     * @param value 值
     */
    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    /**
     * 取 token
     *
     * @param key 键
     * @return 值
     */
    public static String getKey(String key) {

        String value = null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)) {
                return null;
            }
            return value;
        } catch (ExecutionException e) {
            logger.error("localCache get error", e);
        }

        return null;
    }

}
