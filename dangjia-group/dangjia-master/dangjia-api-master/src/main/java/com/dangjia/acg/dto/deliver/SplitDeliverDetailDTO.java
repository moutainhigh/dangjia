package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/21 0021
 * Time: 19:32
 */
@Data
public class SplitDeliverDetailDTO {
    private String splitDeliverId;//发货单ID
    private String number;//发货单号
    private String shipName;//收货人姓名
    private String shipAddress;//地址地址
    private String shipMobile;//收货人电话
    private String supMobile;//供应商电话
    private String supName;//供应商名称
    private String memo;//须知
    private String reason;//备注
    private String totalSplitPrice;//销售总价
    private Double totalAmount;//成本总价（包含运费、搬运费)
    private Double totalPrice;//商品总价
    private Double deliveryFee;//运费
    private Double stevedorageCost;//搬运费
    private Double applyMoney;//结算总价
    private int size;//件
    private Integer shippingState;
    private Integer applyState;
    private String houseId;
    private List<OrderSplitItemDTO> orderSplitItemList;
}
