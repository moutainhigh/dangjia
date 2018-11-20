package com.dangjia.acg.modle.activity;


import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 优惠券记录
 */
@Data
@Entity
@Table(name = "dj_activity_red_pack_record")
@ApiModel(description = "用户优惠券")
public class ActivityRedPackRecord extends BaseEntity {

	@Column(name = "member_id")
	@Desc(value = "会员ID")
	@ApiModelProperty("会员ID")
	private String memberId; //memberid

	@Column(name = "phone")
	@Desc(value = "存放业主电话")
	@ApiModelProperty("存放业主电话")
	private String phone;//


	@Column(name = "house_id")
	@Desc(value = "对应房子")
	@ApiModelProperty("对应房子")
	private String houseId;//houseid


	@Column(name = "have_receive")
	@Desc(value = "优惠券状态，0未使用，1已使用，2已过期,3已失效")
	@ApiModelProperty("优惠券状态，0未使用，1已使用，2已过期,3已失效")
	private int haveReceive;//havereceive


	@Column(name = "red_pack_rule_id")
	@Desc(value = "优惠券规则ID")
	@ApiModelProperty("优惠券规则ID")
	private String redPackRuleId;//

	@Column(name = "red_pack_id")
	@Desc(value = "优惠券Id")
	@ApiModelProperty("优惠券Id")
	private String redPackId;//

	@Column(name = "business_order_number")
	@Desc(value = "订单编号")
	@ApiModelProperty("订单编号")
	private String businessOrderNumber;

}