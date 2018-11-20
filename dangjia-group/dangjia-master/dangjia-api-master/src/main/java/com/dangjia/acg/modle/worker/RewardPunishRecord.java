package com.dangjia.acg.modle.worker;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 工人奖罚记录
 *  原表 WorkerRewardAndPunishRecord
 */
@Data
@Entity
@Table(name = "dj_worker_reward_punish_record")
public class RewardPunishRecord extends BaseEntity {

	@Column(name = "worker_id")
	@Desc(value = "工人id")
	@ApiModelProperty("工人id")
	private String workerId;

	@Column(name = "reward_punish_id")
	@Desc(value = "奖罚id")
	@ApiModelProperty("奖罚id")
	private String rewardPunishId; //workerRewardandPunishid

	@Column(name = "reward_punish_name")
	@Desc(value = "奖罚名称")
	@ApiModelProperty("奖罚名称")
	private String rewardPunishName; //workerRewardandPunishname

	@Column(name = "operator")
	@Desc(value = "存放登录人操作人id")
	@ApiModelProperty("存放登录人操作人id")
	private String operator;

	@Column(name = "supervisor_id")
	@Desc(value = "大管家id")
	@ApiModelProperty("大管家id")
	private String supervisorId;//

	@Column(name = "house_id")
	@Desc(value = "房子Id")
	@ApiModelProperty("房子Id")
    private String houseId;	//houseid

	@Column(name = "state")
	@Desc(value = "1为后台2为大管家操作")
	@ApiModelProperty("1为后台2为大管家操作")
	private int state;//

	@Column(name = "remark")
	@Desc(value = "奖罚说明备注")
	@ApiModelProperty("奖罚说明备注")
	private String remark; //

	@Column(name = "money")
	@Desc(value = "0没有1表示奖2表示罚钱")
	@ApiModelProperty("0没有1表示奖2表示罚钱")
	private int money;//

	@Column(name = "point")
	@Desc(value = "0没有1表示奖2表示罚积分")
	@ApiModelProperty("0没有1表示奖2表示罚积分")
	private int point;//

	@Column(name = "grab")
	@Desc(value = "0没有1表示奖2表示罚不能抢单")
	@ApiModelProperty("0没有1表示奖2表示罚不能抢单")
	private int grab;//

	@Column(name = "deposit")
	@Desc(value = "0没有1表示奖2表示罚既不能抢单又不能提现")
	@ApiModelProperty("0没有1表示奖2表示罚既不能抢单又不能提现")
	private int deposit;//

	@Column(name = "grab_expire")
	@Desc(value = "不能抢单到期时间")
	@ApiModelProperty("不能抢单到期时间")
	private Date grabExpire;

	@Column(name = "deposit_expire")
	@Desc(value = "不能提现到期时间")
	@ApiModelProperty("不能提现到期时间")
	private Date depositExpire;
}
