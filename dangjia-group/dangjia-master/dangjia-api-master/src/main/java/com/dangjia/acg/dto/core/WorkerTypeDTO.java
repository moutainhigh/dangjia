package com.dangjia.acg.dto.core;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 工种
 */
@Data
public class WorkerTypeDTO {

	private String workerTypeId;

	private String id;

	@ApiModelProperty("工种名设计师，精算师，大管家,拆除,水电工，泥工,木工，油漆工")
	private String name;

	@ApiModelProperty("1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
	private int type;

	@ApiModelProperty("0可用排期，2禁用")
	private Integer state;

	@ApiModelProperty("可抢单数")
	private int methods;

	@ApiModelProperty("默认排期")
	private int sort;

	@ApiModelProperty("1启用,0不启用")
	private int safeState;//safestate

	@ApiModelProperty("标准巡查次数")
	private Integer inspectNumber;//inspectnumber

	@ApiModelProperty("图标")
	private String image;

	@ApiModelProperty("颜色")
	private String color;
}