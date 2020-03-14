package com.dangjia.acg.dto.product;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
@ApiModel
public class StorefontInfoDTO {
    private String productId;//商品ID
    private String storefontId;//店铺ID
    private String name;
    private String goodsId;
    private String categoryId;
    private String productSn;
    private Double price;
    private String image;
    List goodsList;
    List productList;

}
