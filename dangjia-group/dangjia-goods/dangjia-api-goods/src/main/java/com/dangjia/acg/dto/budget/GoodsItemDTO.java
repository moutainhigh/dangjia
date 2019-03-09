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
    private Integer convertCount;//计算数量
    private String unitName;//单位
    private Double price;// 销售价
    private String id;  //人工商品workerGoodsId 服务材料 productId
}
