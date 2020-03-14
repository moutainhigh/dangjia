package com.dangjia.acg.dto.repair;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
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
    private Date createDate;//申请时间
    private String houseId;
    private String address;//收货地址
    private String memberId;//业主id
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
    private Integer state; //0生成中,1处理中,2不通过取消,3已通过,4已结算
    private Double totalAmount; //订单总额

    @Transient
    private String deliverNumber;//退货单号
    @Transient
    private String supplierName;//供应商名称

}
