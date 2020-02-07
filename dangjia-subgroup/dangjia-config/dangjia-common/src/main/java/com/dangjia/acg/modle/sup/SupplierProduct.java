package com.dangjia.acg.modle.sup;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


/**
 *
 * @类 名： SupplierGoods
 * @功能描述： 供应商关联货品表
 * @作者信息： zmj
 * @创建时间： 2018-9-17下午2:35:58
 */
@Data
@Entity
@Table(name = "dj_sup_supplier_product")
@ApiModel(description = "供应商关联货品表")
@FieldNameConstants(prefix = "")
public class SupplierProduct extends GoodsBaseEntity implements Serializable{

	@Column(name = "product_id")
	@Desc(value = "商品id")
	@ApiModelProperty("商品id")
	private String productId;//商品id

	@Column(name = "supplier_id")
	@Desc(value = "供应商id")
	@ApiModelProperty("供应商id")
	private String supplierId;//供应商id

	@Column(name = "goods_id")
	@Desc(value = "商品id")
	@ApiModelProperty("商品id")
	private String goodsId;//商品id

	@Column(name = "is_cartage_price")
	@Desc(value = "是否收取上楼费（1是，0否）")
	@ApiModelProperty("是否收取上楼费（1是，0否）")
	private String isCartagePrice;//是否收取上楼费（1是，0否）

	@Column(name = "price")
	@Desc(value = "价格")
	@ApiModelProperty("价格")
	private Double price;//价格

	@Column(name = "porterage")
	@Desc(value = "搬运费")
	@ApiModelProperty("搬运费")
	private Double porterage;//价格


	@Column(name = "stock")
	@Desc(value = "库存")
	@ApiModelProperty("库存")
	private Double stock;//库存

	@Column(name = "is_supply")
	@Desc(value = "是否供应")
	@ApiModelProperty("是否供应")
	private Integer isSupply;//是否供应；0停供，1供应

}
