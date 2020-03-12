package com.dangjia.acg.support.recommend.util;

/**
 * @Description: 推荐配置项
 * @author: luof
 * @date: 2020-3-12
 */
public enum RecommendConfigItem {

    recommend_number("recommend_number","推荐条数"),
    default_item_number("default_item_number", "默认参考项条数");

    private String code;

    private String desc;

    private RecommendConfigItem(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RecommendConfigItem getInstance(String code) {
        for (RecommendConfigItem entity : RecommendConfigItem.values()) {
            if (entity.code.equals(code)) {
                return entity;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
}
