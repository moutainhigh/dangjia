package com.dangjia.acg.modle.activity;


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
 * 实体类 - 活动用户推送模板
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_activity_user_template")
@ApiModel(description = "活动用户推送模板")
public class ActivityUserTemplate extends BaseEntity {


	@Column(name = "name")
	@Desc(value = "模板名称")
	@ApiModelProperty("模板名称")
	private String name;


	@Column(name = "num")
	@Desc(value = "人数")
	@ApiModelProperty("人数")
	private Integer num;


	@Column(name = "members")
	@Desc(value = "模板成员组，多个以逗号分隔")
	@ApiModelProperty("模板成员组，多个以逗号分隔")
	private String members;

}