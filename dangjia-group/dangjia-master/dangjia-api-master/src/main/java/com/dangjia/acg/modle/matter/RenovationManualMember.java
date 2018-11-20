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
 * 实体类 - 装修指南业主勾选记录表
 * 原表名 RenovationManualAndMember
 */
@Data
@Entity
@Table(name = "dj_matter_renovation_manual_member")
@ApiModel(description = "装修指南业主勾选记录表")
public class RenovationManualMember extends BaseEntity {

	@Column(name = "renovation_manual_id")
	@Desc(value = "装修指南id")
	@ApiModelProperty("装修指南id")
	private String renovationManualId;//rId

	@Column(name = "state")
	@Desc(value = "状态0:未勾选；1:已勾选")
	@ApiModelProperty("状态0:未勾选；1:已勾选")
	private int state;//

	@Column(name = "house_id")
	@Desc(value = "房子id")
	@ApiModelProperty("房子id")
	private String houseId;//

	@Column(name = "member_id")
	@Desc(value = "业主id")
	@ApiModelProperty("业主id")
	private String memberId;//
	
}