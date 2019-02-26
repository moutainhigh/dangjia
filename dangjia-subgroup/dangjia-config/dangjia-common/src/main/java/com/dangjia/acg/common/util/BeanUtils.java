package com.dangjia.acg.common.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author QiYuXiang
 * @date 2016年8月25日 下午3:06:07
 * @description 实体bean跟map互转
 * @modifyBy 修改人
 * @modifyDate 修改时间
 * @modifyNote 修改说明
 */
public class BeanUtils {
  private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

  public static ObjectMapper mapper = new ObjectMapper();

  public static Object getResult(Object result){
    Object resultValue =null;
    if(result==null){
        return resultValue;
    }
    if (result instanceof Collection) {
      Collection<?> lstObj = listToMap((Collection<?>) result);
      resultValue=lstObj;
    }else if(result.getClass().isArray()){
      resultValue=result;
    } else if (result instanceof Map) {
      Map<Object, Object> lstObj = mapToMap((Map<Object, Object>) result);
      resultValue=lstObj;
    }else if (result instanceof Date) {
      Date date=(Date) result;
      resultValue=date.getTime();
    } else {
      if (result instanceof BigDecimal ||result instanceof String || result instanceof Boolean || result instanceof Byte || result instanceof Short || result instanceof Integer || result instanceof Long || result instanceof Float
              || result instanceof Double || result instanceof Enum) {
        if (result != null) {
          if (result instanceof BigDecimal) {
            BigDecimal  value =((BigDecimal) result).setScale(2,BigDecimal.ROUND_HALF_UP);
            resultValue = String.valueOf(value.doubleValue());
          }else  if (result instanceof String && Validator.isMobileNo((String)result)) {
            resultValue = ((String)result).replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
          }else {
            resultValue = result;
          }
        }
      } else {
        resultValue = beanToMap(result);
      }
    }
    return resultValue;
  }
  /**
   * Bean转Map 忽略_ignore 结尾的字段
   *
   * @param obj
   * @return
   */
  public static Map beanToMap(Object obj) {
    try {
      Class type = obj.getClass();
      Map returnMap = new HashMap();
      BeanInfo beanInfo = Introspector.getBeanInfo(type);

      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      for (int i = 0; i < propertyDescriptors.length; i++) {
        PropertyDescriptor descriptor = propertyDescriptors[i];
        String propertyName = descriptor.getName();
        if (!propertyName.equals("class")) {
          Method readMethod = descriptor.getReadMethod();
          if(readMethod==null){
            continue;
          }
          Object result = readMethod.invoke(obj, new Object[0]);
          // 有忽略标识的字段不转换
          if (propertyName.indexOf("_ignore") >= 0 || result == null) {
            continue;
          }
          //判断是否为 基础类型 String,Boolean,Byte,Short,Integer,Long,Float,Double
          //判断是否集合类，COLLECTION,MAP
          returnMap.put(propertyName, getResult(result));
        }
      }
      return returnMap;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  /**
   * map转Bean
   *
   * @param clazz
   * @param map
   * @param <T>
   * @return
   */
  public static <T> T mapToBean(Class<T> clazz, Map map) {
    try {
      T bean = clazz.newInstance();
      org.apache.commons.beanutils.BeanUtils.populate(bean, map);
      return bean;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Bean转Map
   *
   * @param obj
   * @return
   */
  public static Map beanToMapAll(Object obj) {
    try {
      Class type = obj.getClass();
      Map returnMap = new HashMap();
      BeanInfo beanInfo = Introspector.getBeanInfo(type);
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      for (int i = 0; i < propertyDescriptors.length; i++) {
        PropertyDescriptor descriptor = propertyDescriptors[i];
        String propertyName = descriptor.getName();
        if (!propertyName.equals("class")) {
          Method readMethod = descriptor.getReadMethod();
          if(readMethod==null){
            continue;
          }
          Object result = readMethod.invoke(obj, new Object[0]);
          // 有忽略标识的字段不转换
          if (result == null) {
            continue;
          }
          //判断是否为 基础类型 String,Boolean,Byte,Short,Integer,Long,Float,Double
          //判断是否集合类，COLLECTION,MAP
          returnMap.put(propertyName, getResult(result));
        }
      }
      return returnMap;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public static Map<Object, Object> mapToMap(Map<Object, Object> orignMap) {
    Map<Object, Object> resultMap = new HashMap<Object, Object>();
    for (Entry<Object, Object> entry : orignMap.entrySet()) {
      Object resultKey = getResult(entry.getKey());
      Object resultValue =  getResult(entry.getValue());
      resultMap.put(resultKey, resultValue);
    }
    return resultMap;
  }


  public static Collection listToMap(Collection lstObj) {
    ArrayList arrayList = new ArrayList();
    for (Object t : lstObj) {
      arrayList.add(getResult(t));
    }
    return arrayList;
  }

  public static void beanToBean(Object srcBeanObject, Object destBeanObject) {
    BeanWrapperImpl srcBean = new BeanWrapperImpl(srcBeanObject);
    BeanWrapperImpl destBean = new BeanWrapperImpl(destBeanObject);
    PropertyDescriptor[] destDesc = destBean.getPropertyDescriptors();
    try {
      for (int i = 0; i < destDesc.length; ++i) {
        String name = destDesc[i].getName();
        if ((!(destBean.isWritableProperty(name))) || (!(srcBean.isReadableProperty(name))))
          continue;
        Object srcValue = srcBean.getPropertyValue(name);
        if (srcValue != null) {
          destBean.setPropertyValue(name, srcValue);
        }
      }
    } catch (Exception e1) {
      logger.debug(e1.getMessage(), e1);
    }
  }

  /**
   * json 转 Map
   * @param jsonStr
   * @return
   */
  public static Map<String, Object> jsonToMap(String jsonStr) {
    try {
      if ("".equals(jsonStr)) {
        return null;
      }
      Map<String, Object> responseMap = mapper.readValue(jsonStr, HashMap.class);
      logger.debug(responseMap.toString());
      return responseMap;
    } catch (JsonParseException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
