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
 * 材料订单项
 */
@Data
@Entity
@Table(name = "dj_deliver_order_item")
@ApiModel(description = "材料订单")
public class OrderItem extends BaseEntity {

	@Column(name = "goods_sn")
	@Desc(value = "商品编号")
	@ApiModelProperty("商品编号")
	private String goodsSn;

	@Column(name = "product_sn")
	@Desc(value = "商品货号")
	@ApiModelProperty("商品货号")
	private String productSn;

	@Column(name = "product_id")
	@Desc(value = "货号id")
	@ApiModelProperty("货号id")
	private String productId;

	@Column(name = "product_name")
	@Desc(value = "商品名称")
	@ApiModelProperty("商品名称")
	private String productName;

	@Column(name = "product_price")
	@Desc(value = "商品价格")
	@ApiModelProperty("商品价格")
	private BigDecimal productPrice;

	@Column(name = "cost")
	@Desc(value = "成本价")
	@ApiModelProperty("成本价")
	private BigDecimal cost;//

	@Column(name = "product_quantity")
	@Desc(value = "商品下单数量")
	@ApiModelProperty("商品下单数量")
	private Double productQuantity;

	@Column(name = "delivery_quantity")
	@Desc(value = "已发数量")
	@ApiModelProperty("已发数量")
	private Double deliveryQuantity;

	@Column(name = "meta_description")
	@Desc(value = "精算备注")
	@ApiModelProperty("精算备注")
	private String metaDescription;

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "order_id")
	@Desc(value = "订单ID")
	@ApiModelProperty("订单ID")
	private Order orderId;
}