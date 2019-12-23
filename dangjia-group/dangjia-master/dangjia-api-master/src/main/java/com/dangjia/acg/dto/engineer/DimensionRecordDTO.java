package com.dangjia.acg.dto.engineer;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 */
@Data
public class DimensionRecordDTO {

    private Date paymentDate;//支付时间
    private Double sincePurchaseAmount;//自购金额
    private Double paymentAmount;//付款金额
    private Double proportion;//维保责任方占比
    private String mrId;//
    private String mrrpId;
    private String houseName;//房子id

}
