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
    private String number;//订单号
    private String shipName;//收货人
    private String shipAddress;//地址
    private String shipMobile;//电话
    private String supMobile;//
    private String supName;//
    private String memo;//须知
    private String reason;//备注
    private Double totalAmount;//总价
    private int size;//件
    private Integer shippingState;
    private Integer applyState;
    private String houseId;
    private List<OrderSplitItemDTO> orderSplitItemDTOS;
}
