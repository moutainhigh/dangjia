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
    private String productId;
    private String shopcount;//购买总数
    private String askCount;//已要总数
    private String receive;//收货数量
    private BigDecimal supCost;
    private BigDecimal num;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private BigDecimal totalProfit;
}
