package com.dangjia.acg.modle.repair;

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
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;

/**
 * 实体类 -补退材料表
 * 原表名 AppBudgetMaterialReplenishment
 */
@Data
@Entity
@Table(name = "dj_repair_mend_materiel")
@ApiModel(description = "补退材料表")
@FieldNameConstants(prefix = "")
public class MendMateriel extends BaseEntity {

	@Column(name = "city_id")
	@Desc(value = "城市id")
	@ApiModelProperty("城市id")
	private String cityId;

	@Column(name = "mend_order_id")
	@Desc(value = "补退单Id")
	@ApiModelProperty("补退单Id")
	private String mendOrderId;

	@Column(name = "product_id")
	@Desc(value = "货号ID")
	@ApiModelProperty("货号ID")
	private String productId;

	@Column(name = "product_sn")
	@Desc(value = "货号编号")
	@ApiModelProperty("货号编号")
	private String productSn;

	@Column(name = "product_name")
	@Desc(value = "货号名称")
	@ApiModelProperty("货号名称")
	private String productName;

	@Column(name = "product_nick_name")
	private String productNickName;//货品昵称

	@Column(name = "price")
	@Desc(value = "销售单价")
	@ApiModelProperty("销售单价")
	private Double price;

	@Column(name = "cost")
	@Desc(value = "成本单价（供应单价）")
	@ApiModelProperty("成本单价（供应单价）")
	private Double cost;// 成本价

	@Column(name = "unit_name")
	@Desc(value = "单位")
	@ApiModelProperty("单位")
	private String unitName;

	@Column(name = "shop_count")
	@Desc(value = "总数")
	@ApiModelProperty("总数")
	private Double shopCount;

	@Column(name = "total_price")
	private Double totalPrice; //总价G

	@Column(name = "product_type")
	private Integer productType; //0：材料；1：服务；2：人工；3：体验；4：增值；5：维保

	@Column(name = "category_id")
	private String categoryId;//分类id

	@Column(name = "image")
	private String image;//图片

	@Column(name = "supplier_id")
	@Desc(value = "供应商id")
	@ApiModelProperty("供应商id")
	private String supplierId;//

	@Column(name = "supplier_telephone")
	@Desc(value = "供应商联系电话")
	@ApiModelProperty("供应商联系电话")
	private String supplierTelephone;//

	@Column(name = "supplier_name")
	@Desc(value = "供应商供应商名称")
	@ApiModelProperty("供应商供应商名称")
	private String supplierName;//

	@Column(name = "repair_mend_deliver_id")
	@Desc(value = "供应商退货单号")
	@ApiModelProperty("供应商退货单号")
	private String repairMendDeliverId;//


	@Column(name = "actual_count")
	@Desc(value = "实际(收、退货数）")
	@ApiModelProperty("实际(收、退货数")
	private Double actualCount;//

	@Column(name = "actual_price")
	@Desc(value = "实际总价")
	@ApiModelProperty("实际总价")
	private Double actualPrice;//

	@Transient
	private String brandName;

	public void initPath(String address){
		this.image = StringUtils.isEmpty(this.image)?null:address+this.image;
	}


	@Column(name = "storefront_id")
	@Desc(value = "店铺ID")
	@ApiModelProperty("店铺ID")
	private String storefrontId;

	@Column(name = "order_item_id")
	@Desc(value = "地址ID")
	@ApiModelProperty("地址ID")
	private String orderItemId;

	@Column(name = "stevedorage_cost")
	@Desc(value = "店铺ID")
	@ApiModelProperty("店铺ID")
	private Double stevedorageCost;//搬运费

	@Column(name = "transportation_cost")
	@Desc(value = "店铺ID")
	@ApiModelProperty("店铺ID")
	private Double transportationCost;//运费

	@Transient
	private Double receive;//收货总数

	@Transient
	List<Map<String,Object>> supplierIdList;//匹配那些供应商

	@Transient
	private String isNonPlatformSupplier;

	@Column(name = "sup_stevedorage_cost")
	@Desc(value = "供应商搬运费")
	@ApiModelProperty("供应商搬运费")
	private Double supStevedorageCost;

	@Column(name = "sup_transportation_cost")
	@Desc(value = "供应商运费")
	@ApiModelProperty("供应商运费")
	private Double supTransportationCost;

}