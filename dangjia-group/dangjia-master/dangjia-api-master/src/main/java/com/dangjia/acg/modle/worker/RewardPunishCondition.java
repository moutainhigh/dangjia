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
 * 工人奖罚条件管理
 *  WorkerRewardAndPunishCondition
 */
@Data
@Entity
@Table(name = "dj_worker_reward_punish_condition")
@ApiModel(description = "工人奖罚条件管理")
public class RewardPunishCondition extends BaseEntity {

    @Column(name = "condition_number")
    @Desc(value = "奖罚条件1为金额2为积分3不能抢单4又不能提现又不能抢单")
    @ApiModelProperty("奖罚条件1为金额2为积分3不能抢单4又不能提现又不能抢单")
	private Integer conditionNumber;//conditionnumber

    @Column(name = "state")
    @Desc(value = "状态1为启用2为停用")
    @ApiModelProperty("状态1为启用2为停用")
	private Integer state;//

    @Column(name = "units")
    @Desc(value = "奖罚条件单位")
    @ApiModelProperty("奖罚条件单位")
	private String units; //

    @Column(name = "type")
    @Desc(value = "奖罚类型1奖励2处罚选择帐户时2为限制帐号")
    @ApiModelProperty("奖罚类型1奖励2处罚选择帐户时2为限制帐号")
	private Integer type; //

    @Column(name = "quantity")
    @Desc(value = "奖罚数量")
    @ApiModelProperty("奖罚数量")
	private int quantity;
}
