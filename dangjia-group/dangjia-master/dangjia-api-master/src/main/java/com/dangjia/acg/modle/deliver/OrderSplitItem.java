package com.dangjia.acg.modle.deliver;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "dj_deliver_order_split_item")
@ApiModel(description = "拆分单的子项")
public class OrderSplitItem extends BaseEntity {

	@Column(name = "shipping_status")
	@Desc(value = "配送状态（刚拆分、提交给供应商,已发货,已收货,已发货退货,取消,生成拆货单）0,1,2,3,4,5,6")
	@ApiModelProperty("配送状态（刚拆分、提交给供应商,已发货,已收货,已发货退货,取消,生成拆货单）0,1,2,3,4,5,6")
	private int shippingStatus;//

	@Column(name = "state")
	@Desc(value = "2业主未发退货")
	@ApiModelProperty("2业主未发退货")
	private int state;//

	@Column(name = "order_id")
	@Desc(value = "总材料单id")
	@ApiModelProperty("总材料单id")
	private String orderId;//

	@Column(name = "order_item_id")
	@Desc(value = "对应子项id")
	@ApiModelProperty("对应子项id")
	private String orderItemId;//

	@Column(name = "order_split_id")
	@Desc(value = "拆分单id")
	@ApiModelProperty("拆分单id")
	private String orderSplitId;//

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "goods_sn")
	@Desc(value = "商品编号")
	@ApiModelProperty("商品编号")
	private String goodsSn;//

	@Column(name = "product_sn")
	@Desc(value = "货号")
	@ApiModelProperty("货号")
	private String productSn;//

	@Column(name = "product_price")
	@Desc(value = "商品价格")
	@ApiModelProperty("商品价格")
	private BigDecimal productPrice;//

	@Column(name = "cost")
	@Desc(value = "成本价")
	@ApiModelProperty("成本价")
	private BigDecimal cost;//

	@Column(name = "name")
	@Desc(value = "名称")
	@ApiModelProperty("名称")
	private String name;//

	@Column(name = "meta_description")
	@Desc(value = "精算 备注")
	@ApiModelProperty("精算 备注")
	private String metaDescription;//

	@Column(name = "unit")
	@Desc(value = "单位")
	@ApiModelProperty("单位")
	private String unit;//

	@Column(name = "brand")
	@Desc(value = "品牌")
	@ApiModelProperty("品牌")
	private String brand;//

	@Column(name = "buy_num")
	@Desc(value = "购买数量")
	@ApiModelProperty("购买数量")
	private Double buyNum;//

	@Column(name = "ship_num")
	@Desc(value = "已发数量")
	@ApiModelProperty("已发数量")
	private Double shipNum;//

	@Column(name = "surplus_num")
	@Desc(value = "剩余数量")
	@ApiModelProperty("剩余数量")
	private Double surplusNum;//

	@Column(name = "this_ship_num")
	@Desc(value = "本次发货数量")
	@ApiModelProperty("本次发货数量")
	private Double thisShipNum;//

	@Column(name = "this_surplus_num")
	@Desc(value = "本次剩余数量")
	@ApiModelProperty("本次剩余数量")
	private Double thisSurplusNum;//
}
