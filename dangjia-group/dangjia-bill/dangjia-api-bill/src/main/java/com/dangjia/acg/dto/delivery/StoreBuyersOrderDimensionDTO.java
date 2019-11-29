package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 店铺卖家维度实体-订单详情
 */
@Data
public class StoreBuyersOrderDimensionDTO {
    private String number; // 订单号
    private Date createDate;//下单时间
    private Double income;//收入
    private String expenditure ;// 支出
    private String profit;// 利润
    List<StoreBuyersDimensionOrderDetailDTO> storeBuyersDimensionOrderDetailList;

}
