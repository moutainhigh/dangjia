package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 11:48
 */
@Data
public class DjDeliveryReturnSlipDTO{

    private String id;
    private Date createDate;
    private String shopId;
    private String shopName;
    private String storekeeperName;
    private String shipAddress;
    private String shipName;
    private String shipMobile;
    private String shippingState;
    private Double totalAmount;
    private String number;
    private String invoiceType;
    private String applyState;
    private String splitId;
    private String status;//部分收货申请平台申述状态. 0:待处理。1.驳回。2.接受。
    private Double applyMoney;
    private String mobile;
}
