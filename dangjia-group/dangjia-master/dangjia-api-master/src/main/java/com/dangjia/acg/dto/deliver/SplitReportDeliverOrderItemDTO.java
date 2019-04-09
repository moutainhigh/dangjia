package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author: qiyuxiang
 * Date: 2019/04/09
 * Time: 17:00
 */
@Data
public class SplitReportDeliverOrderItemDTO {

    private String image;
    private String productName;
    private String productSn;
    private BigDecimal supCost;
    private BigDecimal num;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private BigDecimal totalProfit;
}
