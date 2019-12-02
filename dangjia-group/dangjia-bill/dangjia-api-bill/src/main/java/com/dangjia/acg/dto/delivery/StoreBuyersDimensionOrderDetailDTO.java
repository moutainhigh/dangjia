package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * 店铺卖家维度实体
 */
@Data
public class StoreBuyersDimensionOrderDetailDTO {

    private String splitDeliverId;//发货单id
    private String number;//发货单号
    private String sendTime ; // 发货时间
    private String price;// 收入
    private String cost; // 支出
    private String profit;// 利润

}

