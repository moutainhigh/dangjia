package com.dangjia.acg.dto.house;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/10 0010
 * Time: 11:15
 */
@Data
public class HouseDTO {
    private String houseId;
    private String cityId;
    private String cityName;//
    private String villageId;//小区id
    private String residential;//
    private String modelingLayoutId;//户型id
    private String building;
    private String unit;
    private String number;
    private double square;//外框面积
    private double buildSquare;//外框面积
    private String referHouseId;//参考房子id;
    private String referHouseName;//参考房子信息;
    private String style;//风格
    private String styleId;//风格Id
    private String workDepositId;//结算比例ID
    private String houseType;//房子类型 0：新房；1：老房
    private int drawings;//有无图纸
    private int decorationType;//装修类型:1远程设计，2自带设计

    private String memberId;//业主ID
    private String memberName;//业主姓名
    private String mobile;//业主电话
    private Double inputArea;//业主录入面积
    private String orderNumber;//订单号
    private String orderStatus;//订单状态

    /**
     * 设计精算对应的商品信息
     */
    private List<HouseOrderDetailDTO> orderDetailList;
    /**
     * 设计精算，商品信息
     */
    private String  actuarialDesignInfoAttr;

    @ApiModelProperty("地址")
    private String address;
}
