package com.dangjia.acg.dto.finance;

import lombok.Data;

import java.util.Date;

/**
 * author: ysl
 * Date: 2019/1/25 0018
 * Time: 14:41
 * 供应商发货信息详情
 */
@Data
public class WebSplitDeliverItemDTO {
    private String orderSplitId;//要货单id
    private String splitDeliverId;//发货表 id
    private String supMobile;//供货商电话
    private String supName;//供应商名字
    private String number;//发货单号
    private Double totalAmount;//发货单总额
    private Double applyMoney;//供应商申请结算的价格
    private Integer applyState;//供应商申请结算的状态 0申请中(待处理)；1不通过(驳回)；2通过(同意)
    private String shipAddress;//收货地址
    private String supplierId;//供应商id
    private Date modifyDate;//收货时间
    private Date createDate;// 创建日期
    private String houseId;//房子Id
    private Integer shipState;//供应商发货状态
    private Integer sent; //已处理数
    private Integer wait; //待处理数
}
