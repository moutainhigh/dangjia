package com.dangjia.acg.dto.worker;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实体类 - 工人积分排行
 */
@Data
public class WorkerRunkDTO {

	@ApiModelProperty("工人ID")
	private String workerId;


	@ApiModelProperty("工人头像")
	private String workerHead;


	@ApiModelProperty("工人名称")
	private String workerName;


	@ApiModelProperty("工人排行")
	private Integer rankNo;


	@ApiModelProperty("积分")
	private BigDecimal integral;

}