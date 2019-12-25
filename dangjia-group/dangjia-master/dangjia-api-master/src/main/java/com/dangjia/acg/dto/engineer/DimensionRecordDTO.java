package com.dangjia.acg.dto.engineer;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 */
@Data
public class DimensionRecordDTO {

    private Date paymentDate;//支付时间
    private Double sincePurchaseAmount;//维保金额
    private Double paymentAmount;//付款金额
    private Double proportion;//维保责任方占比
    private String mrId;//
    private String mrrpId;
    private String houseName;//房子名称
    private String houseId;//房子名称
    private Double enoughAmount; //自购金额
    private String responsiblePartyId;//工匠id
    private String stewardRemark;//管家备注
    private int type;//0-申诉 1-申诉中  2-已完成
    private String finishDate;//完工时间
    private List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS;//维保商品列表

    private String str;//备注



}
