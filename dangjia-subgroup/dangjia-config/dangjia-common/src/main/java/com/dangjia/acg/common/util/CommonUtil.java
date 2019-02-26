package com.dangjia.acg.common.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.web.session.HttpServletSession;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.Map.Entry;

/**
 * 公共工具类
 *
 * @author QiYuXiang
 */
public class CommonUtil {

  /**
   * 地球半径
   */
  private static final double EARTH_RADIUS = 6378.137;
  /**
   * 随机范围
   */
  private static final String[] ARY =
      {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
  /***
   * log日志
   */
  private static Logger log = Logger.getLogger(CommonUtil.class);

  /***
   * 随机数
   */
  private static Random random = new Random();

  /***
   * 集合类型判断是否为空
   *
   * @param collection 集合
   * @return
   */
  public static boolean isEmpty(Collection<?> collection) {
    return (null == collection || collection.size() <= 0);
  }

  /***
   * 集合类型判断是否为空
   *
   * @param collection 集合
   * @return
   */
  public static boolean notEmpty(Collection<?> collection) {
    return !isEmpty(collection);
  }

  /***
   * 判断对象是否为空
   *
   * @param o
   * @return
   */
  public static boolean isEmpty(Object o) {
    return null == o;
  }

  /***
   * 判断字符串是否为空
   *
   * @param str
   * @return
   */
  public static boolean isEmpty(String str) {
    return StringUtils.isEmpty(str);
  }

  /**
   * 指定长度,生成随机数
   *
   * @param len 长度
   */
  public static String randomNumber(int len) {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < len; i++) {
      res.append(random.nextInt(len));
    }
    return String.valueOf(res);
  }

