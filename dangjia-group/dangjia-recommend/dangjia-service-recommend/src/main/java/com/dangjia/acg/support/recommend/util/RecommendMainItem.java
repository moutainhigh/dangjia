package com.dangjia.acg.support.recommend.util;

/**
 * @Description: 推荐主项
 * @author: luof
 * @date: 2020-3-12
 */
public enum RecommendMainItem {

    default_item("1","默认项"),
    worker_type("2", "工序");

    private String itemId;

    private String desc;

    private RecommendMainItem(String itemId, String desc) {
        this.itemId = itemId;
        this.desc = desc;
    }

    public static RecommendMainItem getInstance(String itemId) {
        for (RecommendMainItem entity : RecommendMainItem.values()) {
            if (entity.itemId.equals(itemId)) {
                return entity;
            }
        }
        return null;
    }

    public String getItemId() {
        return itemId;
    }
    public String getDesc() {
        return desc;
    }
}
