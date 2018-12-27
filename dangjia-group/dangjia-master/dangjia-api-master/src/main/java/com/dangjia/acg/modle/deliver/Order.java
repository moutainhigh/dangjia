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
import java.math.BigDecimal;

/**
 * 实体类 - 所有订单
 */
@Data
@Entity
@Table(name = "dj_deliver_order")
@ApiModel(description = "所有订单")
@FieldNameConstants(prefix = "")
public class Order extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "business_order_number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String businessOrderNumber;

	@Column(name = "total_amount")
	@Desc(value = "订单总额")
	@ApiModelProperty("订单总额")
	private BigDecimal totalAmount;

	@Column(name = "worker_type_name")
	@Desc(value = "工种名称")
	@ApiModelProperty("工种名称")
	private String workerTypeName;//workertypeName

	@Column(name = "worker_type_id")
	@Desc(value = "工种id")
	@ApiModelProperty("工种id")
	private String workerTypeId;

	@Column(name = "style_name")
	@Desc(value = "设计风格")
	@ApiModelProperty("设计风格")
	private String styleName;

	@Column(name = "style_price")
	@Desc(value = "风格价格")
	@ApiModelProperty("风格价格")
	private BigDecimal stylePrice;

	@Column(name = "budget_cost")
	@Desc(value = "精算价格")
	@ApiModelProperty("精算价格")
	private BigDecimal budgetCost;

	@Column(name = "type")
	@Desc(value = "1人工订单 2材料订单")
	@ApiModelProperty("1人工订单 2材料订单")
	private Integer type;

	@Column(name = "payment")
	@Desc(value = "支付方式1微信, 2支付宝,3后台回调")
	@ApiModelProperty("支付方式1微信, 2支付宝,3后台回调")
	private String payment;

}