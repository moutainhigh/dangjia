package com.dangjia.acg.dto.storefront;

import lombok.Data;

@Data
public class StoreExpenseRecordDTO {

    private String orderNumber;//要货单号
    private String shipAddress;//收货地址
    private String  orderGenerationTime;  //时间
    private String actualPaymentPrice;//实际收入金额
    private String totalAmount;//订单金额



}
