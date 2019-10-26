package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 26/10/2019
 * Time: 上午 11:58
 */
@Data
public class DjDeliveryReturnSlipDetailsListDTO {
    private String number;
    private String name;//业主名字
    private String mobile;//业主电话号码
    private String shipAddress;
    private String workerName;//大管家名字
    private String workerMobile;//大管家电话
    private Double sumPrice;
    private List<DjDeliveryReturnSlipDetailsDTO> djDeliveryReturnSlipDetailsDTOS;
}
