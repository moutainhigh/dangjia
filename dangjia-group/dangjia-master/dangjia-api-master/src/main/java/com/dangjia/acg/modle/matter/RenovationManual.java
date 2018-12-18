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
 * 实体类 - 装修指南
 */
@Data
@Entity
@Table(name = "dj_matter_renovation_manual")
@ApiModel(description = "装修指南")
public class RenovationManual extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "名称")
	@ApiModelProperty("名称")
	private String name;

	@Column(name = "worker_type_id")
	@Desc(value = "工种id")
	@ApiModelProperty("工种id")
	private String workerTypeId;//workertyid

	@Column(name = "url_name")
	@Desc(value = "链接名称")
	@ApiModelProperty("链接名称")
	private String urlName;//

	@Column(name = "url")
	@Desc(value = "链接地址")
	@ApiModelProperty("链接地址")
	private String url;//

	@Column(name = "types")
	@Desc(value = "装修类型")
	@ApiModelProperty("装修类型")
	private String types;//

	@Column(name = "state")
	@Desc(value = "状态0:可用；1:不可用")
	@ApiModelProperty("状态0:可用；1:不可用")
	private Integer state;//

	@Column(name = "order_number")
	@Desc(value = "排序序号")
	@ApiModelProperty("排序序号")
	private Integer orderNumber;//

}