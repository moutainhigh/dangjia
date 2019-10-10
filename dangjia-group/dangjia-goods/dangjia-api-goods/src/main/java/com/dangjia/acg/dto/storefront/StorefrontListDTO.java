package com.dangjia.acg.dto.storefront;

import lombok.Data;

import javax.persistence.Column;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/10/2019
 * Time: 下午 3:56
 */
@Data
public class StorefrontListDTO {
    private String id;
    /**
     * 用户表ID
     */
    private String memberId;

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
    private String contact;


    /**
     * 联系邮件
     */
    private String email;
    private String state="-1"; //状态 0:申请中 1:已选择 2:被打回 -1:待选择
    private String contract;
    private String failReason;
}
