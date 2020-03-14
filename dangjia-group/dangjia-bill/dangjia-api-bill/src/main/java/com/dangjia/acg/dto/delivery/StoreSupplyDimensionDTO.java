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
    private String storefrontId;//店铺ID
    private String productId;//商品ID
    private String image;// 商品图片
    private String imageDetail;//图片详情
    private String productName;//商品名称
    private String productSn;//商品编号
    private String prodTemplateId;//商品id
    private Double shopCount;//总购买数
    private Double suppliedNum;//供货数
    private Double receive;//收货数
    private Double returnCount;//退货数
    private Double totalTransportationCost;//总运费
    private Double totalStevedorageCost;//总搬运费
    private Double totalPrice;//总销售价
    private Double totalSupCost;//总供应价
    private Double supPrice;//供应单价
    private Double price;//销售单价
    private Double profit; //利润
    private Double income;//总收入
    private Double expenditure;//支出
}
