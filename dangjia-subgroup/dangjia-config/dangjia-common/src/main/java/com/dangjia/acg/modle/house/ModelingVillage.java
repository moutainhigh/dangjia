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
 * 实体类 - 建模-小区
 */
@Data
@Entity
@Table(name = "dj_house_modeling_village")
@ApiModel(description = "小区")
@FieldNameConstants(prefix = "")
public class ModelingVillage extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "小区名称")
	@ApiModelProperty("小区名称")
	private String name;

	@Column(name = "city_id")
	@Desc(value = "城市ID")
	@ApiModelProperty("城市ID")
	private String cityId;//areaid

	@Column(name = "area_name")
	@Desc(value = "区域名称")
	@ApiModelProperty("区域名称")
	private String areaName;//areaname

	@Column(name = "address")
	@Desc(value = "小区详细地址")
	@ApiModelProperty("小区详细地址")
	private String address;//

	@Column(name = "village_image")
	@Desc(value = "楼盘图片")
	@ApiModelProperty("楼盘图片")
	private String villageImage;//villageimage

	@Column(name = "village_state")
	@Desc(value = "楼盘状态，1老房，2新房")
	@ApiModelProperty("楼盘状态，1老房，2新房")
	private String villageState;//villagestate

	@Column(name = "collection_state")
	@Desc(value = "销售状态")
	@ApiModelProperty("销售状态")
	private String collectionState;//collectionstate

	@Column(name = "initials")
	@Desc(value = "存放ABCD")
	@ApiModelProperty("存放ABCD")
	private String initials;//

	@Column(name = "layout_sum")
	@Desc(value = "户型总数")
	@ApiModelProperty("户型总数")
	private Integer layoutSum;//layoutsum

	@Column(name = "locationx")
	@Desc(value = "百度定位目标")
	@ApiModelProperty("百度定位目标")
	private String locationx;//

	@Column(name = "locationy")
	@Desc(value = "百度定位目标")
	@ApiModelProperty("百度定位目标")
	private String locationy;//

	@Column(name = "state")
	@Desc(value = "1代表已经删除，0代表可以使用")
	@ApiModelProperty("1代表已经删除，0代表可以使用")
	private Integer state;//

	@Column(name = "esid")
	@Desc(value = "搜索引擎ESID")
	@ApiModelProperty("搜索引擎ESID")
	private String esid;//
}