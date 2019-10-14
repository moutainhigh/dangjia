package com.dangjia.acg.coche;

/**
 * 缓存类
 * @author QiYuXiang
 * @version V1.0.0
 * @Mail qiyuxiangjava@qq.com
 * @Date 2018/1/26
 * @Time 下午10:29
 */
public class CacheMap {
  private String key;
  private Object value;
  private Long date;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return "CacheMap{" +
        "key='" + key + '\'' +
        ", value=" + value +
        ", date=" + date +
        '}';
  }
}
