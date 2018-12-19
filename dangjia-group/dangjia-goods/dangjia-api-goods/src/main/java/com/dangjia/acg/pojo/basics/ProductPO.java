package com.dangjia.acg.pojo.basics;

import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Label;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.pojo.attribute.AttributePO;
import com.dangjia.acg.pojo.attribute.AttributeValuePO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.List;

/**
 * 商品货品实体类
 * @author Ronalcheng
 */
@Data
@Entity
//@Table(name = "dj_basics_product")
@ApiModel(description = "商品货品")
@FieldNameConstants(prefix = "")
public class ProductPO extends Product {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private GoodsCategory  goodsCategory;//分类对象

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "goods_id")
    private Goods goods;//商品对象

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "unit_id")
    private Unit unit;//单位对象

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "label_id")
    private Label label;//标签

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "brand_id")
    private Brand brand;//品牌对象

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "brand_seres_id")
//    private BrandSeries brandSeries;//品牌系列

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ProductPO")
    @JoinColumn(name = "attribute_id_arr")
    private List<AttributePO> attributeValueLists;//属性Id集合
}