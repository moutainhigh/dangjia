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
 * 实体类 - 商品换货订单
 * @author Yinjianbo
 * @Date 2019-5-11
 */
@Data
@Entity
@Table(name = "dj_deliver_product_change_order")
@ApiModel(description = "商品换货订单")
@FieldNameConstants(prefix = "")
public class ProductChangeOrder extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String number;

	@Column(name = "difference_price")
	@Desc(value = "总价差额")
	@ApiModelProperty("总价差额")
	private BigDecimal differencePrice;

	@Column(name = "type")
	@Desc(value = "0未支付 1已支付 2已退款")
	@ApiModelProperty("0未支付 1已支付 2已退款")
	private Integer type;
}