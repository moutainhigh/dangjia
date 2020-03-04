package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/10/2019
 * Time: 上午 10:43
 */
@Data
public class AppointmentDTO {
    private String productId;
    private String orderItemId;
    private String storefrontId;
    private String productName;
    private String productSn;
    private String productType;
    private Double price;
    private String image;
    private Double shopCount;
    private Double askCount;
    private Double returnCount;
    private Double surplusCount;
    private Date reservationDeliverTime;//预约发货时间
    private String unitName;
    private String orderSplitItemId;
    private String orderSplitId;
    private String orderId;
    private Double totalPrice;
    private String valueNameArr;
    private String valueIdArr;
    private String orderStatus;
    private Integer applyStatus;
    private Integer isReservationDeliver;
    private String shippingState;//按钮状态 3:付款 4：取消订单 5：预约发货
    private String shippingType;// 订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）
    private Date createDate;
    private String orderNumber;
    private String priceUnitName;
}
