package com.dangjia.acg.common.enums;

/**
 * 应用类型
 *
 * @author: Luof
 * @date: 2020-3-9
 */
public enum RecommendTargetType {

    GOODS(1, "商品"),
    MANUAL(2, "攻略(指南)"),
    CASE(3, "案例"),
    SITE(4, "工地");

    private Integer code;

    private String desc;

    private RecommendTargetType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RecommendTargetType getInstance(Integer code) {
        for (RecommendTargetType entity : RecommendTargetType.values()) {
            if (entity.getCode().intValue() == code.intValue()) {
                return entity;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
}
