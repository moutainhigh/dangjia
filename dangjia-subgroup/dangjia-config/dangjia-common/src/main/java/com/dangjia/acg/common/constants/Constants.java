package com.dangjia.acg.common.constants;

/**
 * 公共系统常量类
 */
public final class Constants {


  /**
   * 加密密钥
   */
  public static final String DANGJIA_SESSION_KEY = "dangjia201812345";

  /**
   * 加密iv
   */
  public static final String DANGJIA_IV = "0102030405060708";

  /**
   * 成功信息字符串
   */
  public static final String SUCCESS = "success";

  /**
   * 错误信息字符串
   */
  public static final String ERROR = "error";

  /** session中user对象 **/
  public static final String SESSIONUSERID = "_JSESSIONID";



  /**
   * 缓存key
   */
  public static final String REDIS_SHIRO_CACHE = "shiro-cache:";

  /**
   * 会话过期时间
   */
  public static final long SESSION_EXPIRE_TIME = 1800;

  /**
   * AuthSessionkey
   */
  public static final String AUTH_SESSION = "auth_session";

  /**
   * config配置Key
   */
  public static final String CONFIG_KEY = "config_";
  /**
   * 临时配置Token
   */
  public static final String TEMP_TOKEN = "temp-token:";
  /**
   * 短信验证码Code
   */
  public static final String SMS_CODE = "sms-code:";

  /**
   * 用户ID
   **/
  public static final String USERID = "user_id";
  /**
   * 前端用户token
   **/
  public static final String USER_TOKEY = "userToken";

  /**
   * 前端城市ID
   **/
  public static final String CITY_ID = "cityId";
  /**
   * 用户ID
   **/
  public static final String USER_KEY = "user_";

}
