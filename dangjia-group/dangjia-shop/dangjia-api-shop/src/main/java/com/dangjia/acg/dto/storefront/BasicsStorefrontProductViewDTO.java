package com.dangjia.acg.dto.storefront;

import com.dangjia.acg.modle.product.ProductAddedRelation;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import lombok.Data;

import java.util.List;

@Data
public class BasicsStorefrontProductViewDTO {
    private String id;//商品id
    private String goodsName;//商品名称
    private String productName;//货品名称
    //private String attributeIdArr;//
    private String isShelfStatus;//是否上下价

    private Double sellPrice; //销售价格
    private String suppliedNum; //供货数量

    //private String cityId;//城市id
    private String valueNameArr;//货品属性
    private String prodTemplateId;//获品模板id
    private StorefrontProduct storefrontProduct;//店铺集合
    List<ProductAddedRelation>  productAddedRelationlist;//




}
