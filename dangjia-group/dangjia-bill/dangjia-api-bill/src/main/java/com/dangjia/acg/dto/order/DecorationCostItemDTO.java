package com.dangjia.acg.dto.order;

import lombok.Data;

@Data
public class DecorationCostItemDTO {

    private String categoryId;//类别ID

    private String goodsId;//货品ID

    private String goodsName;//货品名称

    private String productId;//商品ID

    private String productTemplateId;//商品模板ID

    private String productName;//商品名称

    private String image;//图片地址

    private String imageUrl;//商品图片访问全地址

    private String productSn;//商品编码

    private Double price;//单价

    private Double shopCount;//购买量

    private String brandId;//品牌ID

    private String brandName;//品牌名称

    private  String valueIdArr;//商品规格ID

    private String valueNameArr;//商品规格名称

    private Double purchaseTotalPrice;//自购商品花费

    private Double actualPaymentPrice;//商品花费

    private Integer steta;//商品类型 （1我们购，2自购商品）

    private String actuaryBudgetId;//精算商品ID（自购商品才有）

}
