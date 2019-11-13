package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * 店铺卖家维度实体
 */
@Data
public class StoreBuyersDimensionOrderDetailDTO {
    private String productName;//商品名称
    private String image;// 商品图片
    private String imageDetail;// 商品图片
    private String productSn;//商品编号
    private String num ;// 发货数量
    private String receive;// 收货数量
    private String cost;// 成本单价
    private String costTotalPrice;// 成本总价
    private String sellPrice;// 销售价
    private String sellTotalPrice; // 销售总价

}

