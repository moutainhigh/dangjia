package com.dangjia.acg.modle.member;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类-轮播图
 */
@Data
@Entity
@Table(name = "dj_member_app_banner")
@ApiModel(description = "轮播图")
public class AppBanner extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "名称")
	@ApiModelProperty("名称")
	private String name;

	@Column(name = "imageurl")
	@Desc(value = "图片路径")
	@ApiModelProperty("图片路径")
	private String imageurl;

	@Column(name = "model")
	@Desc(value = "1找设计，2做精算，3排期施工")
	@ApiModelProperty("1找设计，2做精算，3排期施工")
	private int model;//

	@Column(name = "state")
	@Desc(value = "0启用1禁用")
	@ApiModelProperty("0启用1禁用")
	private int state;//

	@Column(name = "path")
	@Desc(value = "跳转地址")
	@ApiModelProperty("跳转地址")
	private String path;//

	@Column(name = "order_list")
	@Desc(value = "排序")
	@ApiModelProperty("排序")
	private Integer orderList;//
}