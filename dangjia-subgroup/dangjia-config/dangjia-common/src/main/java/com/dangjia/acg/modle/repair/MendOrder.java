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
import javax.persistence.Transient;

/**
 * 实体类 -补人工 补材料 退人工 退材料
 * 原表名 AppBudgetMaterialReplenishmentOrder
 */
@Data
@Entity
@Table(name = "dj_repair_mend_order")
@ApiModel(description = "补退订单表")
@FieldNameConstants(prefix = "")
public class MendOrder extends BaseEntity {

	@Column(name = "number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String number;

	@Column(name = "change_order_id")
	@Desc(value = "变更单id")
	@ApiModelProperty("变更单id")
	private String changeOrderId;//新增字段 补退人工限制时需要使用

	@Column(name = "image_arr")
	@Desc(value = "照片")
	@ApiModelProperty("照片")
	private String imageArr;

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
	@Desc(value = "类型：（0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料）;5业主退货退款;6系统自动退款退差价")
	@ApiModelProperty("类型：（0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料）;5业主退货退款;6系统自动退款退差价 ")
	private Integer type;

	@Column(name = "state")
	@Desc(value = "状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,6已关闭7，已审核待处理，8部分退货）")
	@ApiModelProperty("0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回")
	private Integer state;

	@Column(name = "total_amount")
	@Desc(value = "订单总额")
	@ApiModelProperty("订单总额")
	private Double totalAmount;

	@Column(name = "carriage")
	@Desc(value = "运费")
	@ApiModelProperty("运费")
	private Double carriage;

	@Column(name = "total_stevedorage_cost")
	@Desc(value = "搬运费")
	@ApiModelProperty("搬运费")
	private Double totalStevedorageCost;


	@Column(name = "actual_total_amount")
	@Desc(value = "实际总价（不含运费）")
	@ApiModelProperty("实际总价（不含运费）")
	private Double actualTotalAmount;

	@Column(name = "room_charge")
	@Desc(value = "量房费用")
	@ApiModelProperty("量房费用")
	private Double roomCharge;

	@Column(name = "storefront_id")
	@Desc(value = "店铺ID")
	@ApiModelProperty("店铺ID")
	private String storefrontId;

	@Column(name = "address_id")
	@Desc(value = "地址ID")
	@ApiModelProperty("地址ID")
	private String addressId;

	@Column(name = "order_id")
	@Desc(value = "订单ID")
	@ApiModelProperty("订单ID")
	private String orderId;

	@Transient
	private String deliverNumber;//退货单号

	@Transient
	private String supplierName;//供应商名称

	@Column(name = "return_reason")
	@Desc(value = "退货原因")
	@ApiModelProperty("退货原因")
	private String returnReason;//运费

}