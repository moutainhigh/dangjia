package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * author: LJL
 * Date: 2019/9/11
 * Time: 13:56
 */
@Data
@Entity
@Table(name = "dj_basics_product")
@ApiModel(description = "商品表实体")
@FieldNameConstants(prefix = "")
public class DjBasicsProduct extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "goods_id")
    @Desc(value = "材料id")
    @ApiModelProperty("材料id")
    private String goodsId;

    @Column(name = "product_sn")
    @Desc(value = "货号编号")
    @ApiModelProperty("货号编号")
    private String productSn;

    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String image;

    @Column(name = "unit_name")
    @Desc(value = "单位换算")
    @ApiModelProperty("单位换算")
    private String unitName;

    @Column(name = "unit_id")
    @Desc(value = "单位ID")
    @ApiModelProperty("单位ID")
    private String unitId;

    @Column(name = "category_id")
    @Desc(value = "分类id")
    @ApiModelProperty("分类id")
    private String categoryId;

    @Column(name = "label_id")
    @Desc(value = "标签id")
    @ApiModelProperty("标签id")
    private String labelId;

    @Column(name = "type")
    @Desc(value = "是否禁用0：禁用；1不禁用")
    @ApiModelProperty("是否禁用0：禁用；1不禁用")
    private Integer type;

    @Column(name = "maket")
    @Desc(value = "是否上架0：不上架；1：上架")
    @ApiModelProperty("是否上架0：不上架；1：上架")
    private Integer maket;

    @Column(name = "price")
    @Desc(value = "材料商品别名")
    @ApiModelProperty("材料商品别名")
    private Double price;

    @Column(name = "other_name")
    @Desc(value = "标签id")
    @ApiModelProperty("标签id")
    private String otherName;

    @Column(name = "istop")
    @Desc(value = "是否置顶 0=正常，1=置顶")
    @ApiModelProperty("是否置顶 0=正常，1=置顶")
    private Integer istop;

    @Column(name = "remark")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remark;


}
