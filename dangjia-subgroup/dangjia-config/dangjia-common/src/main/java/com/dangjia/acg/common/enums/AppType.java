package com.dangjia.acg.common.enums;

/**
 * 应用类型
 *
 * @author: QiYuXiang
 * @date: 2018-03-24 11:44
 */
public enum AppType implements IBaseEnum {

    ZHUANGXIU(0, "当家装修"),
    GONGJIANG(1, "当家工匠");

    public static AppType getInstance(int code) {
        for (AppType entity : AppType.values()) {
            if (entity.getCode() == code) {
                return entity;
            }
        }
        return null;
    }

    AppType(int code, String desc){
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
