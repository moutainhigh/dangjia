package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * 店铺卖家维度实体
 */
@Data
public class StoreBuyersDimensionDetailDTO {
    private String orderSplitId;//要货单id
    private String orderNumber;//要货订单号
    private String createDate;//要货下单时间
    private String houseId; //房子id
}

