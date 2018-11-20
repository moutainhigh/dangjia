package com.dangjia.acg.modle.activity;


import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - 活动
 */
@Data
@Entity
@Table(name = "dj_activity")
@ApiModel(description = "活动主表")
public class Activity extends BaseEntity {


	@Column(name = "name")
	@Desc(value = "活动名")
	@ApiModelProperty("活动名")
	private String name;


	@Column(name = "activity_type")
	@Desc(value = "优惠类型 0为直推 1为注册 2为邀请")
	@ApiModelProperty("优惠类型 0为直推 1为注册 2为邀请")
	private int redPacketType;//

	@Column(name = "start_date")
	@Desc(value = "活动开始时间")
	@ApiModelProperty("活动开始时间")
	private Date startDate;//

	@Column(name = "end_date")
	@Desc(value = "活动结束时间")
	@ApiModelProperty("活动结束时间")
	private Date endDate;//


	@Column(name = "remake")
	@Desc(value = "备注说明适用范围")
	@ApiModelProperty("备注说明适用范围")
	private String remake;//

	@Column(name = "vimage")
	@Desc(value = "活动图片")
	@ApiModelProperty("活动图片")
	private String vimage;//

	@Column(name = "vurl")
	@Desc(value = "活动专链")
	@ApiModelProperty("活动专链")
	private String vurl;//


	@Column(name = "city_id")
	@Desc(value = "地区专享")
	@ApiModelProperty("地区专享")
	private String cityId;//


	@Column(name = "delete_state")
	@Desc(value = "状态，0正常，1停用")
	@ApiModelProperty("状态，0正常，1停用")
	private int deleteState;

}