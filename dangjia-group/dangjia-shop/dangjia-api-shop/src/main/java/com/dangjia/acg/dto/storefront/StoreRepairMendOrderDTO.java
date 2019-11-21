package com.dangjia.acg.dto.storefront;

import lombok.Data;

import java.util.List;

@Data
public class StoreRepairMendOrderDTO {

    private String mid;//退货单号
    private String createDate;//退款时间
    private String shipAddress;//业主地址
    private String ownerName;//业主名称
    private String mobile;//业主电话
    private Double sumPrice;
    List<StoreRepairMendOrderDetailDTO> mendOrderDetaillist;

}
