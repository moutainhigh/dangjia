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
import java.util.Date;

/**
 * 实体类  供应退货单-退材料
 */
@Data
@Entity
@Table(name = "dj_repair_mend_deliver")
@ApiModel(description = "供应退货单")
@FieldNameConstants(prefix = "")
public class MendDeliver extends BaseEntity {

	@Column(name = "number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String number;

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "mend_order_id")
	@Desc(value = "要补退订单Id")
	@ApiModelProperty("要补退订单Id")
	private String mendOrderId;

	@Column(name = "total_amount")
	@Desc(value = "退货单总额")
	@ApiModelProperty("退货单总额")
	private Double totalAmount;


	@Column(name = "delivery_fee")
	@Desc(value = "运费 预留")
	@ApiModelProperty("运费 预留")
	private Double deliveryFee;

	@Column(name = "apply_money")
	@Desc(value = "供应商申请结算的价格")
	@ApiModelProperty("供应商申请结算的价格")
	private Double applyMoney;

	@Column(name = "apply_money")
	@Desc(value = "供应商申请结算的状态：0未结算；1已结算")
	@ApiModelProperty("供应商申请结算的状态：0未结算；1已结算")
	private Integer applyState;

	@Column(name = "reason")
	@Desc(value = "不同意理由")
	@ApiModelProperty("不同意理由")
	private String reason;

	@Column(name = "ship_name")
	@Desc(value = "退货人姓名")
	@ApiModelProperty("收货人姓名")
	private String shipName;//

	@Column(name = "ship_mobile")
	@Desc(value = "退货手机")
	@ApiModelProperty("收货手机")
	private String shipMobile;//

	@Column(name = "ship_address")
	@Desc(value = "退货工地")
	@ApiModelProperty("退货工地")
	private String shipAddress;//

	@Column(name = "supplier_id")
	@Desc(value = "供应商id")
	@ApiModelProperty("供应商id")
	private String supplierId;//

	@Column(name = "supplier_telephone")
	@Desc(value = "供应商联系电话")
	@ApiModelProperty("供应商联系电话")
	private String supplierTelephone;//

	@Column(name = "supplier_name")
	@Desc(value = "供应商供应商名称")
	@ApiModelProperty("供应商供应商名称")
	private String supplierName;//

	@Column(name = "memo")
	@Desc(value = "附言 可编辑")
	@ApiModelProperty("附言 可编辑")
	private String memo;//

	@Column(name = "operator_id")
	@Desc(value = "操作确认退货人id")
	@ApiModelProperty("操作确认退货人id")
	private String operatorId;


	@Column(name = "back_time")
	@Desc(value = "大管家确认退货时间")
	@ApiModelProperty("大管家确认退货时间")
	private Date backTime;

	@Column(name = "submit_time")
	@Desc(value = "材料员提交时间")
	@ApiModelProperty("材料员提交时间")
	private Date submitTime;

	@Column(name = "shipping_state")
	@Desc(value = "退货状态（0大管家待确认,1已确认,2已结算,3取消")
	@ApiModelProperty("退货状态（0大管家待确认,1已确认,2已结算,3取消")
	private Integer shippingState;

}