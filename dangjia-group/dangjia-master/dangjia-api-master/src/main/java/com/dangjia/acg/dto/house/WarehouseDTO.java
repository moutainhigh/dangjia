package com.dangjia.acg.dto.house;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/15 0015
 * Time: 10:55
 */
@Data
public class WarehouseDTO {

    private Double shopCount;//买总数
    private Double askCount;//要总数
    private Double backCount;//退总数
    private Double receive;//收货总数
    private Double realCount;//实用 = shopCount - backCount
    private Double surCount;//仓库剩余 = shopCount - askCount - backCount
    private String productName;
    private Double price;
    private Integer maket;//是否上架  0:未上架；1已上架
    private Double tolPrice; //总计价 = realCount * price
    private String unitName;
    private Integer productType;// 0：材料；1：包工包料
    private String image;
    private Double repairCount;
    private String productId;
    private Integer askTime; //要货次数
    private Integer repTime; //补次数
    private Integer backTime; //退次数
    private String brandSeriesName;//品牌系列名
    private Integer changeType; //商品是否更换
    private Integer sales;//退货性质0：可退；1不可退
    private String storefrontId;//店铺id
    private String productSn;
    /**
     * 货品+规格
     */
    private String brandName;
}
