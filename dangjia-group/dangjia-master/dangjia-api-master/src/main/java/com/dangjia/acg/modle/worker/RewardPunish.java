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
 * 工人奖罚管理
 * 原表 WorkerRewardAndPunish
 */
@Data
@Entity
@Table(name = "dj_worker_reward_punish")
@ApiModel(description = "工人奖罚管理")
public class RewardPunish extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "奖罚名称")
	@ApiModelProperty("奖罚名称")
	private String name;//

	@Column(name = "state")
	@Desc(value = "状态1为启用2为停用")
	@ApiModelProperty("状态1为启用2为停用")
	private Integer state;//
	
}
