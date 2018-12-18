package com.dangjia.acg.modle.pay;

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
 * 实体类
 * @author Ronalcheng
 * 支付订单
 */
@Data
@Entity
@Table(name = "dj_pay_pay_order")
@ApiModel(description = "房子")
public class PayOrder extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房子Id")
	@ApiModelProperty("房子Id")
	private String houseId;//houseid

	@Column(name = "number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String number;//

	@Column(name = "business_order_number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String businessOrderNumber;//

	@Column(name = "zhifubao")
	@Desc(value = "支付宝订单号")
	@ApiModelProperty("支付宝订单号")
	private String zhifubao;//

	@Column(name = "weixin")
	@Desc(value = "微信订单号")
	@ApiModelProperty("微信订单号")
	private String weixin;//

	@Column(name = "pay_state")
	@Desc(value = "1微信，2支付宝")
	@ApiModelProperty("1微信，2支付宝")
	private String payState;//paystate

	@Column(name = "state")
	@Desc(value = "0未支付,2已支付")
	@ApiModelProperty("0未支付,2已支付")
	private Integer state;//

	@Column(name = "price")
	@Desc(value = "该笔支付钱")
	@ApiModelProperty("该笔支付钱")
	private BigDecimal price;//

}
