package com.dangjia.acg.modle.delivery;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 9:47
 */
@Data
@Entity
@Table(name = "dj_delivery_return_slip")
@ApiModel(description = "发货退货单")
@FieldNameConstants(prefix = "")
public class DjDeliveryReturnSlip extends BaseEntity {

    @Column(name = "houseId")
    @Desc(value = "房子id")
    @ApiModelProperty("房子id")
    private String houseId;//房子id

    @Column(name = "order_id")
    @Desc(value = "订单ID")
    @ApiModelProperty("订单ID")
    private String orderId;//订单ID

    @Column(name = "sup_id")
    @Desc(value = "供应商ID")
    @ApiModelProperty("供应商ID")
    private String supId;//供应商ID

    @Column(name = "shop_id")
    @Desc(value = "店铺ID")
    @ApiModelProperty("店铺ID")
    private String shopId;//店铺ID

    @Column(name = "order_type")
    @Desc(value = "订单类型：0发货单；1退货单；")
    @ApiModelProperty("订单类型：0发货单；1退货单；")
    private Integer orderType;

    @Column(name = "total_price")
    @Desc(value = "发货商品总价")
    @ApiModelProperty("发货商品总价")
    private Double totalPrice;

    @Column(name = "invoice_status")
    @Desc(value = "货单状态：0待发货；1待收货；2已收货；3待退货；4已退货；5:拒绝退货")
    @ApiModelProperty("货单状态：0待发货；1待收货；2已收货；3待退货；4已退货；5:拒绝退货")
    private String invoiceStatus;

    @Column(name = "booking_delivery_time")
    @Desc(value = "预约发货时间")
    @ApiModelProperty("预约发货时间")
    private Date bookingDeliveryTime;

    @Column(name = "delivery_time")
    @Desc(value = "发货时间")
    @ApiModelProperty("发货时间")
    private Date deliveryTime;

    @Column(name = "shipper")
    @Desc(value = "发货人")
    @ApiModelProperty("发货人")
    private String shipper;

    @Column(name = "ship_name")
    @Desc(value = "收货人姓名")
    @ApiModelProperty("收货人姓名")
    private String shipName;

    @Column(name = "ship_mobile")
    @Desc(value = "收货手机")
    @ApiModelProperty("收货手机")
    private String shipMobile;

    @Column(name = "ship_address")
    @Desc(value = "收货地址")
    @ApiModelProperty("收货地址")
    private String shipAddress;

    @Column(name = "apply_money")
    @Desc(value = "供应商申请结算价格")
    @ApiModelProperty("供应商申请结算价格")
    private String applyMoney;

    @Column(name = "apply_state")
    @Desc(value = "供应商申请结算的状态：0申请中；1不通过；2通过")
    @ApiModelProperty("供应商申请结算的状态：0申请中；1不通过；2通过")
    private String applyState;

    @Column(name = "invoice_type")
    @Desc(value = "货单类型 0:退货单 1:发货单")
    @ApiModelProperty("货单类型 0:退货单 1:发货单")
    private String invoiceType;

    @Column(name = "supervisor_id")
    @Desc(value = "大管家")
    @ApiModelProperty("大管家")
    private String supervisorId;


}
