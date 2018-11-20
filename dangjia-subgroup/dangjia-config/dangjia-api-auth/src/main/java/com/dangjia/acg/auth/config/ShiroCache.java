package com.dangjia.acg.auth.config;

import com.dangjia.acg.api.CacheServiceAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.util.SerializeUtils;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
//import org.springframework.data.redis.core.RedisTemplate;

@SuppressWarnings("unchecked")

public class ShiroCache<K, V> implements Cache<K,V>{


    private String cacheKey;


    private CacheServiceAPI serviceAPI;



    @SuppressWarnings("rawtypes")
    public ShiroCache(String name,CacheServiceAPI cacheServiceAPI) {
        this.cacheKey = Constants.REDIS_SHIRO_CACHE;
        this.serviceAPI = cacheServiceAPI;

    }

    @Override
    public V get(K key) throws CacheException {


        byte[] rawValue = serviceAPI.getCache((String)getCacheKey(key));
        V value = (V) SerializeUtils.deserialize(rawValue);
        return value;
    }

    @Override
    public V put(K key, V value)  {

        serviceAPI.putCacheWithExpireTime((String)getCacheKey(key), SerializeUtils.serialize(value),Constants.SESSION_EXPIRE_TIME);
        return value;
    }

    @Override
    public V remove(K key) throws CacheException {
        V old = get(key);
        serviceAPI.deleteCache((String)getCacheKey(key));
        return old;
    }

    @Override
    public void clear() throws CacheException {
        System.out.printf("清空所有缓存");
        //// TODO: 2017/9/5
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<K> keys() {
        return (Set<K>) serviceAPI.keys((String)getCacheKey("*"));
    }

    @Override
    public Collection<V> values() {
        Set<K> set = keys();
        List<V> list = new ArrayList<>();
        for (K s : set) {
            list.add(get(s));
        }
        return list;
    }

    private K getCacheKey(Object k) {
        return (K) (this.cacheKey + k);
    }
}
