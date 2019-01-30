package com.dangjia.acg.modle.house;

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
 * 实体类 - 剩余材料的临时仓库
 */
@Data
@Entity
@Table(name = "dj_house_surplus_ware_house_item")
@ApiModel(description = "剩余材料的临时仓库的材料详情")
@FieldNameConstants(prefix = "")
public class SurplusWareHouseItem extends BaseEntity {

	@Column(name = "surplus_ware_house_id")
	@Desc(value = "临时仓库id")
	@ApiModelProperty("临时仓库id")
	private String surplusWareHouseId;

	@Column(name = "product_id")
	@Desc(value = "货品id")
	@ApiModelProperty("货品id")
	private String productId;

	@Column(name = "product_name")
	@Desc(value = "货品名字")
	@ApiModelProperty("货品名字")
	private String productName;

	@Column(name = "product_count")
	@Desc(value = "清点商品的数量")
	@ApiModelProperty("清点商品的数量")
	private Integer productCount;

}