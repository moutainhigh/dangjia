package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author: Yinjianbo
 * Date: 2019-5-16
 * 中台-补退换货流程-换货详情
 */
@Data
public class ProductChangeItemDTO {

    private String id;
    // 商品图片
    private String image;
    // 商品ID
    private String productId;
    // 商品编号
    private String productSn;
    // 商品名称
    private String productName;
    // 单价 5.0元/个
    private Double price;
    // 单位名称
    private String unitName;
    // 差价
    private String differPrice;
    // 换货前数量
    private Double srcSurCount;
    // 换货后数量
    private Double destSurCount;
    // 合计
    private BigDecimal totalMoney;
}
