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

	@Column(name = "city_id")
	@Desc(value = "城市id")
	@ApiModelProperty("城市id")

	private String cityId;
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


	@Column(name = "storefont_id")
	@Desc(value = "店铺ID")
	@ApiModelProperty("店铺ID")
	private String storefontId;//店铺ID

	@Column(name = "activity_red_pack_id")
	@Desc(value = "优惠卷ID")
	@ApiModelProperty("优惠卷ID")
	private String activityRedPackId;//优惠卷ID

	@Column(name = "discount_price")
	@Desc(value = "优惠价钱")
	@ApiModelProperty("优惠价钱")
	private Double discountPrice;//优惠价钱

	@Column(name = "actual_payment_price")
	@Desc(value = "实付价钱")
	@ApiModelProperty("实付价钱")
	private Double actualPaymentPrice;//实付价钱

	@Column(name = "stevedorage_cost")
	@Desc(value = "搬运费")
	@ApiModelProperty("搬运费")
	private Double stevedorageCost;//搬运费

	@Column(name = "transportation_cost")
	@Desc(value = "运费")
	@ApiModelProperty("运费")
	private Double transportationCost;//运费

	@Column(name = "required_number")
	@Desc(value = "已要货数量")
	@ApiModelProperty("已要货数量")
	private Double requiredNumber;//已要货数量

	@Column(name = "shipment_number")
	@Desc(value = "已发货数量")
	@ApiModelProperty("已发货数量")
	private Double shipmentNumber;//已发货数量

	@Column(name = "order_type")
	@Desc(value = "订单类型（1设计，2精算，3其它）")
	@ApiModelProperty("订单类型（1设计，2精算，3其它）")
	private String orderType;//订单类型（1设计，精算，2其它）

	@Column(name = "is_reservation_deliver")
	@Desc(value = "是否预约发货(1是，0否）")
	@ApiModelProperty("是否预约发货(1是，0否）")
	private String isReservationDeliver;//是否预约发货(1是，0否）

	@Column(name = "reservation_deliver_time")
	@Desc(value = "预约发货时间")
	@ApiModelProperty("预约发货时间")
	private String reservationDeliverTime;//预约发货时间

	@Column(name = "order_status")
	@Desc(value = "订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）")
	@ApiModelProperty("订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）")
	private String orderStatus;//订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）

	@Column(name = "create_by")
	@Desc(value = "创建人")
	@ApiModelProperty("创建人")
	private String createBy;//创建人

	@Column(name = "update_by")
	@Desc(value = "修改人")
	@ApiModelProperty("修改人")
	private String updateBy;//修改人

	public void initPath(String address){
		this.image = StringUtils.isEmpty(this.image)?null:address+this.image;
	}

}