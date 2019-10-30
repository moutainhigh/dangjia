package com.dangjia.acg.dto.delivery;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/10/2019
 * Time: 下午 3:22
 */
@Data
public class OrderStorefrontDTO {

    private String storefrontId;
    private String orderId;
    private String addressId;
    private String houseId;
    private String businessOrderNumber;
    private String orderNumber;
    private String storefrontName;
}
