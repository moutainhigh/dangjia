package com.dangjia.acg.modle.house;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * 验房价格配置记录
 * @author qiyuxiang
 *
 */
@Data
@Entity
@Table(name = "dj_house_distribution_config")
@ApiModel(description = "验房价格配置记录")
@FieldNameConstants(prefix = "")
public class HouseDistributionConfig extends BaseEntity {

    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;

    @Column(name = "city_name")
    @Desc(value = "城市名")
    @ApiModelProperty("城市名")
    private String cityName;

	@Column(name = "villages")
	@Desc(value = "小区集合ID")
	@ApiModelProperty("小区集合ID")
	private String villages;


	@Column(name = "price")
	@Desc(value = "价格")
	@ApiModelProperty("价格")
	private Double price;

	@Transient
	private String villagesName;

}
