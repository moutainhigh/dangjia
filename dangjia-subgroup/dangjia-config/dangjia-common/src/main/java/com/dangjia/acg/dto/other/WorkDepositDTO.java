package com.dangjia.acg.dto.other;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 实体类 - 结算比例表
 */
@Data
@ApiModel(description = "结算比例表")
public class WorkDepositDTO {

	@ApiModelProperty("普通工种每日完工支付的钱")
	private BigDecimal everydayPay;// 100

	@ApiModelProperty("每日完工支付上限百分比")
	private BigDecimal limitPay;//    0.30

	@ApiModelProperty("阶段完工比例百分比")
	private BigDecimal stagePay;//  0.45

	@ApiModelProperty("整体完工比例")
	private BigDecimal wholePay;//   0.25



	//大管家配置
	@ApiModelProperty("周计划")
	private BigDecimal weekPlan;//   0.10

	@ApiModelProperty("巡查")
	private BigDecimal patrol;//   0.15

	@ApiModelProperty("验收")
	private BigDecimal tested;//   0.20

	@ApiModelProperty("竣工")
	private BigDecimal completed;//   0.55


}