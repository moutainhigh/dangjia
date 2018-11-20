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


  /**
   * 埋点编码 start
   */
  //用户登录
  public static final String USER_LOGIN = "/member/login";
  //用户登出
  public static final String USER_LOGOUT = "/member/logout";
  //添加用户日志
  public static final String USER_ADDUSERLOG = "/member/addUserLog";


  /**
   * 用户角色 1为业主角色，2为工匠角色，0为业主和工匠双重身份角色
   */
  public static final Integer USER_ROLE_ALL = 0;//业主和工匠双重身份角色
  public static final Integer USER_ROLE_YEZHU = 1;//业主角色
  public static final Integer USER_ROLE_GONGJIANG = 2;//工匠角色



  /**
   * 审核状态,0已经提交等审核，1用户自己撤回，2，已受理，3、受理完成，告知业主，4，业主审核通过，全部完成。
   */
  public static final Integer STATE_TJ = 0;//已经提交等审核
  public static final Integer STATE_CH = 1;//用户自己撤回
  public static final Integer STATE_SL = 2;//已受理
  public static final Integer STATE_GZ = 2;//受理完成，告知业主
  public static final Integer STATE_WC = 2;//业主审核通过，全部完成。
}
