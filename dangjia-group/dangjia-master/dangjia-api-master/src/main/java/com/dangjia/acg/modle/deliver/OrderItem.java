package com.dangjia.acg.modle.deliver;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *  订单明细
 */
@Data
@Entity
@Table(name = "dj_deliver_order_item")
@ApiModel(description = "订单明细")
@FieldNameConstants(prefix = "")
public class OrderItem extends BaseEntity {

	@Column(name = "order_id")
	@Desc(value = "订单ID")
	@ApiModelProperty("订单ID")
	private String orderId;

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "product_id")
	@Desc(value = "货品id")
	@ApiModelProperty("货品id")
	private String productId;

	@Column(name = "product_sn")
	@Desc(value = "货品编号")
	@ApiModelProperty("货品编号")
	private String productSn;

	@Column(name = "product_name")
	@Desc(value = "货品名称")
	@ApiModelProperty("货品名称")
	private String productName;

	@Column(name = "product_nick_name")
	private String productNickName;//货品昵称

	@Column(name = "price")
	private Double price;// 销售价

	@Column(name = "cost")
	private Double cost;// 成本价

	@Column(name = "shop_count")
	private Double shopCount;//购买总数

	@Column(name = "unit_name")
	private String unitName;//单位

	@Column(name = "total_price")
	private Double totalPrice; //总价

	@Column(name = "product_type")
	private Integer productType; //0：材料；1：包工包料

	@Column(name = "category_id")
	private String categoryId;//分类id

	@Column(name = "image")
	private String image;//图片

	@Column(name = "worker_goods_name")
	@Desc(value = "人工商品名称")
	@ApiModelProperty("人工商品名称")
	private String workerGoodsName;//人工商品name

	@Column(name = "worker_goods_sn")
	@Desc(value = "人工商品编号")
	@ApiModelProperty("人工商品编号")
	private String workerGoodsSn;//人工商品编号

	@Column(name = "worker_goods_id")
	@Desc(value = "人工商品id")
	@ApiModelProperty("人工商品id")
	private String workerGoodsId;

	public void initPath(String address){
		this.image = StringUtils.isEmpty(this.image)?null:address+this.image;
	}

}