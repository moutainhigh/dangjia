package com.dangjia.acg.modle.deliver;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 拆料订单的拆分单
 */
@Data
@Entity
@Table(name = "dj_deliver_order_split")
@ApiModel(description = "拆分单")
public class OrderSplit extends BaseEntity {

	@Column(name = "shipping_status")
	@Desc(value = "配送状态（刚拆分,提交给供应商,已发货待签收,已收货,已退货,取消,生成拆货单未提交）0,1,2,3,4,5,6")
	@ApiModelProperty("配送状态（刚拆分,提交给供应商,已发货待签收,已收货,已退货,取消,生成拆货单未提交）0,1,2,3,4,5,6")
	private int shippingStatus;//

	@Column(name = "order_id")
	@Desc(value = "材料单ID")
	@ApiModelProperty("材料单ID")
	private String orderId;

	@Column(name = "order_split_sn")
	@Desc(value = "拆分单编号")
	@ApiModelProperty("拆分单编号")
	private String orderSplitSn;//

	@Column(name = "total_product_price")
	@Desc(value = "该订单总价成本价")
	@ApiModelProperty("该订单总价成本价")
	private BigDecimal totalProductPrice;

	@Column(name = "total_price")
	@Desc(value = "总价销售价")
	@ApiModelProperty("总价销售价")
	private BigDecimal totalPrice;

	@Column(name = "delivery_fee")
	@Desc(value = "配送费用可编辑")
	@ApiModelProperty("配送费用可编辑")
	private BigDecimal deliveryFee;

	@Column(name = "apply_money")
	@Desc(value = "供应商申请结算的价格")
	@ApiModelProperty("供应商申请结算的价格")
	private BigDecimal applyMoney;//applymoney

	@Column(name = "apply_status")
	@Desc(value = "供应商申请结算的状态 默认0 1申请中2重新申请(不同意)3通过")
	@ApiModelProperty("供应商申请结算的状态 默认0 1申请中2重新申请(不同意)3通过")
	private int applyStatus;//

	@Column(name = "reason")
	@Desc(value = "不同意理由")
	@ApiModelProperty("不同意理由")
	private String reason;//

	@Column(name = "member_id")
	@Desc(value = "业主ID")
	@ApiModelProperty("业主ID")
	private String memberId;//memberid

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "ship_name")
	@Desc(value = "收货人姓名")
	@ApiModelProperty("收货人姓名")
	private String shipName;//

	@Column(name = "ship_mobile")
	@Desc(value = "收货手机")
	@ApiModelProperty("收货手机")
	private String shipMobile;//

	@Column(name = "ship_address")
	@Desc(value = "收货地址")
	@ApiModelProperty("收货地址")
	private String shipAddress;//

	@Column(name = "worker_type_name")
	@Desc(value = "工种名称")
	@ApiModelProperty("工种名称")
	private String workerTypeName;//workertypeName

	@Column(name = "supplier_classify_id")
	@Desc(value = "供应商分类id")
	@ApiModelProperty("供应商分类id")
	private String supplierClassifyId;

	@Column(name = "supplier_id")
	@Desc(value = "供应商id")
	@ApiModelProperty("供应商id")
	private String supplierId;//

	@Column(name = "supplier_name")
	@Desc(value = "供应商名称")
	@ApiModelProperty("供应商名称")
	private String supplierName;//

	@Column(name = "supplier_tel")
	@Desc(value = "供应商电话")
	@ApiModelProperty("供应商电话")
	private String supplierTel;//

	@Column(name = "notice")
	@Desc(value = "发货须知 关联供应商的")
	@ApiModelProperty("发货须知 关联供应商的")
	private String notice;//

	@Column(name = "memo")
	@Desc(value = "附言 可编辑")
	@ApiModelProperty("附言 可编辑")
	private String memo;//

	@Column(name = "supervisor_id")
	@Desc(value = "大管家id")
	@ApiModelProperty("大管家id")
	private String supervisorId;//

	@Column(name = "supervisor_name")
	@Desc(value = "大管家姓名")
	@ApiModelProperty("大管家姓名")
	private String supervisorName;//

	@Column(name = "supervisor_tel")
	@Desc(value = "大管家电话")
	@ApiModelProperty("大管家电话")
	private String supervisorTel;//

	@Column(name = "send_time")
	@Desc(value = "发货时间")
	@ApiModelProperty("发货时间")
	private Date sendTime; //

	@Column(name = "submit_time")
	@Desc(value = "下单时间")
	@ApiModelProperty("下单时间")
	private Date submitTime; //

	@Column(name = "supervisor_Status")
	@Desc(value = "大管家可收货状态(0:大管家不可收货;1:大管家可收货)")
	@ApiModelProperty("大管家可收货状态(0:大管家不可收货;1:大管家可收货)")
	private int supervisorStatus;//
}
























