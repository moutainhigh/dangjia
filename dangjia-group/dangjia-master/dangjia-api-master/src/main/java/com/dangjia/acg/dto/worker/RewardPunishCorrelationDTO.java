package com.dangjia.acg.dto.worker;

import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.worker.RewardPunishCondition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *  奖罚条件
 *  原表 WorkerRewardAndPunishCorrelation
 */
@Data
public class RewardPunishCorrelationDTO extends BaseEntity {

	@ApiModelProperty("奖罚条件名称")
	private String name;

	@ApiModelProperty("详细描述")
	private String content;

	@ApiModelProperty("奖罚类型")
	private Integer type;//0:奖励;1:处罚

	@ApiModelProperty("启用状态")
	private Integer state;//0:启用;1:不启用

	private List<RewardPunishCondition> conditionList;

}
