package com.dangjia.acg.dto.storefront;

import lombok.Data;

@Data
public class StoreRevenueRecordDTO {
    private String type;//类型
    private String houseOrderId;// 订单号
    private String money;//实际支出金额
    private String totalAmount;//订单金额
    private String orderStatus;//状态
}
