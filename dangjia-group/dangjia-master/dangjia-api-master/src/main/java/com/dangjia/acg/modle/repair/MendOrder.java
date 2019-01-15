package com.dangjia.acg.modle.repair;

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
 * 实体类 -补人工 补材料 退人工 退材料
 * 原表名 AppBudgetMaterialReplenishmentOrder
 */
@Data
@Entity
@Table(name = "dj_repair_mend_order")
@ApiModel(description = "补退货订单表")
@FieldNameConstants(prefix = "")
public class MendOrder extends BaseEntity {

	@Column(name = "number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String number;

	@Column(name = "business_order_number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String businessOrderNumber;

	@Column(name = "order_name")
	@Desc(value = "订单描述")
	@ApiModelProperty("订单描述")
	private String orderName;

	@Column(name = "house_id")
	@Desc(value = "房间ID")
	@ApiModelProperty("房间ID")
	private String houseId;

	@Column(name = "worker_type_id")
	@Desc(value = "工种ID")
	@ApiModelProperty("工种ID")
	private String workerTypeId;

	@Column(name = "apply_member_id")
	@Desc(value = "申请人id")
	@ApiModelProperty("申请人id")
	private String applyMemberId;

	@Column(name = "type")
	@Desc(value = "0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料")
	@ApiModelProperty("0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料")
	private Integer type;

	@Column(name = "landlord_state")
	@Desc(value = "业主退材料状态")
	@ApiModelProperty("0生成中,1平台审核中,2不通过,3通过")
	private Integer landlordState;

	@Column(name = "material_order_state")
	@Desc(value = "补工序材料状态")
	@ApiModelProperty("0生成中,1业主审核中，2业主审核不通过，3业主已支付")
	private Integer materialOrderState;

	@Column(name = "worker_order_state")
	@Desc(value = "补人工审核状态")
	@ApiModelProperty("0生成中,1工匠审核中，2工匠不同意，3工匠同意即业主审核中，4业主不同意,5业主已支付")
	private Integer workerOrderState;

	@Column(name = "material_back_state")
	@Desc(value = "工匠退剩余材料状态")
	@ApiModelProperty("0生成中,1管家审核中，2管家审核不通过，3管家审核通过即材料员审核，4材料员不通过,5材料员通过即业主审核中,6业主不通过,7业主通过退钱")
	private Integer materialBackState;

	@Column(name = "worker_back_state")
	@Desc(value = "退人工审核状态")
	@ApiModelProperty("0生成中,1业主审核中，2业主审核不通过，3工匠审核通过退款，4工匠不同意")
	private Integer workerBackState;

	@Column(name = "audits_member_id")
	@Desc(value = "审核人id")
	@ApiModelProperty("审核人id")
	private String auditsMemberId;

	@Column(name = "total_amount")
	@Desc(value = "订单总额")
	@ApiModelProperty("订单总额")
	private Double totalAmount;

	@Column(name = "carriage")
	@Desc(value = "运费")
	@ApiModelProperty("运费")
	private Double carriage;
}