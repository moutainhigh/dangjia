package com.dangjia.acg.dto.worker;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实体类 - 工人综合分
 */
@Data
public class WorkerComprehensiveDTO {

	@ApiModelProperty("好评度")
	private BigDecimal praiseRate;


	@ApiModelProperty("验收通过度")
	private BigDecimal approved;


	@ApiModelProperty("出勤度")
	private BigDecimal attendance;


	@ApiModelProperty("完成度")
	private BigDecimal finish;


	@ApiModelProperty("准时度")
	private BigDecimal punctual;


	@ApiModelProperty("综合分")
	private BigDecimal overall;


	public BigDecimal getOverall() {
		overall=overall.add(praiseRate==null?new BigDecimal(0):praiseRate);
		overall=overall.add(approved==null?new BigDecimal(0):approved);
		overall=overall.add(attendance==null?new BigDecimal(0):attendance);
		overall=overall.add(finish==null?new BigDecimal(0):finish);
		overall=overall.add(punctual==null?new BigDecimal(0):punctual);
		return overall;
	}
}