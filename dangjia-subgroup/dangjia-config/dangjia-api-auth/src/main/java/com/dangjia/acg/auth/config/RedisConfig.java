package com.dangjia.acg.auth.config;
import org.apache.shiro.cache.CacheManager;


/**
 * redis 配置
 */
public class RedisConfig {

    public CacheManager cacheManager() {
        RedisCacheManager cacheManager = new RedisCacheManager();
        return cacheManager;
    }

}
