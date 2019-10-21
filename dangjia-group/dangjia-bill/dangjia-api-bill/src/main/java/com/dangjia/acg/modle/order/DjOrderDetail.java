package com.dangjia.acg.modle.order;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * Date: 8/10/2019
 * Time: 下午 2:23
 */
@Data
@Entity
@Table(name = "dj_order_detail")
@ApiModel(description = "订单详情表")
@FieldNameConstants(prefix = "")
public class DjOrderDetail extends BaseEntity {

    @Column(name = "order_id")
    @Desc(value = "订单ID")
    @ApiModelProperty("订单ID")
    private String orderId;//订单ID

    @Column(name = "product_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String productId;//商品ID

    @Column(name = "storefont_id")
    @Desc(value = "店铺ID")
    @ApiModelProperty("店铺ID")
    private String storefontId;//店铺ID

    @Column(name = "activity_red_pack_id")
    @Desc(value = "优惠卷ID")
    @ApiModelProperty("优惠卷ID")
    private String activityRedPackId;//优惠卷ID

    @Column(name = "purchase_price")
    @Desc(value = "购买单价")
    @ApiModelProperty("购买单价")
    private Double purchasePrice;//购买单价

    @Column(name = "purchase_number")
    @Desc(value = "购买数量")
    @ApiModelProperty("购买数量")
    private Double purchaseNumber;//购买数量

    @Column(name = "total_purchase_price")
    @Desc(value = "购买总价")
    @ApiModelProperty("购买总价")
    private Double totalPurchasePrice;//购买总价

    @Column(name = "discount_price")
    @Desc(value = "优惠价钱")
    @ApiModelProperty("优惠价钱")
    private Double discountPrice;//优惠价钱

    @Column(name = "actual_payment_price")
    @Desc(value = "实付价钱")
    @ApiModelProperty("实付价钱")
    private Double actualPaymentPrice;//实付价钱

    @Column(name = "stevedorage_cost")
    @Desc(value = "搬运费")
    @ApiModelProperty("搬运费")
    private Double stevedorageCost;//搬运费

    @Column(name = "transportation_cost")
    @Desc(value = "运费")
    @ApiModelProperty("运费")
    private Double transportationCost;//运费

    @Column(name = "required_number")
    @Desc(value = "已要货数量")
    @ApiModelProperty("已要货数量")
    private Double requiredNumber;//已要货数量

    @Column(name = "shipment_number")
    @Desc(value = "已发货数量")
    @ApiModelProperty("已发货数量")
    private Double shipmentNumber;//已发货数量

    @Column(name = "order_type")
    @Desc(value = "订单类型（1设计，2精算，3其它）")
    @ApiModelProperty("订单类型（1设计，2精算，3其它）")
    private String orderType;//订单类型（1设计，精算，2其它）

    @Column(name = "is_reservation_deliver")
    @Desc(value = "是否预约发货(1是，0否）")
    @ApiModelProperty("是否预约发货(1是，0否）")
    private String isReservationDeliver;//是否预约发货(1是，0否）

    @Column(name = "reservation_deliver_time")
    @Desc(value = "预约发货时间")
    @ApiModelProperty("预约发货时间")
    private String reservationDeliverTime;//预约发货时间

    @Column(name = "order_status")
    @Desc(value = "订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）")
    @ApiModelProperty("订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）")
    private String orderStatus;//订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）

    @Column(name = "create_by")
    @Desc(value = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;//创建人

    @Column(name = "update_by")
    @Desc(value = "修改人")
    @ApiModelProperty("修改人")
    private String updateBy;//修改人

}
