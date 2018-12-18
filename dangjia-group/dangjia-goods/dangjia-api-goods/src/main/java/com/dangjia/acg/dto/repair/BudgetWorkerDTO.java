package com.dangjia.acg.dto.repair;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/7 0007
 * Time: 17:20
 */
@Data
public class BudgetWorkerDTO {

    private String workerTypeId;//工种id

    private String workerGoodsId;

    private String workerGoodsSn; //编号

    private String name;//人工商品名

    private Double price;// 单价

    private Double shopCount;//购买总数

    private String unitName;//单位

    private String image;
}
