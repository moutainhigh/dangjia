package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/10/2019
 * Time: 下午 3:22
 */
@Data
public class OrderStorefrontDTO {

    private String storefrontId;
    private String orderId;
    private String addressId;
    private String houseId;
    private String businessOrderNumber;
    private String orderNumber;
    private String storefrontName;
    private String orderSplitId;
    private String storefrontIcon;
    private Integer productCount;
    private String productImageArr;//商品图片（前两件商品图片）
    private String productName;//退款商品名称（一个商品时才有）
    private Date createDate;
    private Double actualPaymentPrice;//实付总价
    private Double totalAmount;//总价（不含运费）
    private String mobile;//店铺联系电话
    private String shippingState;//按钮状态 3:付款 4：取消订单 5：预约发货
    private String shippingType;// 订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）
    private List<AppointmentDTO> appointmentDTOS;
    private Double totalStevedorageCost;//总搬运费
    private Double totalTransportationCost;//总运费
    private Double totalDiscountPrice;//优惠总价
    private String storefrontType;//店铺类型（实物商品：product，人工商品：worker)
    private String workerName;//工匠名称
    private String workerId;//工人id
    private String workerTypeId;
    private String storeActivityId;//店铺活动id
    private String orderSource;//订单来源(1,工序订单，2购物车，3补货单，4补差价订单，5维修订单 6拼团订单 7限时购订单）
    private String parentOrderId;//父订单ID
    private Integer shortPeople;//拼团还差人数
}
