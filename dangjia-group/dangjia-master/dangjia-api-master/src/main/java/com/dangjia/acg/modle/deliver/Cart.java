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

/**
 *  订单明细
 */
@Data
@Entity
@Table(name = "dj_deliver_cart")
@ApiModel(description = "要货购物车表")
@FieldNameConstants(prefix = "")
public class Cart extends BaseEntity {


	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "member_id")
	@Desc(value = "用户ID")
	@ApiModelProperty("用户ID")
	private String memberId;//memberid

	@Column(name = "worker_type_id")
	@Desc(value = "工种类型ID")
	@ApiModelProperty("工种类型ID")
	private String workerTypeId;

	@Column(name = "type")
	@Desc(value = "0仓库商品 1新增商品")
	@ApiModelProperty("0仓库商品 1新增商品")
	private String type;

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

	@Column(name = "price")
	private Double price;// 销售价

	@Column(name = "shop_count")
	private Double shopCount;//购买总数

	@Column(name = "unit_name")
	private String unitName;//单位

	@Column(name = "category_id")
	private String categoryId;//分类id
}