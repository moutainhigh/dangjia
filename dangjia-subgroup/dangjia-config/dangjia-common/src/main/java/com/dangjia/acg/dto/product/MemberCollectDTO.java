package com.dangjia.acg.dto.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class MemberCollectDTO {
    private String id;
    private String productId;
    private String productName;
    private double sellPrice;
    private String image;
    @ApiModelProperty("商品图片(单张)")
    private String imageSingle;

    @ApiModelProperty("商品图片详细地址")
    private String imageUrl;
    private String unitName;
    /**
     * 调后价格
     */
    private Double adjustedPrice;

    /**
     * 调价时间
     */
    private Date modityPriceTime;

    private String collectId;

    private String storefrontName;

    private String systemLogo;

    private String conditionType;
}
