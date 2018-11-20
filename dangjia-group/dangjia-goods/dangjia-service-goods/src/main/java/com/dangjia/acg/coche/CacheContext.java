package com.dangjia.acg.coche;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * @author QiYuXiang
 * @version V1.0.0
 * @Mail qiyuxiangjava@qq.com
 * @Date 2018/1/26
 * @Time 下午6:03
 */
@Service
public class CacheContext {
  /**
   * session有效时间
   */
  @Value("${cache.session.timeout}")
  private Integer sessionTimeout;

  public CacheContext() {
    new ClearThread().start();
  }

  /**
   * 缓存
   */
  private static Map<String, CacheMap> cache = new Hashtable<String, CacheMap>();
  private static Logger logger = LoggerFactory.getLogger(CacheContext.class);

  public static Object getCache(String key) {
    if (key == null || "".equals(key)) {
      return null;
    }
    CacheMap cm = cache.get(key);
    if (cm == null) {
      return null;
    }
    cm.setDate(System.currentTimeMillis());
    return cm.getValue();
  }

  /**
   * 赋值
   *
   * @param key
   * @param value
   */
  public static void setCache(String key, Object value) {
    CacheMap cm = new CacheMap();
    cm.setDate(System.currentTimeMillis());
    cm.setKey(key);
    cm.setValue(value);
    logger.info("==> online :" + cache.size());
    if (key != null)
      cache.put(key, cm);
  }

  /**
   * 根据 key 来删除缓存中的一条记录
   *
   * @param key
   */
  public static void evictCache(String key) {
    if (key != null && cache.containsKey(key)) {
      logger.info("remove timeout key: " + key);
      cache.remove(key);
    }
  }

  /**
   * 清空缓存
   */
  public static void evictCache() {
    cache.clear();
  }

  /**
   * 清理失效缓存
   */
  private class ClearThread extends Thread {
    @Override
    public void run() {
      Set<String> cacheKey = new HashSet<String>();
      while (true) {
        cacheKey.addAll(cache.keySet());
        long currentTime = System.currentTimeMillis();
        for (String k : cacheKey) {
          CacheMap cm = cache.get(k);
          if (currentTime - cm.getDate() >= sessionTimeout * 60 * 1000) {
            evictCache(k);
          }
        }
        try {
          Thread.sleep(60000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        cacheKey.clear();
      }
    }
  }
}
