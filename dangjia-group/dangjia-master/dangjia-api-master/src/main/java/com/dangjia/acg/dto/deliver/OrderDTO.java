package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/4 0004
 * Time: 11:42
 */
@Data
public class OrderDTO {

    private String orderId;//订单id  订单号
    private String image;
    private String name;//各种名
    private BigDecimal totalAmount;//订单总价
    private String workerTypeName;//流水名

    private String storefrontId;//店铺ID
    private String storefrontName;//店铺名称
    private Double totalStevedorageSost;//总搬运费
    private Double totalTransportationSost;//总运费
    private Double totalPrice;//商品小计
    private Double actualPaymentPrice;//实付总价(应付总额)
    private String discountType;//优惠卷类型（1店铺，2平台）
    private String discountNumber;//优惠卷编号
    private String discountName;//优惠卷名称(方式)
    private Double discountPrice;//优惠卷金额
    private Double totalDiscountPrice;//优惠金额

    List<OrderItemDTO>  orderItemList;//订单明细（商品列表）
}
