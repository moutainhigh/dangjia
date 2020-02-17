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

    private String taskStackId;//任务表ID
    private String memberId;//用户ID
    private String houseId;//房子ID
    private String date;
    private String htmlUrl;//审核验收页面
    private String name;
    private String image;//图标地址
    /**
     * 浮动任务类型：  1000：url跳转
     *
     *      1001：量房后产生的补差价
     *      1002：（工序）**待支付
     *      1003：（工序）审核抢单
     *      1004：审核精算
     *      1005：审核（工序）补人工
     *
     *
     *       1006：（工序）提醒您退剩余材料
     *       1007：审核设计图（修改）
     *       1008：审核施工图
     *       1009：审核平面图
     *       1010：设计图纸不合格
     *       1011：请查收您的验房结果
     *       1012：（工序）申请验收
     *       1013：您有一笔退人工处理
     *       1014：您有一笔补人工处理
     *
     *
     *       1017：维保申请验收
     *       1018：在（工序）保质期内有维修单
     *       1019：您的剩余保险天数不足请缴纳质保金
     *       1020：在**质保期内有维修单
     *       1021：质保责任划分通知
     *
     *
     */
    private int type;//1支付任务,2补货补人工,3审核验收任务,4大管家审核退,5审核工匠
    private String taskId;//houseFlowId,mendOrderId,houseFlowApplyId
    private String paramVal;//其他所需参数值，统一为JSON对象格式

}
