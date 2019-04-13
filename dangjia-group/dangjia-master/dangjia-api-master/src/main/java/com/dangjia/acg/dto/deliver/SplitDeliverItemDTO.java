package com.dangjia.acg.dto.deliver;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/22 0022
 * Time: 14:12
 */
@Data
public class SplitDeliverItemDTO {
    private String image;//图片
    private String productName;//名字
    private String productSn;//编号
    private Double shopCount;//购买总数
    private Double num;//本次发货数量
    private String unitName;//单位
    private String brandSeriesName;//品牌系列
    private Double cost;//平均成本价
    private Double price;//销售价
    private Double totalPrice; //销售价*发货数量
    private String id;
    private Double receive;//收货数量
    private Double supCost;//选择的供应商提供的单价
    private Double askCount;//要货数量
}
