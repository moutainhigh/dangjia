package com.dangjia.acg.dto.product;

import lombok.Data;

@Data
public class MemberCollectDTO {
    private String id;
    private String productId;
    private String productName;
    private double sellPrice;
    private String image;
    private String unitName;
}
