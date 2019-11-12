package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: chenyufeng
 * 店铺商品维度
 * Date: 11/11/2019
 * Time: 上 3:00
 */
@Data
public class StoreSupplyDimensionDTO {
    private String image;// 商品图片
    private String productName;//商品名称
    private String productSn;//商品编号
    private String prodTemplateId;//商品id
    private String shopCount;//总购买数
    private String suppliedNum;//供货数
    private String cost;//成本总价
    private String price;//销售总价
    private String profit; //利润

}
