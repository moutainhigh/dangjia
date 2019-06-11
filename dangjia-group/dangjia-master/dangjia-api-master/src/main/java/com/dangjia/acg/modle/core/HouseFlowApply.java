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
import java.util.Date;

/**
 * 实体类 - 任务进程 申请表
 */
@Data
@Entity
@Table(name = "dj_core_house_flow_apply")
@ApiModel(description = "任务进程/申请表")
@FieldNameConstants(prefix = "")
public class HouseFlowApply extends BaseEntity {

	@Column(name = "house_flow_id")
	@Desc(value = "进程ID")
	@ApiModelProperty("进程ID")
	private String houseFlowId;//houseflowid

	@Column(name = "worker_id")
	@Desc(value = "工人ID")
	@ApiModelProperty("工人ID")
	private String workerId;//workerid

	@Column(name = "worker_type_id")
	@Desc(value = "工种id")
	@ApiModelProperty("工种id")
	private String workerTypeId;//workertyid

	@Column(name = "worker_type")
	@Desc(value = "工种类型")
	@ApiModelProperty("工种类型")
	private Integer workerType;//workertype

	@Column(name = "house_id")
	@Desc(value = "房子/项目ID")
	@ApiModelProperty("房子/项目ID")
	private String houseId;//houseid

	@Column(name = "apply_type")
	@Desc(value = "0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查,8提前结束装修,9提前结束装修申请")
	@ApiModelProperty("0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查,8提前结束装修,9提前结束装修申请")
	private Integer applyType;//applytype

	@Column(name = "suspend_day")
	@Desc(value = "申请停工多少天")
	@ApiModelProperty("申请停工多少天")
	private Integer suspendDay;//suspendDay

	@Column(name = "start_date")
	@Desc(value = "开始时间")
	@ApiModelProperty("管家自动审核倒计时时间")
	private Date startDate;

	@Column(name = "end_date")
	@Desc(value = "结束时间")
	@ApiModelProperty("业主自动审核倒计时时间")
	private Date endDate;

	@Column(name = "apply_dec")
	@Desc(value = "每日描述 审核停工的原因")
	@ApiModelProperty("每日描述 审核停工的原因")
	private String applyDec;//applydec

	@Column(name = "member_check")
	@Desc(value = "用户审核结果,0未审核，1审核通过，2审核不通过，3自动审核，4申述中")
	@ApiModelProperty("用户审核结果,0未审核，1审核通过，2审核不通过，3自动审核，4申述中")
	private Integer memberCheck;//membercheck

	@Column(name = "supervisor_check")
	@Desc(value = "大管家审核结果,0未审核，1审核通过，2审核不通过")
	@ApiModelProperty("大管家审核结果,0未审核，1审核通过，2审核不通过")
	private Integer supervisorCheck;//supervisorcheck

	@Column(name = "apply_money")
	@Desc(value = "申请得到的钱")
	@ApiModelProperty("申请得到的钱")
	private BigDecimal applyMoney;//applymoney

	@Column(name = "pay_state")
	@Desc(value = "0未处理, 1已处理")
	@ApiModelProperty("0未处理, 1已处理")
	private Integer payState;//paystate

	@Column(name = "other_money")
	@Desc(value = "剩下的钱")
	@ApiModelProperty("剩下的钱")
	private BigDecimal otherMoney;//othermoney

	@Column(name = "supervisor_money")
	@Desc(value = "管家钱")
	@ApiModelProperty("管家钱")
	private BigDecimal supervisorMoney;//supervisormoney

	@Column(name = "operator")
	@Desc(value = "操作人ID")
	@ApiModelProperty("操作人ID")
	private String operator;
}