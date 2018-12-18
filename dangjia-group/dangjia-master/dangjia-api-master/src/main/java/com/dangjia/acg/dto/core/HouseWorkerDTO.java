package com.dangjia.acg.dto.core;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工匠施工列表
 * zmj
 */
@Data
public class HouseWorkerDTO {
    private int workerType;//0大管家;1工匠
    private String houseFlowId;//装修进程id
    private String houseName;//房子名称
    private String houseId;//房子id
    private String houseMemberName;//业主名称
    private String houseMemberPhone;//业主电话
    private Integer allPatrol;//巡查总数
    private BigDecimal alreadyMoney;//已得钱
    private BigDecimal alsoMoney;//还可得钱



    protected String id;

    protected Date createDate;// 创建日期

    protected Date modifyDate;// 修改日期

    protected int dataStatus;

    @ApiModelProperty("工人ID")
    private String workerId;

    @ApiModelProperty("工种ID")
    private String workerTypeId;

    @ApiModelProperty("工人订单")
    private String houseWorkerOrderId;

    @ApiModelProperty("抢单状态:1已抢单等待被采纳,2被换人，3被删除（别人不能再抢）,4已开工被换人,5拒单(工匠主动拒绝)，6被采纳支付,7抢单后放弃")
    private Integer workType;

    @ApiModelProperty("施工状态,默认0  1阶段完工通过，2整体完工通过 ,3待交底,4已放弃")
    private Integer workSteta;

    @ApiModelProperty("业主或大管家对工人的评价状态,0未开始，1已评价，没有评价")
    private Integer evaluateSteta;

    @ApiModelProperty("用于单独判断业主对工人的评价状态,0未开始，1已评价")
    private int hasEvaluate;

    @ApiModelProperty("申请的状态为1 表示没有发起完工申请可退单,默认0")
    private Integer apply;

    @ApiModelProperty("是否选中当前任务(0:未选中；1：选中)")
    private Integer isSelect;

}
