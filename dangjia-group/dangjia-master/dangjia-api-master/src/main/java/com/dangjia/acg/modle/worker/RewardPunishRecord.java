package com.dangjia.acg.modle.worker;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 工人奖罚记录
 * 原表 WorkerRewardAndPunishRecord
 */
@Data
@Entity
@Table(name = "dj_worker_reward_punish_record")
@FieldNameConstants(prefix = "")
public class RewardPunishRecord extends BaseEntity {


    @Column(name = "member_id")
    @Desc(value = "账户id")
    @ApiModelProperty("账户id")
    private String memberId;

    @Column(name = "reward_punish_correlation_id")
    @Desc(value = "奖罚条件id")
    @ApiModelProperty("奖罚条件id")
    private String rewardPunishCorrelationId;

    @Column(name = "operator_id")
    @Desc(value = "操作人id")
    @ApiModelProperty("操作人id")
    private String operatorId;

    @Column(name = "house_id")
    @Desc(value = "房子Id")
    @ApiModelProperty("房子Id")
    private String houseId;    //houseid

    @Column(name = "type")
    @Desc(value = "奖罚类型")
    @ApiModelProperty("奖罚类型")
    private Integer type;//0:奖励;1:处罚

    @Column(name = "remarks")
    @Desc(value = "奖罚备注")
    @ApiModelProperty("奖罚备注")
    private String remarks;

    @Column(name = "state")
    @Desc(value = "启用状态")
    @ApiModelProperty("启用状态")
    private Integer state;//0:启用;1:不启用

    @Column(name = "complain_id")
    @Desc(value = "申诉ID")
    @ApiModelProperty("申诉ID")
    private String complainId;


}
