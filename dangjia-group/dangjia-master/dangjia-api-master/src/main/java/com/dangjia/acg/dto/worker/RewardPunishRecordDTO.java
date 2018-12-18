package com.dangjia.acg.dto.worker;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 工人奖罚记录
 *  原表 WorkerRewardAndPunishRecord
 */
@Data
public class RewardPunishRecordDTO {
	protected String id;

	@ApiModelProperty("创建时间")
	protected Date createDate;// 创建日期

	@ApiModelProperty("修改时间")
	protected Date modifyDate;// 修改日期

	@ApiModelProperty("数据状态 0=正常，1=删除")
	protected int dataStatus;

	@ApiModelProperty("账户id")
	private String memberId;

	@ApiModelProperty("账户名称")
	private String memberName;

	@ApiModelProperty("奖罚条件id")
	private String rewardPunishCorrelationId;

	@ApiModelProperty("操作人id")
	private String operatorId;

	@ApiModelProperty("房子Id")
	private String houseId;	//houseid
	@ApiModelProperty("房子名称")
	private String houseName;	//houseid

	@ApiModelProperty("奖罚类型")
	private Integer type;//0:奖励;1:处罚

	@ApiModelProperty("奖罚备注")
	private String remarks;

	@ApiModelProperty("启用状态")
	private Integer state;//0:启用;1:不启用

	private RewardPunishCorrelationDTO correlation;

}
