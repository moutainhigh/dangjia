package com.dangjia.acg.common.enums;

/**
 * @author: QiYuXiang
 * @date: 2018/4/26
 */
public enum  InformationEnum implements IBaseEnum{
  TEXT(0, "text"),
  IMAGE(1, "image"),
  LINK(2, "link"),
  MINIPROGRAMPAGE(3, "miniprogrampage");

  public static AppType getInstance(int code) {
    for (AppType entity : AppType.values()) {
      if (entity.getCode() == code) {
        return entity;
      }
    }
    return null;
  }

  InformationEnum(int code, String desc){
    this.code = code;
    this.desc = desc;
  }

  private int code;

  private String desc;

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public String getDesc() {
    return desc;
  }
}
