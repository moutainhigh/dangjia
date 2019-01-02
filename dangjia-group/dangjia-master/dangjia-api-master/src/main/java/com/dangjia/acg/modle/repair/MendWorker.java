package com.dangjia.acg.modle.repair;

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
 * 实体类 -补退人工
 * 原表名 AppBudgetWorkerReplenishment
 */
@Data
@Entity
@Table(name = "dj_repair_mend_worker")
@ApiModel(description = "补退人工")
@FieldNameConstants(prefix = "")
public class MendWorker extends BaseEntity {

	@Column(name = "mend_order_id")
	@Desc(value = "补退单Id")
	@ApiModelProperty("补退单Id")
	private String mendOrderId;

	@Column(name = "worker_goods_id")
	@Desc(value = "人工商品id")
	@ApiModelProperty("人工商品id")
	private String workerGoodsId;

	@Column(name = "worker_goods_sn")
	@Desc(value = "人工商品编号")
	@ApiModelProperty("人工商品编号")
	private String workerGoodsSn;

	@Column(name = "worker_goods_name")
	@Desc(value = "人工商品名称")
	@ApiModelProperty("人工商品名称")
	private String workerGoodsName;

	@Column(name = "price")
	@Desc(value = "单价")
	@ApiModelProperty("单价")
	private Double price;

	@Column(name = "unit_name")
	@Desc(value = "单位")
	@ApiModelProperty("单位")
	private String unitName;

	@Column(name = "shop_count")
	@Desc(value = "总数")
	@ApiModelProperty("总数")
	private Double shopCount;

	@Column(name = "total_price")
	@Desc(value = "总价")
	@ApiModelProperty("总价")
	private Double totalPrice; //总价

	@Column(name = "image")
	@Desc(value = "图片")
	@ApiModelProperty("图片")
	private String image;

	public void initPath(String address){
		this.image = StringUtils.isEmpty(this.image)?null:address+this.image;
	}

}