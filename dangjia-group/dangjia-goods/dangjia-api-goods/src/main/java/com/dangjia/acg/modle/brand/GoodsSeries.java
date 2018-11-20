package com.dangjia.acg.modle.brand;

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
@Table(name = "dj_basics_goods_series")
@ApiModel(description = "商品关联品牌系列")
public class GoodsSeries extends BaseEntity {

	@Column(name = "goods_id")
    private String goodsId;//商品id

	@Column(name = "brand_id")
    private String brandId;//品牌id

	@Column(name = "series_id")
    private String seriesId;//系列id

}