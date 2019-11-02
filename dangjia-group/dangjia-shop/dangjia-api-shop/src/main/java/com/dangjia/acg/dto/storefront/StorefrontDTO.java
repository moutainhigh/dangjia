package com.dangjia.acg.dto.storefront;

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
public class StorefrontDTO {

    private String id;
    /**
     * 用户表ID
     */
    private String userId;

    /**
     * 城市ID
     */
    private String cityId;


    /**
     * 店铺名称
     */
    private String storefrontName;

    /**
     * 店铺地址
     */
    private String storefrontAddress;

    /**
     * 店铺介绍或者店铺描述
     */
    private String storefrontDesc;


    /**
     * 店铺LOGO
     */
    private String storefrontLogo;


    /**
     * 店铺联系人
     */
    private String storekeeperName;

    /**
     * 联系电话
     */
    private String mobile;


    /**
     * 联系邮件
     */
    private String email;

    /**
     * 店铺LOGO
     */
    private String storefrontSigleLogo;


}
