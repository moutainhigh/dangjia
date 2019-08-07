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

/**
 * 实体类 - 商品换货表
 * @author Yinjianbo
 * @Date 2019-5-11
 *
 */
@Data
@Entity
@Table(name = "dj_deliver_product_change")
@ApiModel(description = "商品换货")
@FieldNameConstants(prefix = "")
public class ProductChange extends BaseEntity {

	@Column(name = "city_id")
	@Desc(value = "城市id")
	@ApiModelProperty("城市id")
	private String cityId;

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;

	@Column(name = "order_id")
	@Desc(value = "订单id")
	@ApiModelProperty("订单id")
	private String orderId;

	@Column(name = "member_id")
	@Desc(value = "用户ID")
	@ApiModelProperty("用户ID")
	private String memberId;

	@Column(name = "category_id")
	@Desc(value = "分类id")
	@ApiModelProperty("分类id")
	private String categoryId;

	@Column(name = "src_product_id")
	@Desc(value = "货品id")
	@ApiModelProperty("货品id")
	private String srcProductId;

	@Column(name = "src_product_sn")
	@Desc(value = "货号编号")
	@ApiModelProperty("货号编号")
	private String srcProductSn;

	@Column(name = "src_product_name")
	@Desc(value = "货号名称")
	@ApiModelProperty("货号名称")
	private String srcProductName;

	@Column(name = "src_price")
	@Desc(value = "销售价")
	@ApiModelProperty("销售价")
	private Double srcPrice;

	@Column(name = "src_sur_count")
	@Desc(value = "剩余数")
	@ApiModelProperty("剩余数")
	private Double srcSurCount;

	@Column(name = "src_unit_name")
	@Desc(value = "单位")
	@ApiModelProperty("单位")
	private String srcUnitName;

	@Column(name = "src_image")
	@Desc(value = "货品图片")
	@ApiModelProperty("货品图片")
	private String srcImage;

	@Column(name = "dest_product_id")
	@Desc(value = "货品id")
	@ApiModelProperty("货品id")
	private String destProductId;

	@Column(name = "dest_product_sn")
	@Desc(value = "货号编号")
	@ApiModelProperty("货号编号")
	private String destProductSn;

	@Column(name = "dest_product_name")
	@Desc(value = "货号名称")
	@ApiModelProperty("货号名称")
	private String destProductName;

	@Column(name = "dest_price")
	@Desc(value = "销售价")
	@ApiModelProperty("销售价")
	private Double destPrice;

	@Column(name = "dest_sur_count")
	@Desc(value = "更换数")
	@ApiModelProperty("更换数")
	private Double destSurCount;

	@Column(name = "dest_unit_name")
	@Desc(value = "单位")
	@ApiModelProperty("单位")
	private String destUnitName;

	@Column(name = "dest_image")
	@Desc(value = "货品图片")
	@ApiModelProperty("货品图片")
	private String destImage;

	@Column(name = "difference_price")
	@Desc(value = "差额")
	@ApiModelProperty("差额")
	private BigDecimal differencePrice;

	@Column(name = "type")
	@Desc(value = "0未处理 1已处理")
	@ApiModelProperty("0未处理 1已处理")
	private Integer type;

	@Column(name = "product_type")
	@Desc(value = "0：材料；1：包工包料")
	@ApiModelProperty("0：材料；1：包工包料")
	private Integer productType; //0：材料；1：包工包料
}