package com.dangjia.acg.dto.product;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * 精算查询末级分类及对应末级分类下的商品货品
 */
@Data
public class CategoryGoodsProductDTO {
    private String id;//类别ID
    private String categoryId;//类别ID
    private String categoryName;//类别名称
    private String goodsName;//需查询的货品名称
    //返回对应的货品列表
    private List<CategoryGoodsDTO> goodsList;
}
