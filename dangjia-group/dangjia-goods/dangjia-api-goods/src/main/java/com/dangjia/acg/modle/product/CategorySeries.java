package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @ClassName: Goods
 * @Description: 商品关联品牌系列
 * @author: zmj
 * @date: 2018-9-18上午9:34:42
 */
@Data
@Entity
@Table(name = "dj_basics_category_brand")
@ApiModel(description = "商品关联品牌系列")
public class CategorySeries extends BaseEntity {

	@Column(name = "category_id")
    private String goodsId;//类别id

	@Column(name = "brand_id")
    private String brandId;//品牌id

}