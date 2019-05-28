package com.dangjia.acg.dto.deliver;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/21 0021
 * Time: 19:32
 */
@Data
public class   OrderSplitItemDTO {
    private String productName;//名字
    private Double num;//本次发货数量
    private Double cost;// 成本价
    private Double supCost;//选择的供应商提供的单价
    private String unitName;//单位
    private Double totalPrice; //总价
    private String brandName;//品牌名

    private String brandSeriesName;//品牌系列

    private String image;
    private Double askCount;//要货数量
    private String shopCount;//购买总数
    private Double receive;//收货数量

}