  /**
   * 指定长度,生成随机字符串
   *
   * @param len 长度
   */
  public static String randomString(int len) {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < len; i++) {
      res.append(ARY[random.nextInt(ARY.length)]);
    }
    return String.valueOf(res);
  }

  /***
   * 流文件关闭
   *
   * @param entity 文件流实体
   */
  public static void close(Closeable entity) {
    try {
      if (null != entity) {
        entity.close();
        entity = null;
      }
    } catch (Exception e) {
      log.error("关闭异常.", e);
    }
  }

  /***
   * 判断是否为超级管理员 /超管不受实际限制
   *
   * @param account 账号
   * @return true or false
   */
  public static boolean isSys(String account) {
    Validator.hasText(account, "账号不能为空");
    if ("admin".equals(account)) {
      return true;
    }

    return false;
  }


  /***
   * map根据value值升序排序
   *
   * @param map map值
   * @return
   */
  public static ArrayList<Entry<String, Double>> sortMapByvalueAsc(Map<String, Double> map) {
    List<Entry<String, Double>> entries = new ArrayList<Entry<String, Double>>(map.entrySet());
    Collections.sort(entries, new Comparator<Entry<String, Double>>() {
      public int compare(Entry<String, Double> obj1, Entry<String, Double> obj2) {
        return obj1.getValue().compareTo(obj2.getValue());
      }
    });
    return (ArrayList<Entry<String, Double>>) entries;
  }


  /**
   * 保留两位小数点
   *
   * @param f 浮点类型
   */
  public static String changeTwoDecimal(float f) {
    String str = String.valueOf((float) Math.round(f * 100) / 100);
    int pos = str.indexOf(".");
    if (pos < 0) {
      pos = str.length();
      str += ".";
    }
    while (str.length() <= pos + 2) {
      str += '0';
    }
    return str;
  }


  /***
   * 获取访问者ip
   *
   * @param request req
   * @return
   */
  public static String getRequestIP(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }

  /**
   * 获取访问者IP
   * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
   * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
   * 如果还不存在则调用Request .getRemoteAddr()。
   *
   * @param request req
   * @return
   */
  public static String getIpAddr(HttpServletRequest request) {
    String ip = request.getHeader("X-Real-IP");
    if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
      return ip;
    }
    ip = request.getHeader("X-Forwarded-For");
    if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
      // 多次反向代理后会有多个IP值，第一个为真实IP。
      int index = ip.indexOf(',');
      if (index != -1) {
        return ip.substring(0, index);
      } else {
        return ip;
      }
    } else {
      return request.getRemoteAddr();
    }
  }

  /**
   * 获取本机IP地址
   */
  public static String getLocalIp() {
    try {
      Enumeration<?> netInterfaces = NetworkInterface.getNetworkInterfaces();
      InetAddress ip = null;
      while (netInterfaces.hasMoreElements()) {
        NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
        Enumeration<InetAddress> addresses = ni.getInetAddresses();
        while (addresses.hasMoreElements()) {
          ip = (InetAddress) addresses.nextElement();
          if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
            return ip.getHostAddress();
          }
        }
      }
      return null;
    } catch (Exception e2) {
      return null;
    }
  }

  /***
   * 测试ip地址是否可达:ture:可连通，false:无法连通
   *
   * @param address 地址
   * @return
   */
  public static boolean testIp(String address) {
    boolean flag = false;
    Runtime runtime = Runtime.getRuntime(); // 获取当前程序的运行进对象
    String line = null; // 返回行信息
    try {
      Process process = runtime.exec("ping " + address); // PING
      InputStream is = process.getInputStream(); // 实例化输入流
      InputStreamReader isr = new InputStreamReader(is); // 把输入流转换成字节流
      BufferedReader br = new BufferedReader(isr); // 从字节中读取文本
      while ((line = br.readLine()) != null) {
        if (line.contains("TTL")) {
          flag = true;
          break;
        }
      }
      is.close();
      isr.close();
      br.close();
    } catch (IOException e) {
      log.error("测试ip地址连通性错误");
      e.getStackTrace();
      runtime.exit(1);
    }
    return flag;
  }



  /**
   * 获取文件后缀
   *
   * @param path 路径
   */
  public static String getSuffix(String path) {
    if (null == path || path.indexOf(".") < 0) {
      throw new IllegalArgumentException();
    }
    return path.substring(path.lastIndexOf(".") + 1);
  }

  /**
   * 字符串转HTML文本
   *
   * @param content 内容
   */
  public static String stringToHTML(String content) {
    if (content == null) {
      return "";
    }
    String html = content;
    html = html.replace("'", "&apos;");
    html = html.replace("\"", "&quot;");
    html = html.replace("\t", "&nbsp;&nbsp;");
    html = html.replace(" ", "&nbsp;");
    html = html.replace("<", "&lt;");
    html = html.replace(">", "&gt;");
    html = html.replace("\r", "").replace("\n", "");
    return html;
  }



  /***
   * 将Map形式的键值对中的值转换为泛型形参给出的类中的属性值 t一般代表pojo类
   *
   * @param t      对象
   * @param params 参数
   * @param <T>    对象
   * @return
   */
  public static <T extends Object> T flushObject(T t, Map<String, Object> params) {
    if (params == null || t == null) {
      return t;
    }

    Class<?> clazz = t.getClass();
    for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
      try {
        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
          String name = fields[i].getName(); // 获取属性的名字
          Object value = params.get(name);
          if (value != null && !"".equals(value)) {
            // 注意下面这句，不设置true的话，不能修改private类型变量的值
            fields[i].setAccessible(true);
            fields[i].set(t, value);
          }
        }
      } catch (Exception e) {
      }

    }
    return t;
  }

  /**
   * 去除字符串最后一个逗号,若传入为空则返回空字符串
   *
   * @param para 字符
   * @return
   * @descript
   * @version 1.0
   */
  public static String trimComma(String para) {
    if (StringUtils.isNotBlank(para)) {
      if (para.endsWith(",")) {
        return para.substring(0, para.length() - 1);
      } else {
        return para;
      }
    } else {
      return "";
    }
  }


  /***
   * 对象转换
   *
   * @param object object
   * @return
   */
  public static String convert(Object object) {
    if (object == null) {
      return "null";
    } else if (object instanceof String) {
      if (((String) object).length() > 100) {
        return ((String) object).substring(0, 100);
      } else {
        return (String) object;
      }
    } else if (object instanceof Long) {
      return ((Long) object).toString();
    } else if (object instanceof Boolean) {
      return ((Boolean) object).toString();
    } else if (object instanceof Double) {
      return ((Double) object).toString();
    } else if (object instanceof Integer) {
      return ((Integer) object).toString();
    } else if (object instanceof List) {
      return JSON.toJSONString(object);
    } else if (object instanceof HttpServletRequest) {
      return null;
    } else if (object instanceof HttpServletSession) {

      return null;
    } else if (object instanceof HttpServlet) {

      return null;
    } else if (object instanceof HttpSession) {

      return null;
    } else {
      return JSON.toJSONString(object);
    }
  }




  /***
   * 获取script
   *
   * @param name    名称
   * @param content 内容
   * @return
   */
  private static String getScriptByName(String name, String content) {
    String script = content;
    int start = script.indexOf(name);
    script = script.substring(start + name.length() + 2);
    int end = script.indexOf(",");
    script = script.substring(0, end);
    String result = script.replaceAll("'", "");
    return result.trim();
  }

  /**
   * 生成唯一ID
   *
   * @return
   */
  public static String getUniqueId() {
    return randomString(5) + System.currentTimeMillis();
  }

  /**
   * @author QiYuXiang
   * @description 获取文件后缀
   * @date 2018/4/10 下午3:02
   */
  public static String getExtensionName(String filename) {
    if ((filename != null) && (filename.length() > 0)) {
      int dot = filename.lastIndexOf('.');
      if ((dot > -1) && (dot < (filename.length() - 1))) {
        return filename.substring(dot + 1);
      }
    }
    return filename;
  }

  /**
   * 判断字符串真实长度（中文2个字符，英文1个字符）
   *
   * @param value
   * @return
   */
  public static int getStringLength(String value) {
    int valueLength = 0;
    String chinese = "[\u4e00-\u9fa5]";
    for (int i = 0; i < value.length(); i++) {
      String temp = value.substring(i, i + 1);
      if (temp.matches(chinese)) {
        valueLength += 2;
      } else {
        valueLength += 1;
      }
    }
    return valueLength;
  }
}
