package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单详情
 */
@Data
public class OrderItemByDTO {
    private String productId;
    private String productName;

    private String image;
    private BigDecimal price;
    private BigDecimal shopCount;
    private BigDecimal totalPrice;

    private String goodsName;
    private String goodsId;

    private int type;
}

