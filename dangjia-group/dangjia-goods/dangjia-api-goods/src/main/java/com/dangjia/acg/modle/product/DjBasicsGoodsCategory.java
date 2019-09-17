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
 * author: wk
 * Date: 2019/9/15
 * Time: 9:20
 */
@Data
@Entity
@Table(name = "dj_basics_goods_category")
@ApiModel(description = "商品材料类别")
@FieldNameConstants(prefix = "")
public class DjBasicsGoodsCategory extends BaseEntity {

    @Column(name = "parent_id")
    @Desc(value = "上级id")
    @ApiModelProperty("上级id")
    private String parentId;//上级id

    @Column(name = "parent_top")
    @Desc(value = "上级id")
    @ApiModelProperty("上级id")
    private String parentTop;//顶级id

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;//名称

    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String image;//图片

    @Column(name = "sort")
    @Desc(value = "顺序")
    @ApiModelProperty("顺序")
    private Integer sort;

    @Column(name = "is_last_category")
    @Desc(value = "是否末级分类（1是，0否）")
    @ApiModelProperty("是否末级分类（1是，0否）")
    private Integer isLastCategory;

    @Column(name = "purchase_restrictions")
    @Desc(value = "购买限制（1自由购房；1有房无精算；2有房有精算）")
    @ApiModelProperty("购买限制（1自由购房；1有房无精算；2有房有精算）")
    private Integer purchaseRestrictions;

    @Column(name = "cover_image")
    @Desc(value = "上传封面图")
    @ApiModelProperty("上传封面图")
    private String coverImage;

    @Column(name = "category_label_id")
    @Desc(value = "分类标签ID")
    @ApiModelProperty("分类标签ID")
    private String categoryLabelId;

    @Column(name = "brand_id")
    @Desc(value = "品牌Id")
    @ApiModelProperty("品牌Id")
    private String brandId;
}
