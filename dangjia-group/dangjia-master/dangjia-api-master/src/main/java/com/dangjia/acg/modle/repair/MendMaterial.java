package com.dangjia.acg.modle.repair;

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
 * 实体类 -补材料表
 * 原表名 AppBudgetMaterialReplenishment
 */
@Data
@Entity
@Table(name = "dj_repair_mend_material")
@ApiModel(description = "补材料表")
public class MendMaterial extends BaseEntity {

	@Column(name = "business_order_number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String businessOrderNumber;

	@Column(name = "house_flow_id")
	@Desc(value = "工序id")
	@ApiModelProperty("工序id")
	private String houseFlowId;//houseflowid

	@Column(name = "house_id")
	@Desc(value = "房间ID")
	@ApiModelProperty("房间ID")
	private String houseId;//houseid

	@Column(name = "member_id")
	@Desc(value = "用户ID")
	@ApiModelProperty("用户ID")
	private String memberId;//memberid

	@Column(name = "worker_type_id")
	@Desc(value = "工种ID")
	@ApiModelProperty("工种ID")
	private String workerTypeId;//worktypeid

	@Column(name = "worker_type")
	@Desc(value = "工种类型")
	@ApiModelProperty("工种类型")
	private int workerType;//worktype

	@Column(name = "mend_order_id")
	@Desc(value = "补订单Id")
	@ApiModelProperty("补订单Id")
	private String mendOrderId;//abmroid

	@Column(name = "delete_state")
	@Desc(value = "用户删除状态·,0表示未删除，1表示已经删除,2表示已经支付,3业主取消")
	@ApiModelProperty("用户删除状态·,0表示未删除，1表示已经删除,2表示已经支付,3业主取消")
	private Integer deleteState;//deletestate

	@Column(name = "goods_id")
	@Desc(value = "商品ID")
	@ApiModelProperty("商品ID")
	private String goodsId;//goodsid

	@Column(name = "goods_sn")
	@Desc(value = "商品编号")
	@ApiModelProperty("商品编号")
	private String goodsSn;//

	@Column(name = "product_id")
	@Desc(value = "货号ID")
	@ApiModelProperty("货号ID")
	private String productId;//productid

	@Column(name = "product_sn")
	@Desc(value = "货号编号")
	@ApiModelProperty("货号编号")
	private String productSn;// productidsn

	@Column(name = "name")
	@Desc(value = "商品名称")
	@ApiModelProperty("商品名称")
	private String name;//

	@Column(name = "price")
	@Desc(value = "销售价")
	@ApiModelProperty("销售价")
	private BigDecimal price;//

	@Column(name = "cost")
	@Desc(value = "成本价")
	@ApiModelProperty("成本价")
	private BigDecimal cost;//

	@Column(name = "introduction")
	@Desc(value = "介绍")
	@ApiModelProperty("介绍")
	private String introduction;//

	@Column(name = "shop_count")
	@Desc(value = "购买总数")
	@ApiModelProperty("购买总数")
	private BigDecimal shopCount;//shopcount

	@Column(name = "unit")
	@Desc(value = "单位")
	@ApiModelProperty("单位")
	private String unit;//

	@Column(name = "total_price")
	@Desc(value = "总价")
	@ApiModelProperty("总价")
	private BigDecimal totalPrice;//totalprice

	@Column(name = "order_state")
	@Desc(value = "1代表申请中，2代表审核不通过，3代表精算审核通过，等待业主支付，6代表业主已")
	@ApiModelProperty("1代表申请中，2代表审核不通过，3代表精算审核通过，等待业主支付，6代表业主已")
	private String orderState;//orderstate

	@Column(name = "order_name")
	@Desc(value = "ordername")
	@ApiModelProperty("ordername")
	private String orderName;//ordername

	@Column(name = "istest")
	@Desc(value = "是否测试, 0不是，1 是测试")
	@ApiModelProperty("是否测试, 0不是，1 是测试")
	private int istest;//
}