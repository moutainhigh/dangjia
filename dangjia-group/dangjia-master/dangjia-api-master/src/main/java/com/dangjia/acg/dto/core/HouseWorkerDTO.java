package com.dangjia.acg.dto.core;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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

}
