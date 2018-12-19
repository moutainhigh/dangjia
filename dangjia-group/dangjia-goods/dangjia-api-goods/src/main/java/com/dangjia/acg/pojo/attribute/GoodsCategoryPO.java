package com.dangjia.acg.pojo.attribute;

import com.dangjia.acg.modle.attribute.GoodsCategory;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @类 名： GoodsCategory
 * @功能描述： 商品/服务的类别
 * @作者信息： ysl
 * @创建时间： 2018-12-18下午14:55:35
 */
@Data
@Entity
@Table(name = "dj_basics_goods_category")
@ApiModel(description = "商品材料类别")
public class GoodsCategoryPO extends GoodsCategory {


}