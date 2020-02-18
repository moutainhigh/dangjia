package com.dangjia.acg.modle.core;

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

/**
 * 实体类 - 把工人和工序关联起来，工人订单表
 */
@Data
@Entity
@Table(name = "dj_core_house_worker")
@ApiModel(description = "工人订单表")
@FieldNameConstants(prefix = "")
public class HouseWorker extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "worker_id")
	@Desc(value = "工人ID")
	@ApiModelProperty("工人ID")
	private String workerId;//workerid

	@Column(name = "worker_type_id")
	@Desc(value = "工种ID")
	@ApiModelProperty("工种ID")
	private String workerTypeId;//workertyid

	@Column(name = "worker_type")
	@Desc(value = "工种类型")
	@ApiModelProperty("工种类型")
	private Integer workerType;//workertype

	@Column(name = "work_type")
	@Desc(value = "抢单状态:1已抢单等待被采纳,2被管家换,3被平台换,4已支付被平台换,5拒单(工匠主动拒绝)，6被采纳支付,7抢单后放弃,8已完成")
	@ApiModelProperty("抢单状态:1已抢单等待被采纳,2被管家换,3被平台换,4已支付被平台换,5拒单(工匠主动拒绝)，6被采纳支付,7抢单后放弃,8已完成")
	private Integer workType; //worktype

	@Column(name = "is_select")
	@Desc(value = "是否选中当前任务(0:未选中；1：选中)")
	@ApiModelProperty("是否选中当前任务(0:未选中；1：选中)")
	private Integer isSelect;


	@Column(name = "patrol")
	@Desc(value = "总巡查次数，大管家时使用")
	@ApiModelProperty("总巡查次数，大管家时使用")
	private Integer patrol;



	@Column(name = "business_id")
	@Desc(value = "业务ID（houseFlowId或验房ID或维修ID等等）")
	@ApiModelProperty("业务ID（houseFlowId或验房ID或维修ID等等）")
	private String businessId;

	@Column(name = "type")
	@Desc(value = "0:装修单，1:体验单，2，维修单")
	@ApiModelProperty("0:装修单，1:体验单，2，维修单")
	private Integer type;

	@Column(name = "order_id")
	@Desc(value = "对应业主支付订单ID")
	@ApiModelProperty("对应业主支付订单ID")
	private String orderId;

	@Column(name = "price")
	@Desc(value = "工钱")
	@ApiModelProperty("工钱")
	private BigDecimal price;


}