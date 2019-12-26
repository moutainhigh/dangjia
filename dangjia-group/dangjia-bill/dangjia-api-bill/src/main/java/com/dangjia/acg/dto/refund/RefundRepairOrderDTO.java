package com.dangjia.acg.dto.refund;

import io.swagger.annotations.ApiModelProperty;
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

    private String storefrontIcon;//店铺图标

    private String storefrontMobile;//店铺电话

    private String mobile;//拨打电话

    private Date applyDate;//退款申请时间

    private String applyMemberId;//申请人ID

    private String applyMemberName;//申请人名称

    private Double actualTotalAmount;//退款总价

    private Double totalAmount;//退款货物总价（不含运费）

    private String imageArr;//相关凭证

    private String imageArrUrl;//相关凭证详情地址

    private String state;//退款状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回）

    private Integer type;//类型：（0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料;5业主退货退款,6系统自动退款）

    private Double totalStevedorageCost;//退款总运费

    private Double carriage;//搬运费

    private String stateName;//退款状态名称

    private int repairProductCount;//退款商品总件数

    private String repairProductImageArr;//退款商品图片（前两件商品图片）

    private String repairProductName;//退款商品名称（一个商品时才有）

    private String repairNewNode;//最新处理节点

    private Date repairNewDate;//最新处理时间

    private int showRepairDateType;//显示时间判断（1订单剩余时间，2最新处理时间）

    private long reparirRemainingTime;//订单剩余处理时间

    private  Double roomCharge;//量房费用

    @ApiModelProperty("可操作编码")
    private String associatedOperation;

    @ApiModelProperty("可操作编码描述")
    private String associatedOperationName;

    private List<RefundRepairOrderMaterialDTO> orderMaterialList;

    private List<OrderProgressDTO> orderProgressList;
}
