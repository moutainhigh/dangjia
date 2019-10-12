package com.dangjia.acg.dto.product;

import lombok.Data;
import lombok.experimental.FieldNameConstants;


@Data
@FieldNameConstants(prefix = "")
public class GoodsProductDTO {
    private String productId; //商品ID;

    private String categoryId;//分类id

    private String productName;//商品名称

    private String goodsId;//商品名称
}
