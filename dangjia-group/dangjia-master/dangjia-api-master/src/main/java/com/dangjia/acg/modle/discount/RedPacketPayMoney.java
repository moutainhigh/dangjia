package com.dangjia.acg.modle.discount;

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
 * 时间:2018-08-25
 * 实体类-优惠券订单表
 * 
 */
@Data
@Entity
@Table(name = "dj_discount_red_packet_pay_money")
@ApiModel(description = "优惠券订单表")
public class RedPacketPayMoney extends BaseEntity {

	@Column(name = "should_pay_money")
	@Desc(value = "订单应付金额")
	@ApiModelProperty("订单应付金额")
	private BigDecimal shouldPayMoney; //

	@Column(name = "discount_money")
	@Desc(value = "实付优惠金额")
	@ApiModelProperty("实付优惠金额")
	private BigDecimal discountMoney; //

	@Column(name = "business_order_number")
	@Desc(value = "businessOrderNumber")
	@ApiModelProperty("businessOrderNumber")
	private String businessOrderNumber;

	@Column(name = "house_id")
	@Desc(value = "houseId")
	@ApiModelProperty("houseId")
	private String houseId;

	@Column(name = "member_id")
	@Desc(value = "memberId")
	@ApiModelProperty("memberId")
	private String memberId;
	
}
