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
 *  奖罚条件
 *  原表 WorkerRewardAndPunishCorrelation
 */
@Data
@Entity
@Table(name = "dj_worker_reward_punish_correlation")
@ApiModel(description = "奖罚条件")
public class RewardPunishCorrelation extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "奖罚条件名称")
	@ApiModelProperty("奖罚条件名称")
	private String name;

	@Column(name = "content")
	@Desc(value = "详细描述")
	@ApiModelProperty("详细描述")
	private String content;

	@Column(name = "type")
	@Desc(value = "奖罚类型")
	@ApiModelProperty("奖罚类型")
	private Integer type;//0:奖励;1:处罚

	@Column(name = "state")
	@Desc(value = "启用状态")
	@ApiModelProperty("启用状态")
	private Integer state;//0:启用;1:不启用

}
