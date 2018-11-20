package com.dangjia.acg.modle.discount;


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
 * 实体类 - 红包活动
 */
@Data
@Entity
@Table(name = "dj_discount_red_packet_activity")
@ApiModel(description = "红包活动")
public class RedPacketActivity extends BaseEntity {

	@Column(name = "batch")
	@Desc(value = "批次")
	@ApiModelProperty("批次")
	private String batch;

	@Column(name = "name")
	@Desc(value = "优惠名")
	@ApiModelProperty("优惠名")
	private String name;

	@Column(name = "worker_type_id")
	@Desc(value = "优惠券对应工种适用工种")
	@ApiModelProperty("优惠券对应工种适用工种")
	private String workerTypeId;//workertypeid

	@Column(name = "red_packet_type")
	@Desc(value = "优惠类型 0为人工 1为材料 2为单品")
	@ApiModelProperty("优惠类型 0为人工 1为材料 2为单品")
	private int redPacketType;//

	@Column(name = "num")
	@Desc(value = "发行数量")
	@ApiModelProperty("发行数量")
	private int num;//

	@Column(name = "start_date")
	@Desc(value = "活动开始时间")
	@ApiModelProperty("活动开始时间")
	private Date startDate;//

	@Column(name = "end_date")
	@Desc(value = "活动结束时间")
	@ApiModelProperty("活动结束时间")
	private Date endDate;//

	@Column(name = "goods_id")
	@Desc(value = "单品Id 优惠类型为3的时候有此属性")
	@ApiModelProperty("单品Id 优惠类型为3的时候有此属性")
	private String goodsId;//

	@Column(name = "receive_condition")
	@Desc(value = "优惠券领取条件")
	@ApiModelProperty("优惠券领取条件")
	private String receiveCondition;//

	@Column(name = "toplimit")
	@Desc(value = "是否有上限，0代表有上限，1代表没有上限，默认0")
	@ApiModelProperty("是否有上限，0代表有上限，1代表没有上限，默认0")
	private int toplimit;//

	@Column(name = "top_deductible")
	@Desc(value = "优惠券上限金额")
	@ApiModelProperty("优惠券上限金额")
	private BigDecimal topDeductible;//topdeductible

	@Column(name = "deductible")
	@Desc(value = "优惠券抵扣金额 根据折扣类型决定")
	@ApiModelProperty("优惠券抵扣金额 根据折扣类型决定")
	private BigDecimal deductible;//

	@Column(name = "is_share")
	@Desc(value = "是否可以与其他优惠券共用，0代表可以共用，1代表不可以共用")
	@ApiModelProperty("是否可以与其他优惠券共用，0代表可以共用，1代表不可以共用")
	private int isShare;//isshare

	@Column(name = "money_or_dis")
	@Desc(value = "折扣类型 0为减免金额券 1 为折扣券 2代金券")
	@ApiModelProperty("折扣类型 0为减免金额券 1 为折扣券 2代金券")
	private int moneyOrDis; //

	@Column(name = "remake")
	@Desc(value = "备注说明适用范围")
	@ApiModelProperty("备注说明适用范围")
	private String remake;//

	@Column(name = "delete_state")
	@Desc(value = "状态，0正常，1停用")
	@ApiModelProperty("状态，0正常，1停用")
	private int deleteState;//deletestate

	@Column(name = "surplus_nums")
	@Desc(value = "优惠券剩余数量")
	@ApiModelProperty("优惠券剩余数量")
	private int surplusNums ;//
}