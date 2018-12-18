package com.dangjia.acg.dto.house;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/15 0015
 * Time: 10:55
 */
@Data
public class WarehouseDTO {

    private double shopCount;//买总数
    private double askCount;//要总数
    private double backCount;//退总数
    private double realCount;//实用 = shopCount - backCount
    private double surCount;//仓库剩余 = shopCount - askCount - backCount
    private String productName;
    private double price;
    private double tolPrice; //总计价 = realCount * price
    private String unitName;
    private int productType;// 0：材料；1：服务
    private String image;
    private String productId;

    private int askTime; //要货次数
    private int repTime; //补次数
    private int backTime; //退次数

    private String brandSeriesName;//品牌系列名
}
