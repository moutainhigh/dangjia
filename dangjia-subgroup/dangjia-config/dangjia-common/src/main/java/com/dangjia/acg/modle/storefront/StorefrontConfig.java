package com.dangjia.acg.modle.storefront;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dj_basics_storefront_config")
@ApiModel(description = "店铺配置表")
@FieldNameConstants(prefix = "")
public class StorefrontConfig extends BaseEntity {


    public static final String FREIGHT = "FREIGHT";//参数KEY 运费
    public static final String FREIGHT_TERMS = "FREIGHT_TERMS";//参数KEY 运费条件，满足免运费


    @Column(name = "storefront_id")
    @Desc(value = " 店铺id")
    @ApiModelProperty(" 店铺id")
    private String storefrontId;

    @Column(name = "param_key")
    @Desc("参数key")
    @ApiModelProperty("参数key")
    private String paramKey;

    @Column(name = "param_value")
    @Desc("参数value")
    @ApiModelProperty("参数value")
    private String paramValue;

    @Column(name = "param_desc")
    @Desc("参数描述")
    @ApiModelProperty("参数描述")
    private String paramDesc;

    @Column(name = "city_id")
    @Desc(value = " 城市ID")
    @ApiModelProperty(" 城市ID")
    private String cityId;


}
