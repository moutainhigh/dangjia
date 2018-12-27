package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author: Ronalcheng
 * Date: 2018/12/4 0004
 * Time: 11:42
 */
@Data
public class OrderDTO {

    private String orderId;//订单id  订单号
    private String image;
    private String name;//各种名
    private BigDecimal totalAmount;//合计
    private String workerTypeName;//流水名
}
