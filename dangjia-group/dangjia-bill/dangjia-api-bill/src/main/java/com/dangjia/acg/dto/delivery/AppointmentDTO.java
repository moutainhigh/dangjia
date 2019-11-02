package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/10/2019
 * Time: 上午 10:43
 */
@Data
public class AppointmentDTO {
    private String productId;
    private String orderItemId;
    private String storefrontId;
    private String productName;
    private String productSn;
    private String productType;
    private Double price;
    private String image;
    private Double shopCount;
    private Double askCount;
    private Double returnCount;
    private Double surplusCount;
    private Date reservationDeliverTime;//预约发货时间
    private String unitName;
    private String orderSplitItemId;
    private String orderSplitId;
}
