package com.dangjia.acg.dto.repair;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * author: Ronalcheng
 * Date: 2018/12/18 0018
 * Time: 11:09
 */
@Data
public class MendOrderDTO {

    private String mendOrderId;
    private String number; //订单号
    private String orderName;//描述
    private Date createDate;
    private String houseId;
    private String address;//收货地址
    private String memberName;//业主姓名
    private String memberMobile;//业主电话

    @ApiModelProperty("工种ID")
    private String workerTypeId;

    @ApiModelProperty("申请人id")
    private String applyMemberId;
    private String applyName;//管家姓名
    private String applyMobile;

    @ApiModelProperty("0:补材料;1:补人工;2:退材料;3:退人工,4业主申请退货")
    private Integer type;

    @ApiModelProperty("0生成中,1平台审核中,2不通过,3通过")
    private Integer landlordState;//

    @ApiModelProperty("0生成中,1平台审核中，2平台审核不通过，3平台审核通过待业主支付,4业主已支付，5业主不同意，6管家取消")
    private Integer materialOrderState; //补材料审核状态

    @ApiModelProperty("0生成中,1工匠审核中，2工匠不同意，3工匠同意即平台审核中，4平台不同意,5平台同意即待业主支付，6业主已支付，7业主不同意, 8管家取消")
    private Integer workerOrderState; //补人工审核状态

    @ApiModelProperty("0生成中,1平台审核中，2平台审核不通过，3审核通过，4管家取消")
    private Integer materialBackState; //退材料审核状态

    @ApiModelProperty("0生成中,1工匠审核中，2工匠审核不通过，3工匠审核通过即平台审核中，4平台不同意，5平台审核通过,6管家取消")
    private Integer workerBackState; //退人工审核状态

    private String auditsMemberId; //审核人id

    private Double totalAmount; //订单总额
}
