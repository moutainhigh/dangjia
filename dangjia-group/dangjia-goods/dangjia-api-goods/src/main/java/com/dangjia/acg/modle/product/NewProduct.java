package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 货品下的商品实体类
 * @author Ronalcheng
 */
@Data
@Entity
@Table(name = "dj_basics_product")
@ApiModel(description = "货品下的商品")
@FieldNameConstants(prefix = "")
public class NewProduct extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "category_id")
    private String categoryId;//分类id

    @Column(name = "goods_id")
    private String goodsId;//商品id

    @Column(name = "product_sn")
    private String productSn;//货号编号

    @Column(name = "image")
    private String image;//图片

    @Column(name = "unit_name")
    private String unitName;//单位

    @Column(name = "unit_id")
    private String unitId;//单位id

    @Column(name = "label_id")
    private String labelId;//标签id

    @Column(name = "convert_quality")
    private Double convertQuality;//换算量

    @Column(name = "convert_unit")
    private String convertUnit;//换算单位ID

    @Column(name = "type")
    private Integer type;//是否禁用0：禁用；1不禁用

    @Column(name = "maket")
    private Integer maket;//是否上架  0:未上架；1已上架

    @Column(name = "price")
    private Double price;//销售价
    @Column(name = "istop")
    private String istop;// 是否置顶 0=正常  1=置顶

}
