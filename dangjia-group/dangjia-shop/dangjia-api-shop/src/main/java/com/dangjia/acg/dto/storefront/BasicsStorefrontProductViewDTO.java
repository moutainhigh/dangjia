package com.dangjia.acg.dto.storefront;

import lombok.Data;

@Data
public class BasicsStorefrontProductViewDTO {
    private String id;
    private String goodsName;
    private String productName;
    private String attributeIdArr;
    private String isShelfStatus;
}
