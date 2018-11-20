package com.dangjia.acg.modle.worker;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类  自定义工人流水管理
 */
@Data
@Entity
@Table(name = "dj_worker_defined_worker")
@ApiModel(description = "自定义工人流水管理")
public class DefinedWorker extends BaseEntity {

	@Column(name = "defined_name")
	@Desc(value = "自定义流水名称")
	@ApiModelProperty("自定义流水名称")
	private String definedName;//definedname
}