package com.dangjia.acg.modle.sup;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dj_basics_storefront")
@ApiModel(description = "店铺表")
@FieldNameConstants(prefix = "")
public class Shop extends GoodsBaseEntity {

    /**
     * 用户表ID
     */
    @Column(name = "user_id")
    @Desc(value = " 用户表ID")
    @ApiModelProperty(" 用户表ID")
    private String userId;

    /**
     * 城市ID
     */
    @Column(name = "city_id")
    @Desc(value = "  城市ID")
    @ApiModelProperty("  城市ID")
    private String cityId;


    /**
     * 店铺名称
     */
    @Column(name = "storefront_name")
    @Desc(value = "  店铺名称")
    @ApiModelProperty("  店铺名称")
    private String storefrontName;

    /**
     * 店铺地址
     */
    @Column(name = "storefront_address")
    @Desc(value = "  店铺地址")
    @ApiModelProperty("  店铺地址")
    private String storefrontAddress;

    /**
     * 店铺介绍或者店铺描述
     */
    @Column(name = "storefront_desc")
    @Desc(value = "  店铺介绍或者店铺描述")
    @ApiModelProperty("  店铺介绍或者店铺描述")
    private String storefrontDesc;


    /**
     * 店铺LOGO
     */
    @Column(name = "storefront_logo")
    @Desc(value = "  店铺LOGO")
    @ApiModelProperty("  店铺LOGO")
    private String storefrontLogo;


    /**
     * 店铺联系人
     */
    @Column(name = "storekeeper_name")
    @Desc(value = "  店铺联系人")
    @ApiModelProperty("  店铺联系人")
    private String storekeeperName;

    /**
     * 联系电话
     */
    @Column(name = "mobile")
    @Desc(value = "  联系电话")
    @ApiModelProperty("  联系电话")
    private String mobile;


    /**
     * 联系邮件
     */
    @Column(name = "email")
    @Desc(value = "  联系邮件")
    @ApiModelProperty("  联系邮件")
    private String email;



}
