package com.dangjia.acg.modle.deliver;

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
 * 要货单
 */
@Data
@Entity
@Table(name = "dj_deliver_order_split")
@ApiModel(description = "提交要货单")
@FieldNameConstants(prefix = "")
public class OrderSplit extends BaseEntity {

	@Column(name = "number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String number;

	@Column(name = "mend_number")
	@Desc(value = "补货单号")
	@ApiModelProperty("补货订单号")
	private String mendNumber;

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "apply_status")
	@Desc(value = "后台审核状态：0生成中, 1申请中, 2通过(发给供应商), 3不通过, 4待业主支付,5已撤回")
	@ApiModelProperty("后台审核状态：0生成中, 1申请中, 2通过(发给供应商), 3不通过, 4待业主支付,5已撤回")
	private Integer applyStatus;

	@Column(name = "supervisor_id")
	@Desc(value = "要货人id")
	@ApiModelProperty("要货人id")
	private String supervisorId;//

	@Column(name = "supervisor_name")
	@Desc(value = "要货人姓名")
	@ApiModelProperty("要货人姓名")
	private String supervisorName;//

	@Column(name = "supervisor_tel")
	@Desc(value = "要货人电话")
	@ApiModelProperty("要货人电话")
	private String supervisorTel;

	@Column(name = "worker_type_id")
	@Desc(value = "工种id")
	@ApiModelProperty("工种id")
	private String workerTypeId;
}
























