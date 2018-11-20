package com.dangjia.acg.dto.actuary;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/11/16 0016
 * Time: 17:38
 */
@Data
public class FlowActuaryDTO {
    private String typeName;//人工 材料 服务
    private String name;//商品名
    private Double shopCount;//购买总数
    private String url;//商品详情
}
