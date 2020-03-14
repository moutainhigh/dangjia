package com.dangjia.acg.dto.product;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Date;


@Data
@FieldNameConstants(prefix = "")
public class StorefrontProductDTO {
    private String id;

    private String storefrontId; //店铺ID;

    private String storefrontProductId; //店铺商品ID;

    private String productId;

    private String productTemplateId;//商品模板表ID

    private String categoryId;//分类id

    private String productName;//商品名称

    private String goodsId;//货品ID

    private String productSn;//商品编码

    private Double cost;//平均成本价

    private Double sellPrice;//商品售价

    private String image;//商品图片

    private String imageUrl;//商品图片详细地址

    private Double convertQuality;//换算量

    private String convertUnit;//换算单位

    private String unitId;//换算单位

    private String unitName;//换算单位

    private String storefrontName;//店铺名称

    private Double rushPurchasePrice;//抢购价

    private String storeActivityId;

    private Integer activityType;

    private Integer spellGroup;

    private Date endSession;

    private Date sessionStartTime;
}
