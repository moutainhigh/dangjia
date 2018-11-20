package com.dangjia.acg.modle.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 把工人和工序关联起来，工人订单表
 */
@Data
@Entity
@Table(name = "dj_core_house_worker")
@ApiModel(description = "工序")
public class HouseWorker extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "worker_id")
	@Desc(value = "工人ID")
	@ApiModelProperty("工人ID")
	private String workerId;//workerid

	@Column(name = "house_flow_id")
	@Desc(value = "工序ID")
	@ApiModelProperty("工序ID")
	private String houseFlowId;//houseflowid

	@Column(name = "worker_type_id")
	@Desc(value = "工种ID")
	@ApiModelProperty("工种ID")
	private String workerTypeId;//workertyid

	@Column(name = "worker_type")
	@Desc(value = "工种类型")
	@ApiModelProperty("工种类型")
	private int workerType;//workertype

	@Column(name = "house_worker_order_id")
	@Desc(value = "工人订单")
	@ApiModelProperty("工人订单")
	private String houseWorkerOrderId;//housewokerorderid

	@Column(name = "work_type")
	@Desc(value = "抢单状态:1已抢单等待被采纳,2被换人，3被删除（别人不能再抢）,4已开工被换人,5拒单(工匠主动拒绝)，6被采纳支付,7抢单后放弃")
	@ApiModelProperty("抢单状态:1已抢单等待被采纳,2被换人，3被删除（别人不能再抢）,4已开工被换人,5拒单(工匠主动拒绝)，6被采纳支付,7抢单后放弃")
	private Integer workType; //worktype

	@Column(name = "work_steta")
	@Desc(value = "施工状态,默认0  1阶段完工通过，2整体完工通过 ,3待交底,4已放弃")
	@ApiModelProperty("施工状态,默认0  1阶段完工通过，2整体完工通过 ,3待交底,4已放弃")
	private Integer workSteta;//worksteta

	@Column(name = "evaluate_steta")
	@Desc(value = "业主或大管家对工人的评价状态,0未开始，1已评价，没有评价")
	@ApiModelProperty("业主或大管家对工人的评价状态,0未开始，1已评价，没有评价")
	private Integer evaluateSteta;//evaluatesteta

	@Column(name = "has_evaluate")
	@Desc(value = "用于单独判断业主对工人的评价状态,0未开始，1已评价")
	@ApiModelProperty("用于单独判断业主对工人的评价状态,0未开始，1已评价")
	private int hasEvaluate;//hasevaluate

	@Column(name = "apply")
	@Desc(value = "申请的状态为1 表示没有发起完工申请可退单,默认0")
	@ApiModelProperty("申请的状态为1 表示没有发起完工申请可退单,默认0")
	private Integer apply;//

	@Column(name = "is_select")
	@Desc(value = "是否选中当前任务(0:未选中；1：选中)")
	@ApiModelProperty("是否选中当前任务(0:未选中；1：选中)")
	private Integer isSelect;//

}