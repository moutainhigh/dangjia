package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author: Yinjianbo
 * Date: 2019-5-16
 * 中台-补退换货流程-换货详情
 */
@Data
public class ProductChangeItemDTO{

    // src商品图片
    private String srcImage;
    // src商品ID
    private String srcProductId;
    // src商品编号
    private String srcProductSn;
    // src商品名称
    private String srcProductName;
    // src单价 5.0元/个
    private Double srcPrice;
    // src单位名称
    private String srcUnitName;
    // src差价
    private String srcDifferPrice;
    // src换货前数量
    private Double srcBeforeCount;
    // src换货后数量
    private Double srcAfterCount;
    // src合计
    private BigDecimal srcTotalMoney;

    // dest商品图片
    private String destImage;
    // dest商品ID
    private String destProductId;
    // dest商品编号
    private String destProductSn;
    // dest商品名称
    private String destProductName;
    // dest单价 5.0元/个
    private Double destPrice;
    // dest单位名称
    private String destUnitName;
    // dest差价
    private String destDifferPrice;
    // dest换货前数量
    private Double destBeforeCount;
    // dest换货后数量
    private Double destAfterCount;
    // dest合计
    private BigDecimal destTotalMoney;

    // 支付订单流水换货单商品详情begin
    private String productSn;
    private String productName;
    private String unitName;

    private String image;
    // 单价
    private BigDecimal price;
    // 差价
    private BigDecimal differPrice;
    // 数量
    private Double shopCount;
    private BigDecimal totalPrice;
    // 支付订单流水换货单商品详情end
}
