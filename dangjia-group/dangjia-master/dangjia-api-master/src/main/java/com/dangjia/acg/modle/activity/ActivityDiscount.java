package com.dangjia.acg.modle.activity;


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
 * 实体类 - 活动优惠券关系表
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_activity_discount")
@ApiModel(description = "活动优惠券关系表")
public class ActivityDiscount extends BaseEntity {


	@Column(name = "activity_id")
	@Desc(value = "活动ID")
	@ApiModelProperty("活动ID")
	private String activityId;


	@Column(name = "activity_red_pack_id")
	@Desc(value = "优惠券ID")
	@ApiModelProperty("优惠券ID")
	private String activityRedPackId;
}