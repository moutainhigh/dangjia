package com.dangjia.acg.dto.storefront;

import lombok.Data;

@Data
public class StoreRepairMendOrderDetailDTO {

    private String productName;  //商品名称
    private String productSn; //商品编号
    private String image;//图片
    private String imageDetail;//图片详情
    private String price;//单价
    private String totalPrice;//小计
    private String shopCount;//申请退货数
    private String actualCount;// 实际退货数

}
