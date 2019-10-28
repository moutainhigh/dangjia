package com.dangjia.acg.dto.refund;

import lombok.Data;

import java.util.List;

@Data
public class RefundOrderDTO {

    private  String orderId;//订单ID

    private String storefrontId;//店铺ID

    private String storefrontName;//店铺名称

    private String businessOrderNumber;//业务订单号

    private String orderNumber;//订单编号

    private List<RefundOrderItemDTO> orderDetailList;
}
