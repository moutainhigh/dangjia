package com.dangjia.acg.dto.storefront;

import lombok.Data;

import java.util.Date;

@Data
public class StoreExpenseRecordDTO {

    private String orderId;//订单ID
    private String orderNumber;//订单号
    private String shipAddress;//收货地址
    private Date orderGenerationTime;  //时间
    private Double actualPaymentPrice;//实际收入金额
    private Double totalAmount;//订单金额
    private Double totalStevedorageCost;//搬运费
    private Double totalTransportationCost;//运费

}
