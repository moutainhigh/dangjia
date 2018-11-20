package com.dangjia.acg.auth.config;

import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by QiYuXiang on 2017/7/12.
 */
public class ShiroUtil {
  private static Logger logger = LoggerFactory.getLogger(ShiroUtil.class);


  public static <T> T getShiroSession(String key, Class<T> targetClass) {

    T t = null;
    try {
      Object obj = SecurityUtils.getSubject().getSession().getAttribute(key);
      t = (T) obj;
    } catch (Exception e) {
      logger.error("异常:", e);
    }
    return t;

  }

  public static void setShiroSession(Object key, Object value) {
    Subject subject = SecurityUtils.getSubject();
    if (null != subject) {
      Session session = subject.getSession();
      if (null != session) {
        session.setAttribute(key, value);
      }
    } else {
      throw new BaseException(ServerCode.SERVER_UNKNOWN_ERROR);
    }
  }


  public static void removeShiroSession(Object key) {
    Subject subject = SecurityUtils.getSubject();
    if (null != subject) {
      Session session = subject.getSession();
      if (null != session) {
        session.removeAttribute(key);
      }
    } else {
      throw new BaseException(ServerCode.SERVER_UNKNOWN_ERROR);
    }
  }


}
