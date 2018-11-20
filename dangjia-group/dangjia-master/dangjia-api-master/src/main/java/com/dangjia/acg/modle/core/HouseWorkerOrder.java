package com.dangjia.acg.modle.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 实体类 - 工人订单表
 */
@Data
@Entity
@Table(name = "dj_core_house_worker_order")
@ApiModel(description = "工人订单表")
public class HouseWorkerOrder extends BaseEntity {

	@Column(name = "business_order_number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String businessOrderNumber;//

	@Column(name = "member_id")
	@Desc(value = "用户ID")
	@ApiModelProperty("用户ID")
	private String memberId;//memberid

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "house_flow_id")
	@Desc(value = "工序ID")
	@ApiModelProperty("工序ID")
	private String houseFlowId;//houseflowid

	@Column(name = "house_worker_id")
	@Desc(value = "工人与房子关联")
	@ApiModelProperty("工人与房子关联")
	private String houseWorkerId;//houseworkerid

	@Column(name = "worker_id")
	@Desc(value = "工人ID")
	@ApiModelProperty("工人ID")
	private String workerId;//workerid

	@Column(name = "worker_type_id")
	@Desc(value = "工种ID")
	@ApiModelProperty("工种ID")
	private String workerTypeId;//workertypeid

	@Column(name = "worker_type")
	@Desc(value = "工种类型")
	@ApiModelProperty("工种类型")
	private int workerType;//workertype

	@Column(name = "istest")
	@Desc(value = "是否测试 1测试，0不是测试 默认为0")
	@ApiModelProperty("是否测试 1测试，0不是测试 默认为0")
	private int istest;//

	@Column(name = "pay_state")
	@Desc(value = "支付状态0未支付，1已经支付")
	@ApiModelProperty("支付状态0未支付，1已经支付")
	private Integer payState;//paystate

	@Column(name = "get_state")
	@Desc(value = "取现状态 为1表示有未审核提现")
	@ApiModelProperty("取现状态 为1表示有未审核提现")
	private int getState;//getstate

	@Column(name = "payment")
	@Desc(value = "支付方式1微信, 2支付宝,3后台回调")
	@ApiModelProperty("支付方式1微信, 2支付宝,3后台回调")
	private String payment;

	@Column(name = "worker_type_safe_id")
	@Desc(value = "保险类型id")
	@ApiModelProperty("保险类型id")
	private String workerTypeSafeId;//wtsid

	@Column(name = "worker_type_safe_order_id")
	@Desc(value = "保险订单id")
	@ApiModelProperty("保险订单id")
	private String workerTypeSafeOrderId;//wtsoId

	@Column(name = "safe_price")
	@Desc(value = "保险费")
	@ApiModelProperty("保险费")
	private BigDecimal safePrice;//保险费

	@Column(name = "discounts")
	@Desc(value = "优惠金额")
	@ApiModelProperty("优惠金额")
    private BigDecimal discounts;

	@Column(name = "retention_money")
	@Desc(value = "该订单所收的滞留金")
	@ApiModelProperty("该订单所收的滞留金")
	private BigDecimal retentionMoney;//retentionmoney

	@Column(name = "after_change")
	@Desc(value = "换人后剩余钱")
	@ApiModelProperty("换人后剩余钱")
	private BigDecimal afterChange;//afterchange

	@Column(name = "taked_money")
	@Desc(value = "记录红包抵消金额")
	@ApiModelProperty("记录红包抵消金额")
	private BigDecimal takedMoney;//takedmoney

	@Column(name = "total_price")
	@Desc(value = "要支付的总钱工钱+材料")
	@ApiModelProperty("要支付的总钱工钱+材料")
	private BigDecimal totalPrice;//price

	@Column(name = "material_price")
	@Desc(value = "材料钱")
	@ApiModelProperty("材料钱")
	private BigDecimal materialPrice;//totalprice

	@Column(name = "work_price")
	@Desc(value = "工钱")
	@ApiModelProperty("工钱")
	private BigDecimal workPrice;//paymoney

	@Column(name = "repair_price")
	@Desc(value = "补人工钱")
	@ApiModelProperty("补人工钱")
	private BigDecimal repairPrice;//repairprice

	@Column(name = "have_money")
	@Desc(value = "订单已经拿到的钱")
	@ApiModelProperty("订单已经拿到的钱")
	private BigDecimal haveMoney;//havemaoney

	@Column(name = "every_money")
	@Desc(value = "通过每日完工得到的钱，大管家则为每次验收拿的钱")
	@ApiModelProperty("通过每日完工得到的钱，大管家则为每次验收拿的钱")
	private BigDecimal everyMoney;//everydaypaymaoney

	@Column(name = "check_money")
	@Desc(value = "大管家每次巡查得到的钱 累计")
	@ApiModelProperty("大管家每次巡查得到的钱 累计")
	private BigDecimal checkMoney;

	@Column(name = "order_id")
	@Desc(value = "订单id")
	@ApiModelProperty("订单id")
	private String orderId;//orderid

	public HouseWorkerOrder(){
		this.payState = 0;
		this.safePrice = new BigDecimal(0);//保险费
		this.discounts = new BigDecimal(0);
		this.retentionMoney = new BigDecimal(0);//retentionmoney
		this.afterChange = new BigDecimal(0);//afterchange
		this.takedMoney = new BigDecimal(0);//takedmoney
		this.repairPrice = new BigDecimal(0);//repairprice
		this.haveMoney = new BigDecimal(0);//havemaoney
		this.everyMoney = new BigDecimal(0);//everydaypaymaoney
		this.checkMoney = new BigDecimal(0);
		this.workPrice = new BigDecimal(0);
		this.materialPrice = new BigDecimal(0);
		this.totalPrice = new BigDecimal(0);
	}
}