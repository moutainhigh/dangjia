package com.dangjia.acg.modle.house;

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
 * 实体类 - 材料仓库统计
 */
@Data
@Entity
@Table(name = "dj_house_warehouse")
@ApiModel(description = "材料仓库统计")
@FieldNameConstants(prefix = "")
public class Warehouse extends BaseEntity {

	@Column(name = "city_id")
	@Desc(value = "城市id")
	@ApiModelProperty("城市id")
	private String cityId;

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "shop_count")
	@Desc(value = "买总数")
	@ApiModelProperty("买总数")
	private Double shopCount; //买总数 = repairCount + stayCount + robCount

	@Column(name = "repair_count")
	@Desc(value = "补总数")
	@ApiModelProperty("补总数")
	private Double repairCount;

	@Column(name = "stay_count")
	@Desc(value = "待付款进来总数")
	@ApiModelProperty("待付款进来总数")
	private Double stayCount;

	@Column(name = "rob_count")
	@Desc(value = "抢单任务进来总数")
	@ApiModelProperty("抢单任务进来总数")
	private Double robCount;

	@Column(name = "budget_count")
	@Desc(value = "精算总数")
	@ApiModelProperty("精算总数")
	private Double budgetCount;

	@Column(name = "ask_count")
	@Desc(value = "已要总数")
	@ApiModelProperty("已要总数")
	private Double askCount;

	@Column(name = "receive")
	@Desc(value = "收货总数")
	@ApiModelProperty("收货总数")
	private Double receive;//收货总数

	@Column(name = "back_count")
	@Desc(value = "退总数")
	@ApiModelProperty("退总数")
	private Double backCount;

	@Column(name = "product_id")
	@Desc(value = "货品id")
	@ApiModelProperty("货品id")
	private String productId;

	@Column(name = "product_sn")
	@Desc(value = "货号编号")
	@ApiModelProperty("货号编号")
	private String productSn;

	@Column(name = "product_name")
	@Desc(value = "货号名称")
	@ApiModelProperty("货号名称")
	private String productName;

	@Column(name = "price")
	@Desc(value = "销售价")
	@ApiModelProperty("销售价")
	private Double price;

	@Column(name = "cost")
	@Desc(value = "成本价")
	@ApiModelProperty("成本价")
	private Double cost;

	@Column(name = "unit_name")
	@Desc(value = "单位")
	@ApiModelProperty("单位")
	private String unitName;

	@Column(name = "product_type")
	@Desc(value = "0：材料；1：包工包料")
	@ApiModelProperty("0：材料；1：包工包料")
	private Integer productType; //0：材料；1：包工包料

	@Column(name = "category_id")
	@Desc(value = "分类id")
	@ApiModelProperty("分类id")
	private String categoryId;

	@Column(name = "image")
	@Desc(value = "货品图片")
	@ApiModelProperty("货品图片")
	private String image;

	@Column(name = "pay_time")
	@Desc(value = "支付次数")
	@ApiModelProperty("支付次数")
	private Integer payTime;//支付次数

	@Column(name = "ask_time")
	@Desc(value = "要货次数")
	@ApiModelProperty("要货次数")
	private Integer askTime;//要货次数

	@Column(name = "rep_time")
	@Desc(value = "补次数")
	@ApiModelProperty("补次数")
	private Integer repTime;

	@Column(name = "back_time")
	@Desc(value = "退次数")
	@ApiModelProperty("退次数")
	private Integer backTime;//退次数

	@Column(name = "work_back")
	@Desc(value = "工人退")
	@ApiModelProperty("工人退")
	private Double workBack;//工人退

	@Column(name = "owner_back")
	@Desc(value = "业主退")
	@ApiModelProperty("业主退")
	private Double ownerBack;//业主退

	public void initPath(String address){
		this.image = StringUtils.isEmpty(this.image)?null:address+this.image;
	}
}