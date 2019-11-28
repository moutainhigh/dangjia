package com.dangjia.acg.dto.product;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 24/10/2019
 * Time: 下午 3:10
 */
@Data
public class ShoppingCartProductDTO {

    private String productId;
    private String productName;
    private Integer type;
    private Double sellPrice;
    private String specificationAttributes;
    private String image;
    private Integer count;
}
