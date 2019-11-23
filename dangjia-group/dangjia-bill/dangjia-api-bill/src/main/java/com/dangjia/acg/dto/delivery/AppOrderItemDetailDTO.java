package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppOrderItemDetailDTO {
    private String productId;//
    private String storefrontName;//店铺名称
    private String productName;//商品名称
    private String shopCount;//购买数量
    private String brandName;//规格
    private String price ;//单位
    private String mobile;//电话
    private String image;//图片
    private String imageDetail;//详细图片

    List< Map<String, Object>> detailMaplist;
}
