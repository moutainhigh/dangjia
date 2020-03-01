package com.dangjia.acg.dto.engineer;

import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 */
@Data
public class DimensionRecordDTO {

    private Date createDate;// 创建日期
    private Date paymentDate;//支付时间
    private Double sincePurchaseAmount;//维保金额
    private Double paymentAmount;//付款金额
    private Double proportion;//维保责任方占比
    private String mrId;//totalPrice
    private String mrrpId;
    private String houseName;//房子名称
    private String houseId;//房子名称
    private Double enoughAmount; //自购金额
    private String responsiblePartyId;//工匠id
    private String stewardRemark;//自购备注
    private Integer type;//0-申诉 1-申诉中  2-已完成
    private String finishDate;//完工时间
//    private List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS;//维保商品列表
    private List<ActuarialProductAppDTO> productList;//维保商品列表
    private Double totalPrice;//商品总额
    private Double maintenanceTotalPrice;//付款金额
    private String str;//备注
    private String ownerName;//业主名称;
    private Double square;//房子面积
    private String serviceRemark;//客服备注
    private List<Map<String,Object>> list;//详情
    private String workerTypeId;//工种id
    private Integer pageType;//1-工匠先进场,2管家先进场
    private Integer primaryType;//1-原工匠,2 非原工匠

    private Integer maintenanceType;//维保通知类型：1，责任通知，2最终责任通知
    private Integer isComplain;//是否可申诉（1可申诉，0不可申诉，已过期）
    private Double stevedorageCost;//搬运费
    private Double transportationCost;//运费
    private String complainId;//申诉ID
    private List<Map<String,Object>> reimbursementInFo;//报销信息
}
