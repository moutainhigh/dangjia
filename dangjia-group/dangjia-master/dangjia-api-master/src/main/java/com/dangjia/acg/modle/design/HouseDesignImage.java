package com.dangjia.acg.modle.design;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 设计图与房子关联
 */
@Data
@Entity
@Table(name = "dj_design_house_design_image")
@ApiModel(description = "设计图与房子关联")
public class HouseDesignImage extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "business_order_number")
	@Desc(value = "业务订单号")
	@ApiModelProperty("业务订单号")
	private String businessOrderNumber;//

	@Column(name = "design_image_type_id")
	@Desc(value = "设计图类型ID")
	@ApiModelProperty("设计图类型ID")
	private String designImageTypeId;//designimagetypeid   1为单独平面图

	@Column(name = "imageurl")
	@Desc(value = "图片路径")
	@ApiModelProperty("图片路径")
	private String imageurl;

	@Column(name = "sell")
	@Desc(value = "额外付费图 1是, 0不是")
	@ApiModelProperty("额外付费图 1是, 0不是")
	private int sell;//
}