package com.dangjia.acg.dto.delivery;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: chenyufeng
 * 店铺利润统计-供应商商品详情
 * Date: 17/10/2019
 * Time: 上午 11:27
 */
@Data
public class SupplierDimensionGoodsDetailDTO {

    private String image;//图片
    private String imageDetail;
    private String productName;//商品名称
    private String productSn;//商品编号
    private String num;//发货数量;
    private String receiveCount;//收货数量
    private String cost;//成本单价
    private String totalCost;//成本总价
    private String price;//
    private String totalPrice;//
    private String productId;//商品ID


}
