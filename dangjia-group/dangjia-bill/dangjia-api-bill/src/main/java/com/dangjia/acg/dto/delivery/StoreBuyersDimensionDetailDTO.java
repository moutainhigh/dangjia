package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * 店铺卖家维度实体
 */
@Data
public class StoreBuyersDimensionDetailDTO {
    private String orderSplitId;//要货单id
    private String number; // 订单号
    private Date createDate;//下单时间
    private String cost;// 成本价
    private String price ;// 销售价
    private String  profit ;// 利润
}

