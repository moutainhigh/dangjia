package com.dangjia.acg.common.util;

import com.dangjia.acg.common.constants.RegConstants;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.BaseEntity;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共Service校验器,参数错误时统一抛出IllegalArgumentException
 *
 * @author QiYuXiang
 */
public class Validator extends Assert {

  private static final String CONTAIN_ZH_CN = "[\u4e00-\u9fa5]";
  private static final String FULL_ZH_CN = ".*[\u4e00-\u9fff]+.*";
  /***
   * log
   */
  private static Logger logger = Logger.getLogger(Validator.class);

  public static void hasText(String text, String message) {
    if (!StringUtils.hasText(text)) {
      throw new BaseException(ServerCode.ILLEGAL_ARGUMENT_ERROR, message);
    }
  }


  /***
   * 校验继承baseEntity实体
   *
   * @param entity 实体
   */
  public static void validateEntity(BaseEntity entity) {
    Map<String, String> result = EntityValidator.validate(entity);
    if (null != result && result.size() > 0) {
      BaseException exception = new BaseException(ServerCode.SERVER_ENTITY_VALIDATE_FAIL);
      exception.setAttachment(result);
      throw exception;
    }
  }


  /***
   * 校验枚举值
   *
   * @param code     编码
   * @param enumName 枚举名
   * @param message  异常输出
   */
  public static void inEnum(Integer code, String enumName, String message) {
    if (null == code) {
      throw new IllegalArgumentException(message);
    }
    Class<?> enumClazz = null;
    Object result = null;
    try {
      enumClazz = Class.forName("com.hs.dangjia.common.enums." + enumName);
      result = ClazzUtil.invokeStatic(enumClazz, "getInstance", new Class[] {int.class}, new Object[] {code});
    } catch (Exception e) {
      logger.error("", e);
      throw new BaseException(ServerCode.SERVER_ENUM_EXCEPTION);
    }
    if (null == result) {
      throw new IllegalArgumentException(message);
    }
  }

  /***
   * 大于
   *
   * @param value   待比较的值
   * @param goal    被比较的值
   * @param message 异常信息输出
   */
  public static void gt(int value, int goal, String message) {
    if (value <= goal) {
      throw new IllegalArgumentException(message);
    }
  }


  /***
   * 小于
   *
   * @param value   待比较的值
   * @param goal    被比较的值
   * @param message 异常信息输出
   */
  public static void lt(int value, int goal, String message) {
    if (value >= goal) {
      throw new IllegalArgumentException(message);
    }
  }

  /***
   * 大于
   *
   * @param value   待比较的值
   * @param goal    被比较的值
   * @param message 异常信息输出
   */
  public static void gt(long value, long goal, String message) {
    if (value < goal) {
      throw new IllegalArgumentException(message);
    }
  }

  /***
   * 在数值区间内(startR-区间起点,endR-区间终点)
   *
   * @param value   待校验值
   * @param startR  起点
   * @param endR    结束点
   * @param message 异常信息
   */
  public static void inRange(int value, int startR, int endR, String message) {
    if (value < startR || value > endR) {
      throw new IllegalArgumentException(message);
    }
  }

  /***
   * 在数值区间内(startR-区间起点,endR-区间终点)
   *
   * @param value   待校验值
   * @param startR  起点
   * @param endR    结束点
   * @param message 异常信息
   */
  public static void inRange(long value, long startR, long endR, String message) {
    if (value < startR || value > endR) {
      throw new IllegalArgumentException(message);
    }
  }

  /***
   * 在数值区间内(startR-区间起点,endR-区间终点)
   *
   * @param value   待校验值
   * @param startR  起点
   * @param endR    结束点
   * @param message 异常信息
   */
  public static void inRange(float value, float startR, float endR, String message) {
    if (value < startR || value > endR) {
      throw new IllegalArgumentException(message);
    }
  }

  /***
   * 在数值区间内(startR-区间起点,endR-区间终点)
   *
   * @param value   待校验值
   * @param startR  起点
   * @param endR    结束点
   * @param message 异常信息
   */
  public static void inRange(double value, double startR, double endR, String message) {
    if (value < startR || value > endR) {
      throw new IllegalArgumentException(message);
    }
  }


  /****
   * 至少一项非空
   *
   * @param values  待校验数组
   * @param message 异常信息输出
   */
  public static void atLeastOneText(String[] values, String message) {
    if (null == values || values.length == 0) {
      throw new IllegalArgumentException(message);
    }
    for (String value : values) {
      if (StringUtils.hasText(value)) {
        return;
      }
    }
    throw new IllegalArgumentException(message);
  }

  /****
   * 验证是否是手机
   *
   * @param mobiles 手机
   * @param message 异常信息输出
   */
  public static void isMobileNO(String mobiles, String message) {
    if (!isMobileNo(mobiles)) {
      throw new IllegalArgumentException(message);
    }
  }

  /****
   * 验证是否是手机
   *
   * @param mobile 手机
   * @return true/false
   */
  public static boolean isMobileNo(String mobile) {
    String regex = "(1[0-9]{2}){1}\\d{8}";
    return mobile.matches(regex);
  }
  private static final Pattern SIMPLE_PASSWORD = Pattern
          .compile("^[0-9_a-zA-Z]{6,20}$");
  public static boolean isSimplePassword(String str) {
    return !StringUtils.isEmpty(str) && SIMPLE_PASSWORD.matcher(str).matches();
  }

