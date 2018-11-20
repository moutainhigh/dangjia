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
 * 交底事项
 * @author zengQi
 */
@Data
@Entity
@Table(name = "dj_matter_worker_disclosure")
@ApiModel(description = "交底事项")
public class WorkerDisclosure extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "事项名称")
	@ApiModelProperty("事项名称")
	private String name;//

	@Column(name = "state")
	@Desc(value = "状态1为启用2为停用")
	@ApiModelProperty("状态1为启用2为停用")
	private Integer state;//

	@Column(name = "details")
	@Desc(value = "事项内容")
	@ApiModelProperty("事项内容")
	private String details; //
	
}


