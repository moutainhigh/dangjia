package com.dangjia.acg.dto.delivery;

import lombok.Data;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 下午 3:27
 */
@Data
public class DjDeliveryReturnSlipDetailsDTO {

    private String number;
    private String shipAddress;
    private Double totalPrice;//总价
    private String houseId;
    private Integer num;//本次发货数量
    private Double price;//单价
    private Integer shopCount;//购买数量
    private String image;
    private String productName;
    private String productSn;
    private Integer actualCount;
    private Integer receive;
    private String orderSplitItemId;
    private String repairMendMaterielId;

}
