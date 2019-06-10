package com.dangjia.acg.modle.house;

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
@Entity
@Table(name = "dj_house_construction_record")
@ApiModel(description = "工地记录明细流水表")
@FieldNameConstants(prefix = "")
public class HouseConstructionRecord extends BaseEntity {


	@Column(name = "source_id")
	@Desc(value = "'来源ID'")
	@ApiModelProperty("来源ID")
	private String sourceId;

	@Column(name = "source_type")
	@Desc(value = "来源类型 0=申请/进程ID,1=补退订单表")
	@ApiModelProperty("来源类型")
	private Integer sourceType;

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "content")
	@Desc(value = "内容")
	@ApiModelProperty("内容")
	private String content;

	@Column(name = "worker_id")
	@Desc(value = "工人ID")
	@ApiModelProperty("工人ID")
	private String workerId;

	@Column(name = "worker_type")
	@Desc(value = "工种类型")
	@ApiModelProperty("工种类型")
	private Integer workerType;


	@Column(name = "apply_type")
	@Desc(value = "进度状态；0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查,8补人工,9退人工,10补材料,11退材料,12业主退材料")
	@ApiModelProperty("进度状态")
	private Integer applyType;

}