package com.dangjia.acg.dto.engineer;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 11:01
 */
@Data
public class DjMaintenanceManualAllocationDTO {

    private String manuaId;//人工定责记录ID
    private String address;//工地地址
    private String memberId;//业主ID
    private String memeberName;//业主姓名
    private String membreMobile;//业主电话
    private Double money;//涉及金额
    private Date createDate;//产生时间
    private String operatorId;//处理人
    private String operatorName;//处理人姓名
    private Integer status;//处理状态（1待处理，2已处理）

    private String complainId;//申诉ID
    private String maintenanceRecordId;//维保单ID
    private String productId;
    private String productName;

    private List<Map<String,Object>> responsiblePartylist;//维保责任点比记录
    private List<Map<String,Object>> recordContentList;//相关凭证列表
    private List<Map<String,Object>> newPartyList;//最新定责记录

}
