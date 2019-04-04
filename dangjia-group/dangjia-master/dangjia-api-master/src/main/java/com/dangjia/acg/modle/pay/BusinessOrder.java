package com.dangjia.acg.modle.pay;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 实体类
 * @author Ronalcheng
 * 业务订单  联系所有订单
 */
@Data
@Entity
@Table(name = "dj_pay_business_order")
@ApiModel(description = "房子")
@FieldNameConstants(prefix = "")
public class BusinessOrder extends BaseEntity {

	@Column(name = "member_id")
	@Desc(value = "用户ID")
	@ApiModelProperty("用户ID")
	private String memberId;//memberid

	@Column(name = "number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String number;//

	@Column(name = "pay_order_number")
	@Desc(value = "支付订单号")
	@ApiModelProperty("支付订单号")
	private String payOrderNumber;//

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "task_id")
	@Desc(value = "任务id")
	@ApiModelProperty("任务id")
	private String taskId;//工序支付   补货补人工  提前付

	@Column(name = "state")
	@Desc(value = "处理状态  1刚生成(可编辑),2去支付(不修改),3已支付")
	@ApiModelProperty("处理状态  1刚生成(可编辑),2去支付(不修改),3已支付")
	private Integer state;//

	@Column(name = "total_price")
	@Desc(value = "该订单总价")
	@ApiModelProperty("该订单总价")
	private BigDecimal totalPrice;//

	@Column(name = "discounts_price")
	@Desc(value = "优惠钱")
	@ApiModelProperty("优惠钱")
	private BigDecimal discountsPrice;//

	@Column(name = "pay_price")
	@Desc(value = "实付")
	@ApiModelProperty("实付")
	private BigDecimal payPrice;//

	@Column(name = "type")
	@Desc(value = "支付类型 1工序支付任务,2补货补人工 ,4待付款进来只付材料")
	@ApiModelProperty("支付类型 1工序支付任务,2补货补人工 ,4待付款进来只付材料")
	private Integer type; // 1工序支付任务,2补货补人工 ,4待付款进来只付材料, 5验房分销
}
