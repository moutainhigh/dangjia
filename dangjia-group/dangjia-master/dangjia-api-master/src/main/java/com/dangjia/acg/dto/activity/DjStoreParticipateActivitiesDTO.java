package com.dangjia.acg.dto.activity;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/20
 * Time: 16:44
 */
@Data
public class DjStoreParticipateActivitiesDTO {

    private Integer activityType;//数据类型 1:限时购，2:拼团购
    private Integer pendingCount;//待处理数
    private Integer registrationNumber;//报名店铺数

    private String  storefrontId;//店铺id
    private String  storefrontName;//店铺名称
    private Integer registrationStatus;//报名状态 1:已报名 2:申请中 3:待审核 4：被打回
    private Integer participatingCommodityQuantity;//参与商品数量
    private String  storeActivityId;//活动id
    private String  storeParticipateActivities;//店铺参与活动id
    private String  activitySessionId;//场次id

}
