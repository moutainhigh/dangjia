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
import java.util.Date;

@Data
@Entity
@Table(name = "dj_deliver_order_split_item")
@ApiModel(description = "要货单明细")
@FieldNameConstants(prefix = "")
public class OrderSplitItem extends BaseEntity {

	@Column(name = "city_id")
	@Desc(value = "城市id")
	@ApiModelProperty("城市id")
	private String cityId;

	@Column(name = "order_split_id")
	@Desc(value = "拆分单id")
	@ApiModelProperty("拆分单id")
	private String orderSplitId;

	@Column(name = "warehouse_id")
	@Desc(value = "仓库子项id")
	@ApiModelProperty("仓库子项id")
	private String warehouseId;

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
	private Double cost;//平均成本价

	@Column(name = "sup_cost")
	private Double supCost;//选择的供应商提供的单价

	@Column(name = "shop_count")
	private Double shopCount;//购买总数

	@Column(name = "ask_count")
	private Double askCount;//已要总数

	@Column(name = "num")
	private Double num;//本次发货数量

	@Column(name = "receive")
	private Double receive;//收货数量

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

	@Column(name = "house_id")
	private String houseId;//房子id

	@Column(name = "split_deliver_id")
	private String splitDeliverId;//发货单id

	public void initPath(String address){
		this.image = StringUtils.isEmpty(this.image)?null:address+this.image;
	}

	@Column(name = "address_id")
	@Desc(value = "地址id")
	@ApiModelProperty("地址id")
	private String addressId;

	@Column(name = "storefront_id")
	@Desc(value = "店铺ID")
	@ApiModelProperty("店铺ID")
	private String storefrontId;

	@Column(name = "is_delivery_install")
	@Desc(value = "'是否送货与安装/施工分开'")
	@ApiModelProperty("'是否送货与安装/施工分开'")
	private String isDeliveryInstall;


	@Column(name = "order_item_id")
	@Desc(value = "'是否送货与安装/施工分开'")
	@ApiModelProperty("'是否送货与安装/施工分开'")
	private String orderItemId;

	@Column(name = "order_item_str")
	@Desc(value = "'是否送货与安装/施工分开'")
	@ApiModelProperty("'是否送货与安装/施工分开'")
	private String orderItemStr;

	@Column(name = "is_reservation_deliver")
	@Desc(value = "是否需要预约(1是，0否）")
	@ApiModelProperty("是否需要预约(1是，0否）")
	private Integer isReservationDeliver;

	@Column(name = "reservation_deliver_time")
	@Desc(value = "预约发货时间")
	@ApiModelProperty("预约发货时间")
	private Date reservationDeliverTime;


	@Column(name = "return_count")
	@Desc(value = "退货量")
	@ApiModelProperty("退货量")
	private Double returnCount;


}
