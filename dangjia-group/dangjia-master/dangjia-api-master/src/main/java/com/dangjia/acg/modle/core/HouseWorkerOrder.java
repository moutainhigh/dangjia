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
 * 实体类 - 工人订单记录
 * 主要是钱记录
 */
@Data
@Entity
@Table(name = "dj_core_house_worker_order")
@ApiModel(description = "工人订单记录")
@FieldNameConstants(prefix = "")
public class HouseWorkerOrder extends BaseEntity {

	@Column(name = "business_order_number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String businessOrderNumber;//

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

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
	private Integer workerType;//workertype

	@Column(name = "pay_state")
	@Desc(value = "支付状态0未支付，1已经支付")
	@ApiModelProperty("支付状态0未支付，1已经支付")
	private Integer payState;//paystate

	@Column(name = "safe_price")
	@Desc(value = "保险费")
	@ApiModelProperty("保险费")
	private BigDecimal safePrice;//保险费

	@Column(name = "retention_money")
	@Desc(value = "该订单所收的滞留金")
	@ApiModelProperty("该订单所收的滞留金")
	private BigDecimal retentionMoney;//retentionmoney

	@Column(name = "after_change")
	@Desc(value = "换人后剩余钱")
	@ApiModelProperty("换人后剩余钱")
	private BigDecimal afterChange;//afterchange

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
	@Desc(value = "阶段/整体补人工钱")
	@ApiModelProperty("阶段/整体补人工钱")
	private BigDecimal repairPrice;//repairprice


	@Column(name = "repair_total_price")
	@Desc(value = "补人总工钱")
	@ApiModelProperty("补人总工钱")
	private BigDecimal repairTotalPrice;

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

	public HouseWorkerOrder(){

	}
	public HouseWorkerOrder(Boolean isInit){
		if(isInit) {
			this.payState = 0;
			this.safePrice = new BigDecimal(0);//保险费
			this.retentionMoney = new BigDecimal(0);//retentionmoney
			this.afterChange = new BigDecimal(0);//afterchange
			this.repairPrice = new BigDecimal(0);//repairprice
			this.haveMoney = new BigDecimal(0);//havemaoney
			this.everyMoney = new BigDecimal(0);//everydaypaymaoney
			this.checkMoney = new BigDecimal(0);
			this.workPrice = new BigDecimal(0);
			this.materialPrice = new BigDecimal(0);
			this.totalPrice = new BigDecimal(0);
		}
	}
}