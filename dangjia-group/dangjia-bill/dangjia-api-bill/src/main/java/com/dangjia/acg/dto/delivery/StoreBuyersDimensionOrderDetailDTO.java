package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * 店铺卖家维度实体
 */
@Data
public class StoreBuyersDimensionOrderDetailDTO {

    private String splitId;//货单ID
    private String number;//货单号
    private String address;//货单号
    private String sendTime ; // 下单时间
    private String receive;// 收货数
    private String returnCount; // 支出
    private Double totalTransportationCost;//总运费
    private Double totalStevedorageCost;//总搬运费
    private Double totalPrice;//总销售价
    private Double totalSupCost;//总供应价
    private Double supPrice;//供应单价
    private Double price;//销售单价
    private Double profit; //利润
    private Double income;//总收入
    private Double expenditure;//支出

}

