package com.dangjia.acg.dto.storefront;

import lombok.Data;

import java.util.List;

@Data
public class StoreSplitDeliverDTO {
    private String shipAddress ;//地址
    private String ownerName ;//业主名称
    private String mobile;//业主电话
    private String name;    //供应商
    private String supplierTelephone;    //供应商电话
    private String id;    //发货单号
    private String sendTime;    //发货时间
    private String recTime;    //收货时间
    private String shipplingState; //发货状态
}
