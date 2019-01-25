package com.dangjia.acg.dto.finance;

import lombok.Data;

/**
 * author: ysl
 * Date: 2019/1/25 0018
 * Time: 14:41
 * 供应商发货信息详情
 */
@Data
public class WebSplitDeliverItemDTO {
    private String image;//图片
    private String productName;//名字
    private Double totalPrice; //总价
    private Double shopCount;//购买总数
    private Double num;//本次发货数量
    private String unitName;//单位
}
