package com.dangjia.acg.dto.storefront;

import lombok.Data;

import java.util.List;

/**
 * 店铺-收入记录-订单详情
 */
@Data
public class ExpenseRecordOrderDetailDTO {
    //商品名称
    private String productName;
    //商品编号
    private String productSn;
    //图片
    private String image;
    private String imageDetail;
    //购买数量
    private String shopCount;
    //要货数量
    private String askCount;
    //发货数量
    private String num;
    //收货数量
    private String receive;
    //退货数量
    private String returnCount;
    //供应单价
    private String cost;
    //供应总价
    private String totalCost;
    //销售单价
    private String price ;
    //销售总价
    private String totalPrice ;
    //订单id
    private String orderId ;

    List<StoreOrderSplitItemDTO>  storeOrderSplitItemlist;

    private String productId;
    private String houseId;
}
