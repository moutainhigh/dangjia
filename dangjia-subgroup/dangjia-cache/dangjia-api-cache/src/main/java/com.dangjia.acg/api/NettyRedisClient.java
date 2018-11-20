package com.dangjia.acg.api;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author: QiYuXiang
 * @date: 2018/3/29
 */
@Component
public class NettyRedisClient {


  private static  NettyRedisClient nettyRedisClient;

  @PostConstruct
  public void init(){
    nettyRedisClient = this;
  }
  @Autowired
  private CacheServiceAPI cacheServiceAPI;

  /**
   * 存入Map至内存
   * @param key
   * @param obj
   * @param targetClass
   * @param <T>
   * @return
   */
  public static <T> void putMapNetty(String key, Object obj, Class<T> targetClass){
      Map<String,String> paramMap = new ConcurrentHashMap<>();
      Gson gson = new Gson();
      if(obj.getClass() == ConcurrentHashMap.class) {

          Map<String,Object> map =(Map<String,Object>) obj;
          Iterator it = map.entrySet().iterator();
          while (it.hasNext()) {
              Map.Entry entry = (Map.Entry) it.next();
              Object value = entry.getValue();
              String keys  = String.valueOf(entry.getKey());
              if (targetClass == String.class) {
                paramMap.put(keys,String.valueOf(value));
              } else if (targetClass == Long.class) {
                paramMap.put(keys, String.valueOf(value));
              }else if (targetClass == Double.class) {
                paramMap.put(keys, String.valueOf(value));
              }else if (targetClass == Integer.class) {
                paramMap.put(keys, String.valueOf(value));
              } else {
                String jsonValue = gson.toJson(value);
                paramMap.put(keys,jsonValue);
              }
          }
      }
      String jsonValue1 = gson.toJson(paramMap);
     nettyRedisClient.cacheServiceAPI.putMap(key,jsonValue1);
  }

  /**
   * put普通对象
   * @param key
   * @param gson
   * @param <T>
   * @return
   */
  public static <T> void putNetty(String key,Object object){
    String value = null;
    Gson gson = new Gson();
    if (object.getClass() == String.class) {
      value = String.valueOf(object);
    } else if (object.getClass() == Long.class) {
      value = String.valueOf(object);
    }else if (object.getClass() == Double.class) {
      value = String.valueOf(object);
    }else if (object.getClass() == Integer.class) {
      value = String.valueOf(object);
    } else {
      value = gson.toJson(object);
    }
     nettyRedisClient.cacheServiceAPI.putMap(key,value);
  }

  /**
   * 在内存中读取对象
   * @param key
   * @param targetClass
   * @param <T>
   * @return
   */
  public static <T> Object getNetty(String key,Class<T> targetClass){
    Object value  = nettyRedisClient.cacheServiceAPI.getMap(key);
    if(value == null){
      return  null;
    }
    Gson gson = new Gson();
    if (targetClass == String.class) {
       value =  String.valueOf(value);
    } else if (targetClass  == Long.class) {
      value =  Long.valueOf(value.toString());
    }else if (targetClass == Double.class) {
      value =  Double.valueOf(value.toString());
    }else if (targetClass == Integer.class) {
      value =  Integer.valueOf(value.toString());
    } else {
      value = parseJsonWithGson(value.toString(),targetClass);
    }
    return nettyRedisClient.cacheServiceAPI.getMap(key);
  }

  /**
   * 从Redis读取Map
   * @param key
   * @param targetClass
   * @param <T>
   * @return
   */
  public static <T> Object getMapNetty(String key,Class<T> targetClass){

    Map<String,Object> paramMap = new ConcurrentHashMap<>();

    Object value = nettyRedisClient.cacheServiceAPI.getMap(key);
    if(value == null){
      return null;
    }
    Object instance = null;
    Map<String,Object> map = parseJsonWithGson(value.toString(),Map.class);
    Iterator it = map.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry) it.next();
      Object mapValue = entry.getValue();
      String keys  = String.valueOf(entry.getKey());
      if (targetClass == String.class) {
        paramMap.put(keys,String.valueOf(value));
      } else if (targetClass == Long.class) {
        paramMap.put(keys, String.valueOf(value));
      }else if (targetClass == Double.class) {
        paramMap.put(keys, String.valueOf(value));
      }else if (targetClass == Integer.class) {
        paramMap.put(keys, String.valueOf(value));
      } else {
        paramMap.put(keys,parseJsonWithGson(String.valueOf(mapValue), targetClass));
      }
    }
    return paramMap;
  }

  /**
   * 将Json数据解析成相应的映射对象
   */
  public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
    Gson gson = new Gson();
    T result = gson.fromJson(jsonData, type);
    return result;
  }

}
