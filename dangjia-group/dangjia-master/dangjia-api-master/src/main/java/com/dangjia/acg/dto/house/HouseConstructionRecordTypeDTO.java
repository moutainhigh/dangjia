package com.dangjia.acg.dto.house;

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
 * 实体类 - 工地记录明细流水表
 */
@Data
public class HouseConstructionRecordTypeDTO{


	@Column(name = "ids")
	@Desc(value = "'来源ID'")
	@ApiModelProperty("来源ID")
	private String ids;

	@Column(name = "text")
	@Desc(value = "类名")
	@ApiModelProperty("类名")
	private String text;

	@Column(name = "week")
	@Desc(value = "周期")
	@ApiModelProperty("周期")
	private String week;


	@Column(name = "apply_type")
	@Desc(value = "进度状态；0其他申请，1阶段完工申请，2整体完工申请")
	@ApiModelProperty("进度状态")
	private Integer applyType;

}