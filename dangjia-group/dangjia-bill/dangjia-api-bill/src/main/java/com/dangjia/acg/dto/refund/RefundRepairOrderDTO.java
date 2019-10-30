package com.dangjia.acg.dto.refund;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RefundRepairOrderDTO {

    private  String repairMendOrderId;//退款申请单ID

    private String storefrontId;//店铺ID

    private String houseId;//房子ID

    private String repairOrderNum;//退款单号

    private String storefrontName;//店铺名称

    private Date applyDate;//退款申请时间

    private String applyMemberId;//申请人ID

    private String applyMemberName;//申请人名称

    private Double actualTotalAmount;//退款总价

    private Double totalAmount;//退款货物总价（不含运费）

    private String state;//退款状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回）

    private Double totalStevedorageCost;//退款总运费

    private Double carriage;//搬运费

    private String stateName;//退款状态名称

    private int repairProductCount;//退款商品总件数

    private String repairProductImageArr;;//退款商品图片（前两件商品图片）

    private String repairProductName;//退款商品名称（一个商品时才有）

    private List<RefundRepairOrderMaterialDTO> orderMaterialList;

    private List<OrderProgressDTO> orderProgressList;
}
