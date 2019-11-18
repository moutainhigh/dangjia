package com.dangjia.acg.dto.storefront;

import lombok.Data;

@Data
public class StoreExpenseRecordDTO {

    private String houseOrderId;//要货单号
    private String money;//实际收入金额
    private String totalAmount;//订单金额

}
