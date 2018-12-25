package com.dangjia.acg.dto.deliver;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/22 0022
 * Time: 14:12
 */
@Data
public class SplitDeliverItemDTO {
    private String image;//图片
    private String productName;//名字
    private Double totalPrice; //总价
    private Double shopCount;//购买总数
    private Double num;//本次发货数量
    private String unitName;//单位
}
