package com.dangjia.acg.dto.storefront;

import lombok.Data;

@Data
public class StoreOrderSplitItemDTO {
    //要货单号
    private String id;
    //要货时间
    private String createDate;
    //要货数
    private  String askCount;
    //发货数
    private String num;
    //收货数
    private String receive;
    //退货数
    private String returnCount;

}
