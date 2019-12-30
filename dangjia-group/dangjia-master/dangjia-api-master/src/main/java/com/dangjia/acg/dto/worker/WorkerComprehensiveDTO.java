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
		overall=overall.add(praiseRate);
		overall=overall.add(approved);
		overall=overall.add(attendance);
		overall=overall.add(finish);
		overall=overall.add(punctual);
		return overall;
	}
}