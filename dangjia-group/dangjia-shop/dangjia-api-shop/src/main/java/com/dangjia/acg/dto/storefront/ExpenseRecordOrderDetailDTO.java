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
    private String imageUrl;

    private String imageDetail;
    //购买数量
    private Double shopCount;
    //剩余量
    private Double surplusCount;
    //要货数量
    private Double askCount;
    //发货数量
    private Double num;
    //收货数量
    private Double receive;
    //仅退款
    private Double returnCount;
    //退货退款
    private Double returnRefundCount;
    //供应单价
    private Double cost;
    //供应总价
    private Double totalCost;
    //销售单价
    private Double price ;
    //搬运费
    private Double moveCost ;
    //销售总价
    private Double totalPrice ;
    //订单id
    private String orderId ;

    List<StoreOrderSplitItemDTO>  storeOrderSplitItemlist;

    private String productId;
    private String houseId;
}
