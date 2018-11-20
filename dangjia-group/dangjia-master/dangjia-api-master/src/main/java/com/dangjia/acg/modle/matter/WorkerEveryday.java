package com.dangjia.acg.modle.matter;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 工匠日常事项
 * @author zengQi
 *
 */
@Data
@Entity
@Table(name = "dj_matter_worker_everyday")
@ApiModel(description = "工匠日常事项")
public class WorkerEveryday extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "事项名称")
	@ApiModelProperty("事项名称")
	private String name;//

	@Column(name = "type")
	@Desc(value = "事项类型1开工事项2完工事项")
	@ApiModelProperty("事项类型1开工事项2完工事项")
	private Integer type;//

	@Column(name = "state")
	@Desc(value = "状态1为启用2为停用")
	@ApiModelProperty("状态1为启用2为停用")
	private Integer state;//
}

