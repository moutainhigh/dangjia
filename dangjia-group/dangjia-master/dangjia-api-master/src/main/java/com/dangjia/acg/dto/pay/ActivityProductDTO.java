package com.dangjia.acg.dto.pay;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActivityProductDTO {
    private String productId;//商品ID
    private Double shopCount;//商品数量
    private Double price;//商品单价
}
