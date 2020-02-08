package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * author: Ronalcheng
 * Date: 2018/12/4 0004
 * Time: 11:42
 */
@Data
public class OrderSplitDTO {

    private String houseId;//房子ID
    private String addressId;//地址ID
    private String orderSplitId;//要货单ID
    private String number ;//要货单号
    private Date createDate;//要货时间
    private Integer applyStatus;//要货状态（1,待处理，2已处理）
    private String isReservationDeliver;//是否需要预约发货（1是，0否）
}
