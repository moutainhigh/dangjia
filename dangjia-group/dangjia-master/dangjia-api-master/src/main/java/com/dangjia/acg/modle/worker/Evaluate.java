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

/**
 * 实体类 - 评价记录
 * 
 */
@Data
@Entity
@Table(name = "dj_worker_evaluate")
@ApiModel(description = "评价记录")
@FieldNameConstants(prefix = "")
public class Evaluate extends BaseEntity {

	@Column(name = "content")
	@Desc(value = "内容")
	@ApiModelProperty("内容")
	private String content;//

	@Column(name = "member_id")
	@Desc(value = "用户id")
	@ApiModelProperty("用户id")
	private String memberId;//memberid

	@Column(name = "house_id")
	@Desc(value = "房子id")
	@ApiModelProperty("房子id")
	private String houseId;//houseid

	@Column(name = "butler_id")
	@Desc(value = "大管家id")
	@ApiModelProperty("大管家id")
	private String butlerId;//butlerid

	@Column(name = "star")
	@Desc(value = "星级")
	@ApiModelProperty("星级")
	private Integer star;//星级

	@Column(name = "house_flow_apply_id")
	@Desc(value = "申请id")
	@ApiModelProperty("申请id")
	private String houseFlowApplyId;//houseFlowApplyid

	@Column(name = "house_flow_id")
	@Desc(value = "工序id")
	@ApiModelProperty("工序id")
	private String houseFlowId;//houseFlowid

	@Column(name = "worker_id")
	@Desc(value = "工人id")
	@ApiModelProperty("工人id")
	private String workerId;//workerid

	@Column(name = "worker_name")
	@Desc(value = "工人名字")
	@ApiModelProperty("工人名字")
	private String workerName;//workername

	@Column(name = "state")
	@Desc(value = "1为业主对工人的评价,2为商品评论,3为管家对工人的评价")
	@ApiModelProperty("1为业主对工人的评价,2为商品评论,3为管家对工人的评价")
	private Integer state;//

	@Column(name = "apply_type")
	@Desc(value = "1为阶段完工评价，2为整体完工评价")
	@ApiModelProperty("1为阶段完工评价，2为整体完工评价")
	private Integer applyType;//applytype
}