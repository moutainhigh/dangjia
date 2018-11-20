package com.dangjia.acg.dto.core;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 19:57
 * 业主app大铃铛
 */
@Data
public class Task {
    private String date;
    private String htmlUrl;//审核验收页面
    private String name;
    private String image;//图标地址
    private int type;//1支付任务,2补货补人工,3审核验收任务
    private String taskId;//houseFlowId,mendOrderId,houseFlowApplyId
}
