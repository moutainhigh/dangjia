package com.dangjia.acg.modle.worker;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  奖罚条件明细
 *  WorkerRewardAndPunishCondition
 */
@Data
@Entity
@Table(name = "dj_worker_reward_punish_condition")
@ApiModel(description = "奖罚条件明细")
@FieldNameConstants(prefix = "")
public class RewardPunishCondition extends BaseEntity {

    @Column(name = "reward_punish_correlation_id")
    @Desc(value = "奖罚条件id")
    @ApiModelProperty("奖罚条件id")
    private String rewardPunishCorrelationId;//

    @Column(name = "name")
    @Desc(value = "奖罚条件明细名称")
    @ApiModelProperty("奖罚条件明细名称")
	private String name;

    @Column(name = "type")
    @Desc(value = "奖罚类型1积分;2钱;3限制接单;4冻结账号（去除）")
    @ApiModelProperty("奖罚类型1积分;2钱;3限制接单;4冻结账号（去除）")
	private Integer type;

    @Column(name = "quantity")
    @Desc(value = "奖罚数量")
    @ApiModelProperty("奖罚数量")
	private BigDecimal quantity;

    @Column(name = "units")
    @Desc(value = "奖罚条件明细单位")
    @ApiModelProperty("奖罚条件明细单位")
    private String units;

    @Column(name = "start_time")
    @Desc(value = "开始时间(时间类型的处罚)")
    @ApiModelProperty("开始时间(时间类型的处罚)")
    private Date startTime;

    @Column(name = "end_time")
    @Desc(value = "结束时间(时间类型的处罚)")
    @ApiModelProperty("结束时间(时间类型的处罚)")
    private Date endTime;
}
