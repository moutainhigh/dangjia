package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 下午 3:27
 */
@Data
public class DjDeliveryReturnSlipDetailsDTO {

    private String id;
    private String name;//业主名字
    private String mobile;//业主电话号码
    private String address;
    private String shipName;
    private String shipMobile;
    private String totalPrice;
    private String workerName;//大管家名字
    private String workerMobile;//大管家电话
    private String houseId;

    private List<DjDeliveryReturnSlipDetailsProductDTO> djDeliveryReturnSlipDetailsProductDTOS;
}
