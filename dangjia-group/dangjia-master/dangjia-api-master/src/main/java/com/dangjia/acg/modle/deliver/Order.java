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
import java.util.Date;

/**
 * 实体类 - 所有订单
 */
@Data
@Entity
@Table(name = "dj_deliver_order")
@ApiModel(description = "所有订单")
@FieldNameConstants(prefix = "")
public class Order extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "business_order_number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String businessOrderNumber;

	@Column(name = "total_amount")
	@Desc(value = "订单总额")
	@ApiModelProperty("订单总额")
	private BigDecimal totalAmount;

	@Column(name = "member_id")
	@Desc(value = "用户ID")
	@ApiModelProperty("用户ID")
	private String memberId;//memberid

	@Column(name = "address_id")
	@Desc(value = "地址ID")
	@ApiModelProperty("地址ID")
	private String addressId;

	@Column(name = "worker_id")
	@Desc(value = "工人ID")
	@ApiModelProperty("工人ID")
	private String workerId;
	@Column(name = "worker_type_name")
	@Desc(value = "工种名称")
	@ApiModelProperty("工种名称")
	private String workerTypeName;//workertypeName

	@Column(name = "worker_type_id")
	@Desc(value = "工种id")
	@ApiModelProperty("工种id")
	private String workerTypeId;

	@Column(name = "style_name")
	@Desc(value = "设计风格")
	@ApiModelProperty("设计风格")
	private String styleName;

	@Column(name = "style_price")
	@Desc(value = "风格价格")
	@ApiModelProperty("风格价格")
	private BigDecimal stylePrice;

	@Column(name = "budget_cost")
	@Desc(value = "精算价格")
	@ApiModelProperty("精算价格")
	private BigDecimal budgetCost;

	@Column(name = "type")
	@Desc(value = "1人工订单 2材料订单")
	@ApiModelProperty("1人工订单 2材料订单")
	private Integer type;

	@Column(name = "payment")
	@Desc(value = "支付方式1微信, 2支付宝,3后台回调")
	@ApiModelProperty("支付方式1微信, 2支付宝,3后台回调")
	private String payment;

	@Column(name = "order_number")
	@Desc(value = "订单编号")
	@ApiModelProperty("订单编号")
	private String orderNumber;//订单编号

	@Column(name = "parent_order_id")
	@Desc(value = "父订单ID")
	@ApiModelProperty("父订单ID")
	private String parentOrderId;//父订单ID


	@Column(name = "storefont_id")
	@Desc(value = "店铺ID")
	@ApiModelProperty("店铺ID")
	private String storefontId;//店铺ID

	@Column(name = "city_id")
	@Desc(value = "城市ID")
	@ApiModelProperty("城市ID")
	private String cityId;//城市ID

	@Column(name = "total_order_price")
	@Desc(value = "订单总价钱")
	@ApiModelProperty("订单总价钱")
	private BigDecimal totalOrderPrice;//订单总价钱

	@Column(name = "total_discount_price")
	@Desc(value = "优惠总价钱")
	@ApiModelProperty("优惠总价钱")
	private BigDecimal totalDiscountPrice;//优惠总价钱

	@Column(name = "total_stevedorage_cost")
	@Desc(value = "总搬运费")
	@ApiModelProperty("总搬运费")
	private BigDecimal totalStevedorageCost;//总搬运费

	@Column(name = "total_transportation_cost")
	@Desc(value = "总运费")
	@ApiModelProperty("总运费")
	private BigDecimal totalTransportationCost;//总运费

	@Column(name = "order_type")
	@Desc(value = "订单类型（1设计，精算，2其它）")
	@ApiModelProperty("订单类型（1设计，精算，2其它）")
	private String orderType;//订单类型（1设计，精算，2其它）

	@Column(name = "actual_payment_price")
	@Desc(value = "实付总价")
	@ApiModelProperty("实付总价")
	private BigDecimal actualPaymentPrice;//实付总价

	@Column(name = "is_pay_money")
	@Desc(value = "是否可付款（1不可付款，2可付款）")
	@ApiModelProperty("是否可付款（1不可付款，2可付款）")
	private String isPayMoney;//是否可付款（1不可付款，2可付款）


	@Column(name = "is_show_order")
	@Desc(value = "是否显示该订单（1是，0否）")
	@ApiModelProperty("是否显示该订单（1是，0否）")
	private String isShowOrder;//是否可付款（1不可付款，2可付款）


	@Column(name = "order_status")
	@Desc(value = "订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）")
	@ApiModelProperty("订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）")
	private String orderStatus;//订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）

	@Column(name = "order_generation_time")
	@Desc(value = "订单生成时间")
	@ApiModelProperty("订单生成时间")
	private Date orderGenerationTime;//订单生成时间

	@Column(name = "order_pay_time")
	@Desc(value = "订单支付时间")
	@ApiModelProperty("订单支付时间")
	private Date orderPayTime;//订单支付时间

	@Column(name = "order_source")
	@Desc(value = "订单来源(1,精算制作，2业主自购，3购物车）")
	@ApiModelProperty("订单来源(1,精算制作，2业主自购，3购物车）")
	private int orderSource;//订单来源(1,精算制作，2业主自购，3购物车）

	@Column(name = "create_by")
	@Desc(value = "创建人")
	@ApiModelProperty("创建人")
	private String createBy;//创建人

	@Column(name = "update_by")
	@Desc(value = "修改人")
	@ApiModelProperty("修改人")
	private String updateBy;//修改人

}