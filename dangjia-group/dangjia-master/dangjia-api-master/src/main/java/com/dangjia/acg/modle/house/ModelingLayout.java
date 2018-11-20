package com.dangjia.acg.modle.house;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 建模-户型
 */
@Data
@Entity
@Table(name = "dj_house_modeling_layout")
@ApiModel(description = "房子")
public class ModelingLayout extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "户型名称")
	@ApiModelProperty("户型名称")
	private String name;//

	@Column(name = "village_id")
	@Desc(value = "关联小区id")
	@ApiModelProperty("关联小区id")
	private String villageId;//villageid

	@Column(name = "image")
	@Desc(value = "户型图片")
	@ApiModelProperty("户型图片")
	private String image;//inamge

	@Column(name = "build_square")
	@Desc(value = "建筑面积直接关联房子的建筑面积")
	@ApiModelProperty("建筑面积直接关联房子的建筑面积")
	private String buildSquare; //acreage

	@Column(name = "city_id")
	@Desc(value = "areaid")
	@ApiModelProperty("areaid")
	private String cityId;//areaid
}