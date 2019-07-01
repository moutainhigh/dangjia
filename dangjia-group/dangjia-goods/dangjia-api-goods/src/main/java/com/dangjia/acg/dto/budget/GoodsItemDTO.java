package com.dangjia.acg.dto.budget;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2019/2/27 0027
 * Time: 19:04
 */
@Data
public class GoodsItemDTO {
    private String workerTypeName;//工种名字
    private String goodsImage;
    private String goodsName;
    private Double convertCount;//计算数量
    private String unitName;//单位
    private Double price;// 销售价
    private String id;  //人工商品workerGoodsId 服务材料 productId
    private Double shopCount;//购买
    private Double repairCount;//补退
    private Double backCount;//退货
    private Double surCount;//剩余数量
}
