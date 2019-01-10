package com.dangjia.acg.modle.member;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 客服和业主的沟通记录表 （记录每条沟通记录描述）
 * at ysl 2019-1-5
 */
@Data
@Entity
@Table(name = "dj_member_customer_record")
@ApiModel(description = "客服和业主的沟通记录表")
public class CustomerRecord extends BaseEntity {

	@Column(name = "member_id")
	@Desc(value = "业主id")
	@ApiModelProperty("业主id")
	private String memberId;

	@Column(name = "user_id")
	@Desc(value = "当前客服id")
	@ApiModelProperty("当前客服id")
	private String userId;

	@Column(name = "describes")
	@Desc(value = "沟通描述")
	@ApiModelProperty("沟通描述")
	private String describes;

	@Column(name = "remind_time")
	@Desc(value = "提醒时间")
	@ApiModelProperty("提醒时间")
	protected Date remindTime;
}