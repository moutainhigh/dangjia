package com.dangjia.acg.modle.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

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
@FieldNameConstants(prefix = "")
public class WorkerType extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "工种名设计师，精算师，大管家,拆除,水电工，泥工,木工，油漆工")
	@ApiModelProperty("工种名设计师，精算师，大管家,拆除,水电工，泥工,木工，油漆工")
	private String name;

	@Column(name = "type")
	@Desc(value = "1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
	@ApiModelProperty("1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
	private Integer type;

	@Column(name = "state")
	@Desc(value = "0可用排期，2禁用")
	@ApiModelProperty("0可用排期，2禁用")
	private Integer state;

	@Column(name = "methods")
	@Desc(value = "可抢单数")
	@ApiModelProperty("可抢单数")
	private Integer methods;

	@Column(name = "sort")
	@Desc(value = "默认排期")
	@ApiModelProperty("默认排期")
	private Integer sort;

	@Column(name = "safe_state")
	@Desc(value = "1启用,0不启用")
	@ApiModelProperty("1启用,0不启用")
	private Integer safeState;//safestate

	@Column(name = "inspect_number")
	@Desc(value = " 标准巡查次数")
	@ApiModelProperty("标准巡查次数")
	private Integer inspectNumber;//inspectnumber

	@Column(name = "image")
	@Desc(value = "图标")
	@ApiModelProperty("图标")
	private String image;

	@Column(name = "color")
	@Desc(value = "颜色")
	@ApiModelProperty("颜色")
	private String color;
}