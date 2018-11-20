package com.dangjia.acg.api;


import com.dangjia.acg.common.util.ProtoStuffSerializerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by QiYuXiang on 2018/3/20.
 */
@Component
public class RedisClient {
    private static int count = 0 ;
    private static final Object obj = new Object();

    @Autowired
    private com.dangjia.acg.api.CacheServiceAPI cacheServiceAPI;

    /***
     * put
     * @param key key
     * @param obj obj

     * @return
     */
    public <T> boolean put( String key,  T obj){

        byte [] bValue = ProtoStuffSerializerUtil.serialize(obj);

        return  cacheServiceAPI.put(key,bValue);
    }

    public  boolean put( String key,  byte[] obj){


        return  cacheServiceAPI.put(key,obj);
    }



    /***
     * put
     * @param key
     * @param obj
     * @param expireTime
     * @param <T>
     */

    public <T> void putCacheWithExpireTime(String key, T obj, final long expireTime){

        byte []bValue = ProtoStuffSerializerUtil.serialize(obj);
        cacheServiceAPI.putCacheWithExpireTime(key,bValue,expireTime);
    }

    /***
     * 集合put
     * @param key
     * @param objList
     * @param <T>
     * @return
     */
    public <T> boolean putListCache(String key, List<T> objList){

        byte [] bValue = ProtoStuffSerializerUtil.serializeList(objList);
        return cacheServiceAPI.putListCache(key,bValue);
    }

    public boolean putListCacheStr(String key,List<String> objList){
        return cacheServiceAPI.putListCacheStr(key,objList);
    }

    /***
     * 集合put
     * @param key
     * @param objList
     * @param <T>
     * @return
     */
    public <T> boolean putListCaches(String key, List<T> objList){
        byte [] bValue = ProtoStuffSerializerUtil.serializeList(objList);
        return cacheServiceAPI.putListCaches(key,bValue);
    }

//
    /***
     * put
     * @param key
     * @param objList
     * @param expireTime
     * @param <T>
     * @return
     */
    public <T> boolean putListCacheWithExpireTime(String key, List<T> objList, final long expireTime){

        byte [] bValue = ProtoStuffSerializerUtil.serializeList(objList);
        return cacheServiceAPI.putListCacheWithExpireTime(key,bValue,expireTime);
    }
//
//
    /***
     *
     * @param key
     * @return
     */
    public <T> T getCache(String key,Class<T> targetClass){

        byte [] value = cacheServiceAPI.getCache(key);
        if(value == null || value.length == 0){
            return null;
        }
        return ProtoStuffSerializerUtil.deserialize(value,targetClass);

    }

//    public static <T> T  get1(String key,Class<T> targetClass){
//
//        byte [] value = cacheServiceAPI.getCache(key);
//
//        return ProtoStuffSerializerUtil.deserialize(value,targetClass);
//
//    }

    /***
     * 批量查询
     * @param key
     * @param targetClass
     * @param <T>
     * @return
     */
    public <T> List<T> getListCache(final String key, Class<T> targetClass){

        byte [] value = cacheServiceAPI.getCache(key);
        if(value == null || value.length == 0){
            return null;
        }
        return ProtoStuffSerializerUtil.deserializeList(value,targetClass);
    }

    public <T> T getLikeListCache(String key,Class<T> targetClass){

        synchronized (obj) {
          byte[] value = cacheServiceAPI.getLikeListCache(key);
          if (value == null || value.length == 0) {
            return null;
          }
          return ProtoStuffSerializerUtil.deserialize(value, targetClass);
        }
    }
    /***
     * 删除
     * @param key
     */
    public void deleteCache(String key){

        cacheServiceAPI.deleteCache(key);
    }


    /***
     * 判断是否存在
     * @param key
     * @return
     */
    public boolean exists(String key){
        return cacheServiceAPI.exists(key);
    }

    /***
     * 模糊匹配删除
     * @param pattern
     */
    public void deleteCacheWithPattern(String pattern){
        cacheServiceAPI.deleteCacheWithPattern(pattern);
    }


    /***
     * 时间更新
     * @param key
     * @param timeout
     * @return
     */
    public boolean expire(String key,Long timeout){
        return cacheServiceAPI.expire(key,timeout);
    }


    /**
     * 分页查询
     * @param key
     * @param isAlpha
     * @param pageNow
     * @param pageSize
     * @param <T>
     * @return
     */
    public <T> List<T> sortPageList(String key,boolean isAlpha,Integer pageNow,Integer pageSize){
        if (pageNow == null){
            pageNow = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
      return cacheServiceAPI.sortPageList(key,isAlpha,(pageNow-1) * pageSize,pageSize);
    }


}
