package com.dangjia.acg.dto.storefront;

import com.dangjia.acg.modle.storefront.StorefrontProduct;
import lombok.Data;

@Data
public class BasicsStorefrontProductViewDTO {
    private String id;
    private String goodsName;
    private String productName;
    private String attributeIdArr;
    private String isShelfStatus;
    /**
     * 城市ID
     */
    private String cityId;

    private String valueNameArr;
    private String prodTemplateId;

    private StorefrontProduct storefrontProduct;
}
