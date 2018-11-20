package com.dangjia.acg.controller;


import com.dangjia.acg.common.util.ProtoStuffSerializerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by QiYuXiang on 2018/3/20.
 */
@RestController
public class CacheController {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${cacheSuffix}")
    private String cacheSuffix;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Map<String,Object> concurrentHashMapList = new HashMap<String,Object>();

    @RequestMapping(value = "put",method = RequestMethod.POST)
    public  boolean put(String key, @RequestParam("obj") byte[] obj) throws UnsupportedEncodingException {
        key = cacheSuffix+key;
        final byte[] bkey = key.getBytes();
        final byte [] bvalue = obj;
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                 connection.set(bkey, bvalue);
                return true;
            }
        });
        return result;
    }


    @RequestMapping(value = "putCacheWithExpireTime",method = RequestMethod.POST)
    public  void putCacheWithExpireTime(String key, byte [] obj, final long expireTime) {
        key = cacheSuffix+key;
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = obj;
        System.out.println("putCacheWithExpireTime:"+key+"次");
        redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setEx(bkey, expireTime, bvalue);
                return true;
            }
        });
    }

    @RequestMapping(value = "putListCache",method = RequestMethod.POST)
    public  boolean putListCache(String key, byte [] objList) {
        key = cacheSuffix+key;
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = objList;
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {

                 connection.set(bkey, bvalue);
                return true;
            }
        });
        return result;
    }

    @RequestMapping(value = "putListCaches",method = RequestMethod.POST)
    public <T> boolean putListCaches(String key,@RequestBody byte [] objList) {
        key = cacheSuffix+key;
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = objList;
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {

                connection.set(bkey, bvalue);
                return true;
            }
        });
        return result;
    }

    @RequestMapping(value = "putListCacheStr",method = RequestMethod.POST)
    public <T> boolean putListCacheStr(String key,@RequestBody List<String> objList) {

        byte [] bObject = ProtoStuffSerializerUtil.serializeList(objList);
        key = cacheSuffix+key;
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = bObject;
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {

                connection.set(bkey, bvalue);
                return true;
            }
        });
        return result;
    }

    @RequestMapping(value = "putListCacheWithExpireTime",method = RequestMethod.POST)
    public  boolean putListCacheWithExpireTime(String key, byte [] objList, final long expireTime) {
        key = cacheSuffix+key;
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = objList;
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {

                connection.setEx(bkey, expireTime, bvalue);
                return true;
            }
        });
        return result;
    }


    @RequestMapping(value = "getCache",method = RequestMethod.POST)
    public byte[] getCache(final String key) {
        final String keys =  cacheSuffix+key;
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(keys.getBytes());
            }
        });
        if (result == null) {
            return null;
        }
        return result;
    }


    @RequestMapping(value = "getLikeListCache",method = RequestMethod.POST)
    public byte [] getLikeListCache(final String key) {
        final String keys =  cacheSuffix+key;
        String value = null;
        String setKey = null;
        Set<String> set = redisTemplate.keys(keys);
        Iterator it = set.iterator();
        while (it.hasNext()){
          Map.Entry entry = (Map.Entry) it.next();
          setKey = String.valueOf(entry.getKey());
          value = String.valueOf(entry.getValue());
          break;
        }
        deleteCache(setKey);
        return value.getBytes();
    }
    @RequestMapping(value = "getListCache",method = RequestMethod.POST)
    public byte [] getListCache(final String key) {
        final String keys =  cacheSuffix+key;
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(keys.getBytes());
            }
        });
        if (result == null) {
            return null;
        }
        return result;
    }

    /**
     * 精确删除key
     *
     * @param key
     */

    @RequestMapping(value = "deleteCache",method = RequestMethod.POST)
    public void deleteCache(String key) {
        key = cacheSuffix+key;
        redisTemplate.delete(key);
    }

    /**
     * 模糊删除key
     *
     * @param pattern
     */
    @RequestMapping(value = "deleteCacheWithPattern",method = RequestMethod.POST)
    public void deleteCacheWithPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);

    }

    @RequestMapping(value = "exists",method = RequestMethod.POST)
    public boolean exists(String key){
        key =  cacheSuffix+key;
        return redisTemplate.hasKey(key);
    }

    @RequestMapping(value = "expire",method = RequestMethod.POST)
    public boolean expire(String key,Long timeout){
        key =  cacheSuffix+key;
        return redisTemplate.expire(key,timeout, TimeUnit.SECONDS);
    }
    @RequestMapping(value = "keys",method = RequestMethod.POST)
    public Set<String> keys(String pattern){

        return redisTemplate.keys(pattern) ;
    }

    @RequestMapping(value = "putMap",method = RequestMethod.POST)
    public void putMap(String key,Object object){
        concurrentHashMapList.put(key,object);
    }

    @RequestMapping(value = "getMap",method = RequestMethod.POST)
    public Object getMap(String key){
       return concurrentHashMapList.get(key);
    }



  @RequestMapping(value = "sortPageList",method = RequestMethod.POST)
  public <T> List<T> sortPageList(String key, boolean isAlpha, Integer off, Integer num) throws  Exception{
      Set<String> keys = redisTemplate.keys(key);
      List<T> list = (List<T>) keys;

      return list;
  }

}
