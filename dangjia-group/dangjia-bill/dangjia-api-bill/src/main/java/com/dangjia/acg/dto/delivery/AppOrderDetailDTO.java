package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;

@Data
public class AppOrderDetailDTO {

    private String houseId;//房子编号
    private String  orderId;//订单编号
    private String orderGenerationTime;//创建时间
    private String totalAmount;//商品总额
    private String totalTransportationCost;//运费
    private String totalStevedorageCost;//搬运费
    private String totalDiscountPrice;//优惠价
    List<AppOrderItemDetailDTO> list;
}
