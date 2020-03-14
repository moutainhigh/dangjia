package com.dangjia.acg.dto.activity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - 优惠券
 */
@Data
public class ActivityRedPackInfo {


	@ApiModelProperty("优惠券名")
	private String name;

	@ApiModelProperty("来源数据ID")
	private String fromObject;//workertypeid
	@ApiModelProperty("来源数据name")
	private String fromObjectName;//workertypeid

	@ApiModelProperty("来源数据类型 0为人工 1为材料 2为单品,3类别，4货品，5商品，6城市，7店铺")
	private int fromObjectType;//


	@ApiModelProperty("有效开始时间")
	private Date startDate;//fromObjectName

	@ApiModelProperty("有效结束时间")
	private Date endDate;//

	@ApiModelProperty("可用城市ID")
	private String cityId;//

	@ApiModelProperty("发行总数数量")
	private int num;//

	@ApiModelProperty("优惠卷类型：1城市券，2店铺券")
	private Integer sourceType;//

	@ApiModelProperty("单人领取次数 默认1")
	private int receiveCount;


	@ApiModelProperty("优惠券类型 0为减免金额券 1 为折扣券 2代金券")
	private int type; //


	private BigDecimal money;//优惠卷面值

	@ApiModelProperty("满足使用条件的金额数")
	private BigDecimal satisfyMoney;//

}