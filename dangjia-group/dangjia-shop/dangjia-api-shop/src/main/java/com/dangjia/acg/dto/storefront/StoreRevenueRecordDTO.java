package com.dangjia.acg.dto.storefront;

import lombok.Data;

import java.util.Date;

@Data
public class StoreRevenueRecordDTO {
    private String number;// 订单号
    private String accountFlowRecordId;// 支付流水ID
    private String state;//类型
    private String anyOrderId;// 订单ID
    private String money;//实际支出金额
    private Date createDate;//时间
}
