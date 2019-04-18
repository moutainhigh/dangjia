package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单详情
 */
@Data
public class OrderItemByDTO {
    private String productSn;
    private String productName;
    private String unitName;

    private String image;
    private BigDecimal price;
    private BigDecimal shopCount;
    private BigDecimal totalPrice;

    private String goodsName;
    private String goodsSn;

    private int type;
}

