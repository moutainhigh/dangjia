package com.dangjia.acg.dto.pay;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 17:34
 */
@Data
public class ActuaryDTO {
    private String image;//图标
    private String kind;//类别
    private String name;//
    private String price;//价格
    private String button;//按钮名
    private String url;//明细url
    private int type; //1人工商品,2材料商品,3服务商品,4补人工,5补材料
}
