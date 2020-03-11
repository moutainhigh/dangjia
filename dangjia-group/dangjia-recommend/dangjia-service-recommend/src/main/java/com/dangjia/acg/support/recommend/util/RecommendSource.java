package com.dangjia.acg.support.recommend.util;

/**
 * @Description: 推荐源
 * @author: luof
 * @date: 2020-3-11
 */
public enum RecommendSource {

    browse_goods(0, "浏览商品"),
    browse_case(1, "浏览案例"),
    label_attrib(2, "标签属性");

    private int code;

    private String desc;

    private RecommendSource(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RecommendSource getInstance(int code) {
        for (RecommendSource entity : RecommendSource.values()) {
            if (entity.getCode() == code) {
                return entity;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
}
