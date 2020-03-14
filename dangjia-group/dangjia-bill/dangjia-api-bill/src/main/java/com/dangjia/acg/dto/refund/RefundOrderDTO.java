package com.dangjia.acg.dto.refund;

import lombok.Data;

import java.util.List;

@Data
public class RefundOrderDTO {

    private  String orderId;//订单ID

    private  String orderSplitId;//要货单ID

    private  String orderSplitNumber;//要货单编码

    private String storefrontId;//店铺ID

    private String houseId;//房子ID

    private String addressId;//地址ID

    private String storefrontName;//店铺名称

    private String storefrontIcon;//店铺图标

    private String businessOrderNumber;//业务订单号

    private String orderNumber;//订单编号

    private Double totalRransportationCost;//可退运费

    private Double totalStevedorageCost;//可退搬运费

    private Double actualTotalAmount;//退货金额（不含运费）

    private Double totalAmount;//实退款(含运费)

    private String totalRransportationCostRemark;//可退运费描述

    private String totalStevedorageCostRemark;//可退搬运费描述

    private List<RefundOrderItemDTO> orderDetailList;
}
