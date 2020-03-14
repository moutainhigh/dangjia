package com.dangjia.acg.modle.deliver;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 后台生成发货单
 */
@Data
@Entity
@Table(name = "dj_deliver_split_deliver")
@ApiModel(description = "后台生成发货单")
@FieldNameConstants(prefix = "")
public class SplitDeliver extends BaseEntity {

    @Column(name = "number")
    @Desc(value = "订单号")
    @ApiModelProperty("订单号")
    private String number;

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "order_split_id")
    @Desc(value = "要货单id")
    @ApiModelProperty("要货单id")
    private String orderSplitId;

    @Column(name = "total_amount")
    @Desc(value = "发货单总额(销售总额，包含运费、搬运费)")
    @ApiModelProperty("发货单总额")
    private Double totalAmount;

    @Column(name = "delivery_fee")
    @Desc(value = "供应运费")
    @ApiModelProperty("供应商运费")
    private Double deliveryFee;

    @Column(name = "apply_money")
    @Desc(value = "供应商申请结算的价格（供应商成本总额，包含运费、搬运费）")
    @ApiModelProperty("供应商申请结算的价格")
    private Double applyMoney;

    @Column(name = "apply_state")
    @Desc(value = "供应商申请结算的状态：0申请中(待处理)；1不通过(驳回)；2通过(同意),3其它(迁移)")
    @ApiModelProperty("供应商申请结算的状态0申请中(待处理)；1不通过(驳回)；2通过(同意)")
    private Integer applyState;

    @Column(name = "reason")
    @Desc(value = "不同意理由")
    @ApiModelProperty("不同意理由")
    private String reason;

    @Column(name = "ship_name")
    @Desc(value = "收货人姓名")
    @ApiModelProperty("收货人姓名")
    private String shipName;//

    @Column(name = "ship_mobile")
    @Desc(value = "收货手机")
    @ApiModelProperty("收货手机")
    private String shipMobile;//

    @Column(name = "ship_address")
    @Desc(value = "收货地址")
    @ApiModelProperty("收货地址")
    private String shipAddress;//

    @Column(name = "supplier_id")
    @Desc(value = "供应商id")
    @ApiModelProperty("供应商id")
    private String supplierId;//

    @Column(name = "supplier_telephone")
    @Desc(value = "供应商联系电话")
    @ApiModelProperty("供应商联系电话")
    private String supplierTelephone;//

    @Column(name = "supplier_name")
    @Desc(value = "供应商供应商名称")
    @ApiModelProperty("供应商供应商名称")
    private String supplierName;//

    @Column(name = "memo")
    @Desc(value = "附言 可编辑")
    @ApiModelProperty("附言 可编辑")
    private String memo;//

    @Column(name = "supervisor_id")
    @Desc(value = "大管家id")
    @ApiModelProperty("大管家id")
    private String supervisorId;//

    @Column(name = "send_time")
    @Desc(value = "发货时间")
    @ApiModelProperty("发货时间")
    private Date sendTime; //

    @Column(name = "submit_time")
    @Desc(value = "下单时间")
    @ApiModelProperty("下单时间")
    private Date submitTime;

    @Column(name = "rec_time")
    @Desc(value = "收货时间")
    @ApiModelProperty("收货时间")
    private Date recTime;

    @Column(name = "sup_State")
    @Desc(value = "大管家可收货状态(0:大管家不可收货;1:大管家可收货)")
    @ApiModelProperty("大管家可收货状态(0:大管家不可收货;1:大管家可收货)")
    private Integer supState;

    @Column(name = "shipping_state")
    @Desc(value = "配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收,5已结算," +
            "6店铺撤回(只待发货才能撤回)7-待安装 8-已完成）9-拒绝收货 10 -待评价")
    @ApiModelProperty("配送状态")
    private Integer shippingState;

    @Column(name = "image")
    @Desc(value = "收货照片")
    @ApiModelProperty("收货照片")
    private String image;//收货照片 多张

    @Column(name = "operator_id")
    @Desc(value = "操作收货人id")
    @ApiModelProperty("操作收货人id")
    private String operatorId;


    @Column(name = "storefront_id")
    @Desc(value = "店铺ID")
    @ApiModelProperty("店铺ID")
    private String storefrontId;

    @Column(name = "install_name")
    @Desc(value = "安装人姓名")
    @ApiModelProperty("安装人姓名")
    private String installName;

    @Column(name = "install_mobile")
    @Desc(value = "安装人号码")
    @ApiModelProperty("安装人号码")
    private String installMobile;

    @Column(name = "delivery_name")
    @Desc(value = "送货人姓名")
    @ApiModelProperty("送货人姓名")
    private String deliveryName;

    @Column(name = "delivery_mobile")
    @Desc(value = "送货人号码")
    @ApiModelProperty("送货人号码")
    private String deliveryMobile;

    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;

    @Column(name = "receipt_num")
    @Desc(value = "供应商结算单号")
    @ApiModelProperty("供应商结算单号")
    private String receiptNum;

    @Column(name = "address_id")
    @Desc(value = "地址ID")
    @ApiModelProperty("地址ID")
    private String addressId;


    @Column(name = "total_price")
    @Desc(value = "供应商供应商品总额")
    @ApiModelProperty("供应商供应商品总额")
    private Double totalPrice;

    @Column(name = "stevedorage_cost")
    @Desc(value = "供应商搬运费总额")
    @ApiModelProperty("供应商搬运费总额")
    private Double stevedorageCost;

    @Column(name = "complain_status")
    @Desc(value = "部分收货申诉状态，0未处理，1已认可部分收货，2已申请平台申诉，3平台申诉已通过，4平台申诉被驳回")
    @ApiModelProperty("部分收货申诉状态，0未处理，1已认可部分收货，2已申请平台申诉，3平台申诉已通过，4平台申诉被驳回")
    private Integer complainStatus;


    @Column(name = "complain_count")
    @Desc(value = "申诉次数")
    @ApiModelProperty("申诉次数")
    private Integer complainCount;


    @Transient
    private String isDeliveryInstall;//增加是否发货与安装分开的字段
    @Transient
    private Integer isNonPlatformSupperlier;//是否非平台供应商（1是，0否）


}
