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

/**
 * 实体类 - 材料订单
 */
@Data
@Entity
@Table(name = "dj_deliver_order")
@ApiModel(description = "材料订单")
public class Order extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "business_order_number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String businessOrderNumber;//

	@Column(name = "order_sn")
	@Desc(value = "订单编号")
	@ApiModelProperty("订单编号")
	private String orderSn;

	@Column(name = "total_amount")
	@Desc(value = "订单总额")
	@ApiModelProperty("订单总额")
	private BigDecimal totalAmount;//

	@Column(name = "ship_name")
	@Desc(value = "收货人姓名")
	@ApiModelProperty("收货人姓名")
	private String shipName;//

	@Column(name = "ship_address")
	@Desc(value = "收货地址")
	@ApiModelProperty("收货地址")
	private String shipAddress;//

	@Column(name = "ship_phone")
	@Desc(value = "收货电话")
	@ApiModelProperty("收货电话")
	private String shipPhone;//

	@Column(name = "ship_mobile")
	@Desc(value = "收货手机")
	@ApiModelProperty("收货手机")
	private String shipMobile;//

	@Column(name = "memo")
	@Desc(value = "附言")
	@ApiModelProperty("附言")
	private String memo;//

	@Column(name = "worker_type_name")
	@Desc(value = "工种名称")
	@ApiModelProperty("工种名称")
	private String workerTypeName;//workertypeName

	@Column(name = "payment")
	@Desc(value = "支付方式1微信, 2支付宝,3后台回调")
	@ApiModelProperty("支付方式1微信, 2支付宝,3后台回调")
	private String payment;//

	@Column(name = "istest")
	@Desc(value = "是否测试, 0不是，1是测试")
	@ApiModelProperty("是否测试, 0不是，1是测试")
	private int istest;//
}