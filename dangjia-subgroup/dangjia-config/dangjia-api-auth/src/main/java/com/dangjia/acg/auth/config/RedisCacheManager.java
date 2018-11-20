package com.dangjia.acg.auth.config;


import com.dangjia.acg.api.CacheServiceAPI;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.springframework.data.redis.core.RedisTemplate;

@Component
public class RedisCacheManager implements CacheManager {


    @Autowired
    private CacheServiceAPI cacheServiceAPI;

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {

        return new ShiroCache<K, V>(name,cacheServiceAPI);
    }

}
