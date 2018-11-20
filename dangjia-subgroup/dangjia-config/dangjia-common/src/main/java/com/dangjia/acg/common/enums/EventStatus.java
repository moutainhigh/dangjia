package com.dangjia.acg.common.enums;

/**
 * Created by QiYuXiang on 2018/3/17.
 */
public enum EventStatus implements IBaseEnum {
    SUCCESS(1000, "SUCCESS"),
    ERROR(1001, "ERROR"),
    SYM_TOKEN_ERROR(1002, "系统token失效"),
    USER_TOKEN_ERROR(1003, "userToken失效"),
    NO_DATA(1004, "查无数据");
    public static EventStatus getInstance(int code) {
        for (EventStatus entity : EventStatus.values()) {
            if (entity.getCode() == code) {
                return entity;
            }
        }
        return null;
    }
    EventStatus(int code,String desc){
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
