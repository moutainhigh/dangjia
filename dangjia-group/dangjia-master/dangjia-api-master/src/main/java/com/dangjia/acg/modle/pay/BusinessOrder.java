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
 * 业务订单
 */
@Data
@Entity
@Table(name = "dj_pay_business_order")
@ApiModel(description = "房子")
public class BusinessOrder extends BaseEntity {

	@Column(name = "member_id")
	@Desc(value = "用户ID")
	@ApiModelProperty("用户ID")
	private String memberId;//memberid

	@Column(name = "number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String number;//

	@Column(name = "red_packet_pay_money_id")
	@Desc(value = "优惠顶级单号")
	@ApiModelProperty("优惠顶级单号")
	private String redPacketPayMoneyId;//

	@Column(name = "pay_order_number")
	@Desc(value = "支付订单号")
	@ApiModelProperty("支付订单号")
	private String payOrderNumber;//

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "houseflow_ids")
	@Desc(value = "houseflowid json串")
	@ApiModelProperty("houseflowid json串")
	private String houseflowIds;//

	@Column(name = "state")
	@Desc(value = "处理状态  1刚生成(可编辑),2去支付(不修改),3已支付")
	@ApiModelProperty("处理状态  1刚生成(可编辑),2去支付(不修改),3已支付")
	private int state;//

	@Column(name = "total_price")
	@Desc(value = "该订单总价")
	@ApiModelProperty("该订单总价")
	private BigDecimal totalPrice;//

	@Column(name = "discounts_price")
	@Desc(value = "优惠钱")
	@ApiModelProperty("优惠钱")
	private BigDecimal discountsPrice;//

	@Column(name = "pay_price")
	@Desc(value = "实付")
	@ApiModelProperty("实付")
	private BigDecimal payPrice;//
}
