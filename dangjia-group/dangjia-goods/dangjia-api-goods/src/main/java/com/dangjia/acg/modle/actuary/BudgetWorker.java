package com.dangjia.acg.modle.actuary;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * @类 名： 人工精算BudgetWorker.java
 */
@Data
@Entity
@Table(name = "dj_actuary_budget_worker")
@ApiModel(description = "人工精算")
@FieldNameConstants(prefix = "")
public class BudgetWorker extends BaseEntity {

	@Column(name = "house_flow_id")
	private String houseFlowId;

	@Column(name = "house_id")
	private String houseId;

	@Column(name = "worker_type_id")
	private String workerTypeId;//工种id

	@Column(name = "steta")
	private Integer steta;//1代表我们购,2代表自购,3代表模板

	@Column(name = "template_id")
	private String templateId; // 模板信息Id

	@Column(name = "delete_state")
	private Integer deleteState;//用户删除状态·,0表示未删除，1表示精算删除,2业主取消,3表示已经支付

	@Column(name = "worker_goods_id")
	private String workerGoodsId;

	@Column(name = "worker_goods_sn")
	private String workerGoodsSn; //编号

	@Column(name = "name")
	private String name;//人工商品名

	@Column(name = "price")
	private Double price;// 单价

	@Column(name = "description")
	private String description; //页面描述 备注

	@Column(name = "shop_count")
	private Double shopCount;//购买总数

	@Column(name = "unit_name")
	private String unitName;//单位

	@Column(name = "total_price")
	private Double totalPrice; //总价

}
