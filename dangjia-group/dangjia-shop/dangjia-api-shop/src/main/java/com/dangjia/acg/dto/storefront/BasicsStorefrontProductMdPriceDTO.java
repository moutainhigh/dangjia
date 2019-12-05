package com.dangjia.acg.dto.storefront;

import lombok.Data;

/**
 * 调价列表实体
 */
@Data
public class BasicsStorefrontProductMdPriceDTO {
    private String id;//店铺商品id
    private String goodsName ; //货品名称
    private String productName;//商品名称
    private String image;//商品图片
    private String valueNameArr;//属性
    private  String sellPrice;//销售价格
    private String prodTemplateId;// 商品模板id
    private String suppliedNum; // 供货数
    private String adjustedPrice;//调后价格
    private String modityPriceTime; //调价时间


}
