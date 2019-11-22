package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppOrderDetailDTO {

    private String houseId;//房子编号
    private String  orderId;//订单编号
    private String createDate;//创建时间
    private String totalAmount;//商品总额
    private String totalTransportationCost;//运费
    private String totalStevedorageCost;//搬运费
    private String totalDiscountPrice;//优惠价
    private String shipAddress;//房子地址
    private String orderStatus;//订单状态
    private String paymentAmount;//需付款
    private String orderPayTime;//订单支付时间
    private String orderGenerationTime ;//订单成交时间
    List<AppOrderItemDetailDTO>   detaillist;//订单详情列表
    List<Map<String,Object>> mapList;//动态展示商品总额 运费 搬运费 优惠卷名称
    List<Map<String,Object>> orderDtailList;//订单编号 订单快照 创建时间 付款时间 发货单号 发货时间 成交时间
}
