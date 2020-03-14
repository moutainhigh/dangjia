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
import javax.persistence.Transient;
import java.util.Date;

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
	private Integer productType; //0：材料；1：服务；2：人工；3：体验；4：增值；5：维保

	@Column(name = "category_id")
	private String categoryId;//分类id

	@Column(name = "image")
	private String image;//图片

	@Transient
	private String imageUrl;

	@Transient
	private String valueIdArr;

	@Transient
	private String valueNameArr;




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
	@Desc(value = "实付价钱+运费+搬运费-优惠金额")
	@ApiModelProperty("实付价钱+运费+搬运费-优惠金额")
	private Double actualPaymentPrice;//实付价钱

	@Column(name = "stevedorage_cost")
	@Desc(value = "搬运费")
	@ApiModelProperty("搬运费")
	private Double stevedorageCost;//搬运费

	@Column(name = "transportation_cost")
	@Desc(value = "运费")
	@ApiModelProperty("运费")
	private Double transportationCost;//运费


	@Column(name = "ask_count")
	@Desc(value = "已要总数")
	@ApiModelProperty("已要总数")
	private Double askCount;

	@Column(name = "return_count")
	@Desc(value = "退货数")
	@ApiModelProperty("退货数")
	private Double returnCount;

	@Column(name = "receive_count")
	@Desc(value = "收货数")
	@ApiModelProperty("收货数")
	private Double receiveCount;


	@Column(name = "is_reservation_deliver")
	@Desc(value = "是否预约发货(1是，0否）")
	@ApiModelProperty("是否预约发货(1是，0否）")
	private String isReservationDeliver;//是否预约发货(1是，0否）

	@Column(name = "reservation_deliver_time")
	@Desc(value = "预约发货时间")
	@ApiModelProperty("预约发货时间")
	private Date reservationDeliverTime;//预约发货时间

	@Column(name = "order_status")
	@Desc(value = "订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭8待安装 ）")
	@ApiModelProperty("订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭8待安装 ）")
	private String orderStatus;//订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）

	@Column(name = "worker_type_id")
	@Desc(value = "工种ID（1设计,2精算，3其它）")
	@ApiModelProperty("工种ID（1设计,2精算，3其它）")
	private String workerTypeId;

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
	@Column(name = "cancellation_time")
	@Desc(value = "订单取消时间")
	@ApiModelProperty("订单取消时间")
	private Date cancellationTime;//修改人

	@Column(name = "remark")
	@Desc(value = "体验单备注	")
	@ApiModelProperty("体验单备注")
	private String remark;


	@Column(name = "images")
	@Desc(value = "体验单图片，逗号分隔")
	@ApiModelProperty("体验单图片，逗号分隔")
	private String images;
}