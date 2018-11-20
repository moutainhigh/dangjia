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
 * 工人奖罚关系表
 *  原表 WorkerRewardAndPunishCorrelation
 */
@Data
@Entity
@Table(name = "dj_worker_reward_punish_correlation")
@ApiModel(description = "工人奖罚关系表")
public class RewardPunishCorrelation extends BaseEntity {

	@Column(name = "reward_punish_id")
	@Desc(value = "奖罚id")
	@ApiModelProperty("奖罚id")
	private String rewardPunishId; //workerRewardandPunishid

	@Column(name = "condition_id")
	@Desc(value = "奖罚条件id")
	@ApiModelProperty("奖罚条件id")
	private String conditionId; //

	

}
