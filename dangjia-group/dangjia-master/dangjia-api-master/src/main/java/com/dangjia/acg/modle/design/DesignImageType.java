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
 * 实体类 - 设计图类型
 *
 *  原表名 HouseDesignImageType
 */
@Data
@Entity
@Table(name = "dj_design_design_image_type")
@ApiModel(description = "设计图与房子关联")
public class DesignImageType extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "名字")
	@ApiModelProperty("名字")
	private String name;

	@Column(name = "state")
	@Desc(value = "1可用，2禁用，3删除")
	@ApiModelProperty("1可用，2禁用，3删除")
	private int state;//

	@Column(name = "upload")
	@Desc(value = "0非必传，1必传")
	@ApiModelProperty("0非必传，1必传")
	private int upload;//

	@Column(name = "type")
	@Desc(value = "1施工图,2效果图")
	@ApiModelProperty("1施工图,2效果图")
	private int type;//

	@Column(name = "sell")
	@Desc(value = "额外付费 1是, 0不是")
	@ApiModelProperty("额外付费 1是, 0不是")
	private int sell;//

	@Column(name = "price")
	@Desc(value = "价格")
	@ApiModelProperty("价格")
	private BigDecimal price;
}