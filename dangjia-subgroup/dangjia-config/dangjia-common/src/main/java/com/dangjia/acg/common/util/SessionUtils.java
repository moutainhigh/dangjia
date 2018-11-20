package com.dangjia.acg.common.util;

import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: QiYuXiang
 * @date: 2018/3/27
 */
public class SessionUtils {

  private static Logger logger = LoggerFactory.getLogger(SessionUtils.class);

  /**
   * 读取Session
   * @param key
   * @param <T>
   * @return
   */
  public static <T> T getSession(String key) {
    Object obj = SecurityUtils.getSubject().getSession().getAttribute(key);
    T t = null;
    try {
      t = (T) obj;
    } catch (Exception e) {
      logger.error("异常:", e);
    }
    return t;
  }

  /**
   * 保存Session
   * @param key
   * @param value
   */
  public static void setSession(Object key, Object value) {
    Subject subject = SecurityUtils.getSubject();
    if (null != subject) {
      Session session = subject.getSession();
      if (null != session) {
        session.setAttribute(key, value);
      }
    }else{
      throw new BaseException(ServerCode.SERVER_UNKNOWN_ERROR);
    }
  }

}
