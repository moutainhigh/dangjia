package com.dangjia.acg.dto.refund;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReturnWorkOrderDTO {

    private  String repairWorkOrderId;//退款申请单ID

    private String repairWorkOrderName;//退人工申请名称

    private String houseId;//房子ID

    private String workTypeId;//工种ID

    private String workTypeName;//工种名称

    private String content;//申诉内容

    private Date applyDate;//退款申请时间

    private String stateName;//申请状态

    private String repairNewNode;//最新处理节点

}
