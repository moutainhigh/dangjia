package com.dangjia.acg.modle.actuary;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * 材料/服务精算
 */
@Data
@Entity
@Table(name = "dj_actuary_budget_material")
@ApiModel(description = "材料精算表")
@FieldNameConstants(prefix = "")
public class BudgetMaterial extends BaseEntity{

	@Column(name = "house_flow_id")
	private String houseFlowId;

	@Column(name = "house_id")
	private String houseId;//房间ID

	@Column(name = "worker_type_id")
	private String workerTypeId;//工种ID

	@Column(name = "steta")
	private Integer steta;//1代表我们购,2代表自购,3代表模板

	@Column(name = "template_id")
	private String templateId; // 模板信息Id

	@Column(name = "delete_state")
	private Integer deleteState;//用户删除状态·,0表示未支付，1表示已删除,2表示业主取消,3表示已经支付,4再次购买

	@Column(name = "product_id")
	private String productId;//货号ID

	@Column(name = "product_sn")
	private String productSn;// 货号编号

	@Column(name = "product_name")
	private String productName;//货号名称

	@Column(name = "product_nick_name")
	private String productNickName;//货品昵称

	@Column(name = "goods_id")
	private String goodsId;//商品Id

	@Column(name = "goods_name")
	private String goodsName;// 商品名称

	@Column(name = "price")
	private Double price;// 销售价

	@Column(name = "cost")
	private Double cost;// 成本价

	@Column(name = "description")
	private String description; //页面描述 备注

	@Column(name = "shop_count")
	private Double shopCount;//购买总数

	@Column(name = "actuarial_quantity")
	private Double actuarialQuantity;//精算量

	@Column(name = "unit_name")
	private String unitName;//单位

	@Column(name = "total_price")
	private Double totalPrice; //总价

	@Column(name = "group_type")
	private String groupType; //null：单品；有值：关联组合

	@Column(name = "product_type")
	private Integer productType; //0：材料；1：服务

	@Column(name = "goods_group_id")
	private String goodsGroupId; //所属关联组

	@Column(name = "category_id")
	private String categoryId;//分类id

	@Column(name = "image")
	private String image;//货品图片

}
