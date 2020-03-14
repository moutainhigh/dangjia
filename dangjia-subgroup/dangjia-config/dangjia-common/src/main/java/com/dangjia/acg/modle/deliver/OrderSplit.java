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
 * 要货单
 */
@Data
@Entity
@Table(name = "dj_deliver_order_split")
@ApiModel(description = "提交要货单")
@FieldNameConstants(prefix = "")
public class OrderSplit extends BaseEntity {


	@Column(name = "split_parent_id")
	@Desc(value = "父订单ID")
	@ApiModelProperty("父订单ID")
	private String splitParentId;

	@Column(name = "number")
	@Desc(value = "订单号")
	@ApiModelProperty("订单号")
	private String number;

	@Column(name = "mend_number")
	@Desc(value = "补货单号")
	@ApiModelProperty("补货订单号")
	private String mendNumber;

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "apply_status")
	@Desc(value = "后台审核状态：0生成中, 1申请中, 2通过(发给供应商), 3不通过, 4待业主支付,5已撤回")
	@ApiModelProperty("后台审核状态：0生成中, 1申请中, 2通过(发给供应商), 3不通过, 4待业主支付,5已撤回")
	private Integer applyStatus;

	@Column(name = "member_id")
	@Desc(value = "要货人id")
	@ApiModelProperty("要货人id")
	private String memberId;//

	@Column(name = "member_name")
	@Desc(value = "要货人姓名")
	@ApiModelProperty("要货人姓名")
	private String memberName;//

	@Column(name = "mobile")
	@Desc(value = "要货人电话")
	@ApiModelProperty("要货人电话")
	private String mobile;

	@Column(name = "worker_type_id")
	@Desc(value = "工种id")
	@ApiModelProperty("工种id")
	private String workerTypeId;


	@Column(name = "storefront_id")
	@Desc(value = "店铺ID")
	@ApiModelProperty("店铺ID")
	private String storefrontId;


	@Column(name = "address_id")
	@Desc(value = "地址ID")
	@ApiModelProperty("地址ID")
	private String addressId;

	@Column(name = "city_id")
	@Desc(value = "城市id")
	@ApiModelProperty("城市id")
	private String cityId;

	@Column(name = "order_id")
	@Desc(value = "订单ID")
	@ApiModelProperty("订单ID")
	private String orderId;

	@Column(name = "is_reservation_deliver")
	@Desc(value = "是否需要预约(1是，0否）")
	@ApiModelProperty("是否需要预约(1是，0否）")
	private String isReservationDeliver;

	@Column(name = "reservation_deliver_time")
	@Desc(value = "预约发货时间")
	@ApiModelProperty("预约发货时间")
	private Date reservationDeliverTime;

	@Column(name = "total_amount")
	@Desc(value = "总价")
	@ApiModelProperty("总价")
	private BigDecimal totalAmount;
}
























