package com.dangjia.acg.common.exception;


import com.dangjia.acg.common.enums.IBaseEnum;

/**
 * Server服务状态码
 * <p>每个模块的抛出EXCEPTION都在一定的范围内,0至-1000 公共异常    app: -1000至-2000    </p>
 *
 * @author QiYuXiang
 */
public enum ServerCode implements IBaseEnum {

  SUCCESS(1000, "SUCCESS"),
  ERROR(1001, "ERROR"),
  SYM_TOKEN_ERROR(1002, "系统token失效"),
  USER_TOKEN_ERROR(1003, "无效的token,请重新登录或注册"),
  NO_DATA(1004, "查无数据"),
  NEED_TO_PAY(1009, "需要支付"),

  /***/
  WRONG_URL(-1, "URL错误"),

  READ_TIMEOUT(-2, "请求超时"),

  ERROR_POPUP(-3, "通用弹窗code"),

  SERVER_EXCEPTION_NULLPOINTER(-300, "空指针异常"),

  INVALID_PARAMS_CONVERSION(-400, "参数类型非法"),

  PERMISSION_UNLOGIN(-401, "未登录"),
  PERMISSION_DENIED(-402, "权限非法"),

  HTTP_MESSAGE_NOT_READABLE(-410, "HTTP消息不可读"),

  REQUEST_METHOD_NOT_SUPPORTED(-405, "不支持的HTTP请求方法"),

  SERVER_EXCEPTION_REQUEST(-505, "URL访问异常"),


  SERVER_UPGRADE(-900, "系统升级中,该功能请稍后再试"),

  DECODE_ERROR(-601, "解析异常"),

  CLIENT_EXCEPTION(-801, "服务器升级维护中，请稍后再试"),

  DUPLICATE_KEY(-701, "操作过快, 请稍后再试"),

  USER_NOT_EXISTS(-1001, "用户不存在, 请联系管理员"),

  USER_IS_EXISTS(-1002, "用户不存在, 请联系管理员"),
  INVALID_CREDENTIAL(-1002, "用户名或密码错误"),

  USER_STATE_LOCKED(-1003, "该用户已被禁用"),

  SERVER_ENTITY_VALIDATE_FAIL(-10001, "实体校验失败"),

  SERVER_ENUM_EXCEPTION(-10002, "实体类枚举校验失败"),

  ILLEGAL_ARGUMENT_ERROR(-10003, "参数非法"),

  DATA_SAVE_FAIL(-10006, "数据保存失败"),

  USER_NOT_AUTHORIZE(-10005,"用户未授权"),

  AUTHORIZE_MOBILE_ERROR(-10006, "手机号授权失败"),

  WRONG_PARAM(-19999, "错误参数"),

  SERVER_UNKNOWN_ERROR(-99999, "服务端异常, 请稍后再试"),

  THE_NICKNAME_ALREADY_EXISTS(-10004, "昵称已经存在"),

  DATA_ALREADY_EXISTS(-10007,"数据已经存在"),

  UPLODAD_FILE_ERROR(-333,"文件上传失败"),

  FAILURE_TO_MEET_THE_MATCHING_REQUIREMENTS(-2004,"未达到匹配要求"),
  UNIONID_IS_NULL(-2005,"unionId为空"),
  THE_LANDING_TIME_PLEASE_LAND_AGAIN(-2006,"登录超时,请重新登录"),
  THE_OPPONENT_LEAVE_THE_ROOM(-2007,"好友离开房间"),

  JSON_TYPE_ERROR(-3001,"JSON转换异常"),
  ES_ERROR(-3001,"保存ES失败"),
  ACTIVITY_ERROR_EXISTS(-3002,"活动已下架"),
  ALI_PAY_ERROR(-3003,"支付宝获取签名错误"),
  ;

  private int code;

  private String desc;

  ServerCode(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  /***
   * 获取实力
   *
   * @param code 编码
   * @return
   */
  public static ServerCode getInstance(int code) {
    for (ServerCode entity : ServerCode.values()) {
      if (entity.getCode() == code) {
        return entity;
      }
    }
    return null;
  }

  @Override
  public int getCode() {

    return this.code;
  }

  @Override
  public String getDesc() {

    return this.desc;
  }


}
