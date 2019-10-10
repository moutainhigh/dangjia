package com.dangjia.acg.model.storefront;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
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
public class Storefront extends BaseEntity {

    /**
     * 用户表ID
     */
    @Column(name = "member_id")
    private String memberId;

    /**
     * 城市ID
     */
    @Column(name = "city_id")
    private String cityId;


    /**
     * 店铺名称
     */
    @Column(name = "storefront_name")
    private String storefrontName;

    /**
     * 店铺地址
     */
    @Column(name = "storefront_address")
    private String storefrontAddress;

    /**
     * 店铺介绍或者店铺描述
     */
    @Column(name = "storefront_desc")
    private String storefrontDesc;


    /**
     * 店铺LOGO
     */
    @Column(name = "storefront_logo")
    private String storefrontLogo;


    /**
     * 店铺联系人
     */
    @Column(name = "storekeeper_name")
    private String storekeeperName;

    /**
     * 联系电话
     */
    @Column(name = "contact")
    private String contact;


    /**
     * 联系邮件
     */
    @Column(name = "email")
    private String email;


}
