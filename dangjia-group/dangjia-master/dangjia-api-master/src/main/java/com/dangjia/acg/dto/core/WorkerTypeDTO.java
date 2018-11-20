package com.dangjia.acg.dto.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 工种类
 */
@Data
@Entity
@Table(name = "dj_core_worker_type")
@ApiModel(description = "工种")
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

	@ApiModelProperty("工艺说明url")
	private String technical;

	@ApiModelProperty("标准巡查次数")
	private Integer inspectNumber;//inspectnumber

	@ApiModelProperty("颜色")
	private String color;
}