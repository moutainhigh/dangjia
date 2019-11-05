package com.dangjia.acg.dto.storefront;

import lombok.Data;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/10/2019
 * Time: 下午 3:56
 */
@Data
public class StorefrontListDTO {
    private String id;
    private String supId;
    private String shopId;
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
     *  状态 0:申请中 1:已选择 2:被打回 -1:待选择
     */
    private String applicationStatus;

    /**
     * 合同
     */
    private String contract;

    /**
     * 失败原因
     */
    private String failReason;


}
