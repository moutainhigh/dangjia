package com.dangjia.acg.modle.attribute;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
   * @类 名： GoodsCategory
   * @功能描述： 商品/服务的类别
   * @作者信息： zmj
   * @创建时间： 2018-9-12下午1:55:35
 */
@Data
@Entity
@Table(name = "dj_basics_goods_category")
@ApiModel(description = "商品材料类别")
@FieldNameConstants(prefix = "")
public class GoodsCategory extends BaseEntity {

	@Column(name = "parent_id")
    private String parentId;//上级id

	@Column(name = "parent_top")
    private String parentTop;//顶级id

	@Column(name = "name")
    private String name;//名称

	@Column(name = "image")
    private String image;//图片

}