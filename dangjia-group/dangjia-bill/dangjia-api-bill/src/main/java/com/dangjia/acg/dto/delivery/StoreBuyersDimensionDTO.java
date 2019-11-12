package com.dangjia.acg.dto.delivery;

import lombok.Data;

/**
 * 店铺卖家维度实体
 */
@Data
public class StoreBuyersDimensionDTO {

    private String StorefrontId;//店铺id
    private String shipAddress; // 房子地址
    private String name; // 业主名称
    private Double income;//收入
    private String expenditure ;// 支出
    private String profit;// 利润
    private String houseId;//房子id
}
