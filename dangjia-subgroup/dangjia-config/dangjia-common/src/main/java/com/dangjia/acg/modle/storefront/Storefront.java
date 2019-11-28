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
import javax.persistence.Transient;

@Data
@Entity
@Table(name = "dj_basics_storefront")
@ApiModel(description = "店铺表")
@FieldNameConstants(prefix = "")
public class Storefront extends BaseEntity {

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

    @Column(name = "total_account")
    @Desc(value = "账户总额")
    @ApiModelProperty("账户总额")
    private Double totalAccount;

    @Column(name = "surplus_money")
    @Desc(value = "可提现余额")
    @ApiModelProperty("可提现余额")
    private Double surplusMoney;

    @Column(name = "retention_money")
    @Desc(value = "滞留金")
    @ApiModelProperty("滞留金")
    private Double retentionMoney;



    @Column(name = "djself_manage")
    @Desc(value = "是否当家自营")
    @ApiModelProperty("是否当家自营")
    private Integer  ifDjselfManage;

    @Column(name = "system_logo")
    @Desc(value = "系统图标")
    @ApiModelProperty("系统图标")
    private String  systemLogo;


    @Column(name = "storefront_type")
    @Desc(value = "店铺类型（实物商品：product，人工商品：worker)")
    @ApiModelProperty("店铺类型（实物商品：product，人工商品：worker)")
    private String  storefrontType;



}
