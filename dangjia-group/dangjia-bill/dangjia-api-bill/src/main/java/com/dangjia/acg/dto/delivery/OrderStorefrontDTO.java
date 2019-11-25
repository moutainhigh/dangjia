package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

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
    private String storefrontLogo;
    private Integer productCount;
    private String productImageArr;//商品图片（前两件商品图片）
    private String productName;//退款商品名称（一个商品时才有）
    private Date createDate;
    private Double actualPaymentPrice;//实付总价
    private Double totalAmount;//总价（不含运费）
}
