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
import java.util.Date;

/**
 * 实体类 - 活动优惠券关系表
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_activity_participant")
@ApiModel(description = "用户礼品表")
public class ActivityParticipant extends BaseEntity {


	@Column(name = "activity_id")
	@Desc(value = "活动ID")
	@ApiModelProperty("活动ID")
	private String activityId;

	@Column(name = "city_id")
	@Desc(value = "活动ID")
	@ApiModelProperty("活动ID")
	private String cityId;

	@Column(name = "remarks")
	@Desc(value = "领取备注")
	@ApiModelProperty("领取备注")
	private String remarks;


	@Column(name = "receive_date")
	@Desc(value = "领取时间")
	@ApiModelProperty("领取时间")
	private Date receiveDate;

	@Column(name = "state")
	@Desc(value = "状态，0已报名，1已领取，2已排除")
	@ApiModelProperty("状态，0已报名，1已领取，2已排除")
	private Integer state;

	@Column(name = "member_id")
	@Desc(value = "会员ID")
	@ApiModelProperty("会员ID")
	private String memberId;

	@Column(name = "phone")
	@Desc(value = "存放业主手机号")
	@ApiModelProperty("存放业主手机号")
	private String phone;


	@Column(name = "nick_name")
	@Desc(value = "存放业主昵称")
	@ApiModelProperty("存放业主昵称")
	private String nickName;


}