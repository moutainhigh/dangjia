package com.dangjia.acg.dto.core;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/26 0026
 * Time: 20:36
 */
@Data
public class HouseFlowApplyDTO {

    private String houseId;
    private String houseName;
    private String workerId;
    private String houseFlowApplyId;
    private Integer applyType;//0每日完工申请，1阶段完工申请，2整体完工申请, 3大管家申请直接调用业主对大管家审核
    private String applyTypeName;//0每日完工申请，1阶段完工申请，2整体完工申请, 3大管家申请直接调用业主对大管家审核
    private List<String> imageList;//工地图片
    private String date;
    private List<Map> list;//申请进程明细记录
    private Long startDate;
    private Long endDate; //自动审核时间
    private Integer memberCheck;//用户审核结果,0未审核，1审核通过，2审核不通过，3自动审核
    private Integer supervisorCheck;//大管家审核结果,0未审核，1审核通过，2审核不通过

    private Integer withdrawalTime;//业主是否提醒
    private String confirmMsg;//第二次确认消息
    private Integer isPay;//是否需要再次购买 （1是，0否）

    private String supervisorHouseFlowId;
    private String applyDec;
    private String managerId;
    private String headA;//工匠头像
    private String nameA;//工匠名字
    private String mobileA;//工匠电话
    private String workerTypeName;//工种名
    private String workerTypeId;//工种ID
    private String headB;//大管家头像
    private String nameB;//大管家名字
    private String mobileB;//大管家手机

}
