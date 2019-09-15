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
import javax.persistence.Transient;

/**
   * @类 名： GoodsCategory
   * @功能描述： 商品/服务的类别
   * @作者信息： zmj
   * @创建时间： 2018-9-12下午1:55:35
 */
@Data
@Entity
@Table(name = "dj_basics_goods_category")
@ApiModel(description = "商品类别")
@FieldNameConstants(prefix = "")
public class BasicsGoodsCategory extends BaseEntity {

	@Column(name = "parent_id")
    private String parentId;//上级id

	@Column(name = "parent_top")
    private String parentTop;//顶级id

	@Column(name = "name")
    private String name;//名称

	@Column(name = "image")
    private String image;//图片

    @Column(name = "sort")
    @Desc(value = "顺序")
    @ApiModelProperty("顺序")
    private Integer sort;

    @Column(name = "is_last_category")
    @Desc(value = "是否末级分类")
    @ApiModelProperty("是否末级分类")
    private String isLastCategory;//是否末级分类（1是，0否）

    @Column(name = "purchase_restrictions")
    @Desc(value = "购买限制")
    @ApiModelProperty("购买限制")
    private String purchaseRestrictions;//购买限制（1自由购房；1有房无精算；2有房有精算）


    @Column(name = "brand_ids")
    @Desc(value = "品牌ID")
    @ApiModelProperty("品牌ID")
    private String brandIds;//关联的品牌ID，多个逗号分割

    @Column(name = "cover_image")
    @Desc(value = "上传封面图")
    @ApiModelProperty("上传封面图")
    private String coverImage;//上传封面图

    @Column(name = "category_label_id")
    @Desc(value = "分类标签ID")
    @ApiModelProperty("分类标签ID")
    private String categoryLabelId;//分类标签ID

    //业主
    @Transient
    private String categoryIds;
    @Transient
    private Double rowPrice;
}