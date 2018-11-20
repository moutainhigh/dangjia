package com.dangjia.acg.modle.design;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 实体类 - 设计风格类型
 * 原表名 HouseStyleEntity
 */
@Data
@Entity
@Table(name = "dj_design_house_style_type")
@ApiModel(description = "设计图与房子关联")
public class HouseStyleType extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "风格名称")
	@ApiModelProperty("风格名称")
	private String name;//

	@Column(name = "price")
	@Desc(value = "价格")
	@ApiModelProperty("价格")
	private BigDecimal price;//wages

	@Column(name = "design_image_list")
	@Desc(value = "关联设计图json串")
	@ApiModelProperty("关联设计图json串")
	private String designImageList;//esignimagetypelist

	@Column(name = "delete_state")
	@Desc(value = "0表示正常，1表示删除")
	@ApiModelProperty("0表示正常，1表示删除")
	private Integer deleteState;//deletestate

	@Column(name = "order_list")
	@Desc(value = "排序")
	@ApiModelProperty("排序")
	private Integer orderList;//

}