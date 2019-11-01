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
@Table(name = "dj_basics_storefront_ruleconfig")
@ApiModel(description = "店铺运费规则表")
@FieldNameConstants(prefix = "")
public class StorefrontRuleConfig extends BaseEntity {
    @Column(name = "user_id")
    @Desc(value = " 用户id")
    @ApiModelProperty(" 用户id")
    private String userId;


    @Column(name = "storefront_id")
    @Desc(value = " 店铺id")
    @ApiModelProperty(" 店铺id")
    private String storefrontId;

    @Column(name = "storefront_key")
    @Desc(value = " 运费配置项所关联的键")
    @ApiModelProperty(" 运费配置项所关联的键")
    private String storefrontKey;

    @Column(name = "freight")
    @Desc(value = " 收取运费")
    @ApiModelProperty(" 收取运费")
    private String freight;

    @Column(name = "below_unit_price")
    @Desc(value = " 每单价格低于")
    @ApiModelProperty(" 每单价格低于")
    private String belowUnitPrice;

    @Column(name = "city_id")
    @Desc(value = " 城市ID")
    @ApiModelProperty(" 城市ID")
    private String cityId;


}