  /***
   * 验证帐号
   *
   * @param str 待校验信息
   */
  public static void pattern(String str, String reg, ServerCode e) {

    if (!str.matches(reg)) {
      throw new BaseException(e);
    }
  }



  /****
   * 验证是否为MD5格式
   *
   * @param md5Code md5编号
   * @param message 异常信息输出
   */
  public static void isMD5Code(String md5Code, String message) {
    String regex = "[0-9a-z]{32}$";
    if (!md5Code.matches(regex)) {
      throw new IllegalArgumentException(message);
    }
  }


  /***
   * 验证是否为name格式
   *
   * @param name    name
   * @param message 异常信息输出
   */
  public static void isName(String name, String message) {
    String regex = "^[a-zA-Z0-9]{4,20}";
    if (!name.matches(regex)) {
      throw new IllegalArgumentException(message);
    }
  }

  /***
   * 是否指定格式的日期字符串
   *
   * @param date    日期
   * @param regex   正则
   * @param message 异常信息输出
   */
  public static void isDateFormat(String date, String regex, String message) {
    hasText(date, message);
    if (!date.matches(regex)) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * 是否是正的int类型
   *
   * @param inte    待校验
   * @param message 异常信息
   */
  public static int isPlusInt(String inte, String message) {
    String regex = "^([+]?)([0-9]+)$";
    if (!inte.matches(regex)) {
      throw new IllegalArgumentException(message);
    }
    return NumberUtils.parseNumber(inte, Integer.class);
  }

  /**
   * 是否是int类型
   *
   * @param inte    待校验
   * @param message 异常信息
   */
  public static int isInt(String inte, String message) {
    String regex = "^([+-]?)([0-9]+)$";
    if (!inte.matches(regex)) {
      throw new IllegalArgumentException(message);
    }
    return NumberUtils.parseNumber(inte, Integer.class);
  }

  /**
   * 是否是long类型
   *
   * @param longe   待校验
   * @param message 异常信息
   */
  public static long isLong(String longe, String message) {
    String regex = "^([+-]?)([0-9]+)$";
    if (!longe.matches(regex)) {
      throw new IllegalArgumentException(message);
    }
    return NumberUtils.parseNumber(longe, Long.class);
  }


  /***
   * 是否是float类型
   *
   * @param flt     待校验
   * @param message 异常输出
   * @return float
   */
  public static float isFloat(String flt, String message) {
    String regex = "^([+-]?)\\d*(\\.\\d{1,8})?$";
    if (!flt.matches(regex)) {
      throw new IllegalArgumentException(message);
    }
    return NumberUtils.parseNumber(flt, Float.class);
  }


  /***
   * 是否包含中文
   *
   * @param str     待校验
   * @param message 异常输出
   */
  public static void isContainsChinese(String str, String message) {
    Matcher matcher = Pattern.compile(CONTAIN_ZH_CN).matcher(str);
    if (matcher.find()) {
      throw new IllegalArgumentException(message);
    }
  }

  /***
   * 是否全是中文
   *
   * @param str     待校验
   * @param message 异常输出
   */
  public static void isChinese(String str, String message) {
    Matcher matcher = Pattern.compile(FULL_ZH_CN).matcher(str);
    if (!matcher.matches()) {
      throw new IllegalArgumentException(message);
    }
  }


  /***
   * 判断timstamp 时间范围是否为合理范围
   *
   * @param eTime   时间类型
   * @param message 异常输出
   */
  public static void isRightTimestamp(Long eTime, String message) {
    SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMAT5);
    try {
      Date start = sdf.parse("19700101000000");
      Date end = sdf.parse("20500101000000");
      if (eTime > end.getTime() || eTime < start.getTime()) {
        throw new IllegalArgumentException(message);
      }
    } catch (ParseException e) {
      throw new IllegalArgumentException("timestamp时间解析异常");
    }

  }

  /***
   * 有
   *
   * @param minSize 最小值
   * @param maxSize 最大值
   * @param text    待测试值
   * @param message 异常输出
   */
  public static void isRightLength(int minSize, int maxSize, String text, String message) {

    if (text.length() > maxSize || text.length() < minSize) {
      throw new IllegalArgumentException(message);
    }

  }

  /***
   * 是否为图片格式
   *
   * @param name    名称
   * @param message 异常输出
   */
  public static void isPicture(String name, String message) {

    if (!name.toLowerCase().matches(RegConstants.IMAGE_URL)) {
      throw new IllegalArgumentException(message);
    }
  }

  /***
   * 是否为excel
   *
   * @param name    名称
   * @param message 异常输出
   */
  public static void isExcel(String name, String message) {

    if (!name.toLowerCase().matches(RegConstants.EXCEL)) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * 是否是空
   *
   * @param oo 对象
   * @return
   */
  public static boolean isNotNull(Object oo) {
    Boolean bol = false;
    if (oo instanceof String) {
      bol = (null != oo && !"".equals(oo)) ? true : false;
    } else {
      bol = null != oo ? true : false;
    }
    return bol;
  }
}
