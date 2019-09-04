package com.dangjia.acg.dto.sale.rob;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

/**
 * 新增沟通记录信息
 */
@Data
@ApiModel(description = "沟通记录详情 ")
@Entity
public class CustomerRecDTO {

	@ApiModelProperty("业主id")
	private String memberId;

	@ApiModelProperty("当前客服id")
	private String userId;

	@ApiModelProperty("沟通描述")
	private String describes;


//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone="GMT+8")
    @ApiModelProperty("提醒时间")
    private String remindTime;

	@ApiModelProperty("线索ID")
	private String clueId;

	@ApiModelProperty("阶段 0:线索阶段 1:客户阶段")
	private Integer phaseStatus;
}