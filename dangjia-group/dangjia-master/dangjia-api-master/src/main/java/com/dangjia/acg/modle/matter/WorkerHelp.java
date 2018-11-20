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
 * 工人帮助
 * @author zengQi
 *
 */
@Data
@Entity
@Table(name = "dj_matter_worker_help")
@ApiModel(description = "工人帮助")
public class WorkerHelp extends BaseEntity {

	@Column(name = "title")
	@Desc(value = "帮助标题")
	@ApiModelProperty("帮助标题")
	private String title; //

	@Column(name = "content")
	@Desc(value = "帮助内容")
	@ApiModelProperty("帮助内容")
	private String content; //

	@Column(name = "state")
	@Desc(value = "状态1为启用2为停用")
	@ApiModelProperty("状态1为启用2为停用")
	private Integer state;//

}
