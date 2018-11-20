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
 * 实体类 -补人工
 * 原表名 AppBudgetWorkerReplenishment
 */
@Data
@Entity
@Table(name = "dj_repair_mend_worker")
@ApiModel(description = "补人工")
public class MendWorker extends BaseEntity {

	@Column(name = "business_order_number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String businessOrderNumber;

	@Column(name = "house_flow_id")
	@Desc(value = "工序id")
	@ApiModelProperty("工序id")
	private String houseFlowId; //houseflowid

	@Column(name = "house_id")
	@Desc(value = "房间ID")
	@ApiModelProperty("房间ID")
	private String houseId;//houseid

	@Column(name = "member_id")
	@Desc(value = "用户id")
	@ApiModelProperty("用户id")
	private String memberId; //memberid

	@Column(name = "worker_type_id")
	@Desc(value = "工种id")
	@ApiModelProperty("工种id")
	private String workerTypeId;//workertypeid

	@Column(name = "worker_type")
	@Desc(value = "工种类型1设计师，2精算师，3大管家,4拆除，6水电工，7泥工，8木工，9油漆工")
	@ApiModelProperty("工种类型1设计师，2精算师，3大管家,4拆除，6水电工，7泥工，8木工，9油漆工")
	private int workerType;//workertype

	@Column(name = "delete_state")
	@Desc(value = "用户删除状态,0表示未删除，1表示已删除,2表示已经支付3业主取消未支付")
	@ApiModelProperty("用户删除状态,0表示未删除，1表示已删除,2表示已经支付3业主取消未支付")
	private Integer deleteState;//deletestate

	@Column(name = "worker_goods_id")
	@Desc(value = "商品id")
	@ApiModelProperty("商品id")
	private String workerGoodsId;//workergoodsid

	@Column(name = "mend_order_id")
	@Desc(value = "补订单Id")
	@ApiModelProperty("补订单Id")
	private String mendOrderId;//abmroid

	@Column(name = "name")
	@Desc(value = "人工名称")
	@ApiModelProperty("人工名称")
	private String name;

	@Column(name = "worker_goods_dec")
	@Desc(value = "补充说明")
	@ApiModelProperty("补充说明")
	private String workerGoodsDec;//workergoodsdec

	@Column(name = "worker_goods_sn")
	@Desc(value = "人工商品编号")
	@ApiModelProperty("人工商品编号")
	private String workerGoodsSn;//workergoodssn

	@Column(name = "unit")
	@Desc(value = "单位")
	@ApiModelProperty("单位")
	private String unit;//workergoodsunit

	@Column(name = "work_explain")
	@Desc(value = "工作说明")
	@ApiModelProperty("工作说明")
	private String workExplain;//

	@Column(name = "introduction")
	@Desc(value = "介绍")
	@ApiModelProperty("介绍")
	private String introduction;

	@Column(name = "price")
	@Desc(value = "单价")
	@ApiModelProperty("单价")
	private BigDecimal price;

	@Column(name = "market_price")
	@Desc(value = "市场价格")
	@ApiModelProperty("市场价格")
	private BigDecimal marketPrice;//marketprice

	@Column(name = "sum_price")
	@Desc(value = "合价")
	@ApiModelProperty("合价")
	private BigDecimal sumPrice; //sumprice

	@Column(name = "shop_count")
	@Desc(value = "购买总数")
	@ApiModelProperty("购买总数")
	private BigDecimal shopCount; //shopcount

	@Column(name = "order_state")
	@Desc(value = "1代表申请中，2代表审核不通过，3代表精算审核通过，等待业主支付，6代表业主已支付,7待工匠审核")
	@ApiModelProperty("1代表申请中，2代表审核不通过，3代表精算审核通过，等待业主支付，6代表业主已支付,7待工匠审核")
	private String orderState;//orderstate

	@Column(name = "order_name")
	@Desc(value = "ordername")
	@ApiModelProperty("ordername")
	private String orderName;//ordername

	@Column(name = "istest")
	@Desc(value = "是否测试, 0不是，1 是测试")
	@ApiModelProperty("是否测试, 0不是，1 是测试")
	private int istest;
}
