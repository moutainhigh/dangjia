package com.dangjia.acg.modle.other;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * 实体类 - 结算比例表
 */
@Data
@Entity
@Table(name = "dj_other_work_deposit")
@ApiModel(description = "结算比例表")
public class WorkDeposit extends BaseEntity {

	@Column(name = "everyday_pay")
	@Desc(value = "普通工种每日完工支付的钱")
	@ApiModelProperty("普通工种每日完工支付的钱")
	private BigDecimal everydayPay;//everydaypay       100

	@Column(name = "limit_pay")
	@Desc(value = "每日完工支付上限百分比")
	@ApiModelProperty("每日完工支付上限百分比")
	private BigDecimal limitPay;//limitpay          0.30

	@Column(name = "stage_pay")
	@Desc(value = "阶段完工比例百分比")
	@ApiModelProperty("阶段完工比例百分比")
	private BigDecimal stagePay;//stagepay       0.45

	@Column(name = "whole_pay")
	@Desc(value = "整体完工比例")
	@ApiModelProperty("整体完工比例")
	private BigDecimal wholePay;//wholepay       0.25

	@Column(name = "deposit")
	@Desc(value = "最终押金比例")
	@ApiModelProperty("最终押金比例")
	private BigDecimal deposit;//          0.05

	@Column(name = "budget_cost")
	@Desc(value = "精算多少钱一平方")
	@ApiModelProperty("精算多少钱一平方")
	private BigDecimal budgetCost;//budgetcost       3.5
}