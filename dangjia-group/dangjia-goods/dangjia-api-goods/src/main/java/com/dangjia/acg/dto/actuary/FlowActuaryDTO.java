package com.dangjia.acg.dto.actuary;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/11/16 0016
 * Time: 17:38
 */
@Data
public class FlowActuaryDTO {
    private String budgetMaterialId;
    private String typeName;//人工 材料 服务
    private String name;//商品名
    private String image;//图片
    private Double shopCount;//购买总数
    private String url;//商品详情

    private int buy;//购买性质0：必买；1可取消；2自购
    private String attribute;//属性
    private String price;//销售价跟单位拼在一起   最新价格
    private String totalPrice;//总价    最新价格
}
