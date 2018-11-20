package com.dangjia.acg.modle.discount;


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
@Table(name = "dj_discount_red_packet_record")
@ApiModel(description = "优惠券记录")
public class RedPacketRecord extends BaseEntity {

	@Column(name = "member_id")
	@Desc(value = "会员ID")
	@ApiModelProperty("会员ID")
	private String memberId; //memberid

	@Column(name = "phone")
	@Desc(value = "存放业主电话")
	@ApiModelProperty("存放业主电话")
	private String phone;//

	@Column(name = "worker_type")
	@Desc(value = "优惠券对应工种")
	@ApiModelProperty("优惠券对应工种")
	private int workerType;//workertype

	@Column(name = "house_id")
	@Desc(value = "对应房子")
	@ApiModelProperty("对应房子")
	private String houseId;//houseid

	@Column(name = "red_packet_activity_id")
	@Desc(value = "对应优惠券父级类别表ID")
	@ApiModelProperty("对应优惠券父级类别表ID")
	private String redPacketActivityId;//redpacketactivityid

	@Column(name = "have_receive")
	@Desc(value = "是否已经领取使用，0没领取，1已经领取，2已经使用,3重复项,4已经绑定 勾选")
	@ApiModelProperty("是否已经领取使用，0没领取，1已经领取，2已经使用,3重复项,4已经绑定 勾选")
	private int haveReceive;//havereceive

	@Column(name = "red_packet_numbered")
	@Desc(value = "优惠券编号")
	@ApiModelProperty("优惠券编号")
	private int redPacketNumbered;//

	@Column(name = "red_packet_id")
	@Desc(value = "优惠券Id")
	@ApiModelProperty("优惠券Id")
	private String redPacketId;//

	@Column(name = "red_packet_pay_money_id")
	@Desc(value = "关联订单")
	@ApiModelProperty("关联订单")
	private String redPacketPayMoneyId;

}