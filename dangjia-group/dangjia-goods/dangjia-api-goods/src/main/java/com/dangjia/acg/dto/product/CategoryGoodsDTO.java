package com.dangjia.acg.dto.product;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;


@Data
@FieldNameConstants(prefix = "")
public class CategoryGoodsDTO {
    private String id;//货品ID
    private String goodsId; //货品ID

    private String goodsName;//货品名称

    private String categoryId;//分类id

    private Integer type;//0:材料；1：包工包料2：人工；3：体验；4：增值

    private Integer buy;//购买性质0：必买；1可选；2自购

    private Integer sales;//退货性质0：可退；1不可退
    //返回对应的商品类型
    List<GoodsProductDTO> productList;
}
