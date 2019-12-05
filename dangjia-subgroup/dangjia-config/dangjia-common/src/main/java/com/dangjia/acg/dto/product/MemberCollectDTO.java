package com.dangjia.acg.dto.product;

import lombok.Data;

import java.util.Date;

@Data
public class MemberCollectDTO {
    private String id;
    private String productId;
    private String productName;
    private double sellPrice;
    private String image;
    private String unitName;
    /**
     * 调后价格
     */
    private Double adjustedPrice;

    /**
     * 调价时间
     */
    private Date modityPriceTime;
}
