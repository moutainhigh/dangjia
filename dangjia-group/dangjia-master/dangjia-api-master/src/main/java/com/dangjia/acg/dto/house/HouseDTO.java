package com.dangjia.acg.dto.house;

import com.alipay.api.domain.OrderDetail;
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

    /**
     * //id 订单详情ID，orderId订单ID，productId商品ID，purchasePrice购买单价，purchaseNumber购买数量(面积），totalPurchasePrice购买总价,updateBy修改人
     *
     */
    private List orderDetailList;
    /**
     * //id 订单详情ID，orderId订单ID，productId商品ID，purchasePrice购买单价，purchaseNumber购买数量(面积），totalPurchasePrice购买总价,updateBy修改人
     *
     */
    private String  orderDetailInfoAttr;
}
