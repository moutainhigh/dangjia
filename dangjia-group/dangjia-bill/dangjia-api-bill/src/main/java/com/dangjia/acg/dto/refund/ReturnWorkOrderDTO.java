package com.dangjia.acg.dto.refund;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReturnWorkOrderDTO {

    private  String repairWorkOrderId;//退款申请单ID

    private String repairWorkOrderName;//退人工申请名称

    private String repairWorkOrderNumber;//退人工申请单编号

    private String houseId;//房子ID

    private String workTypeId;//工种ID

    private String workTypeName;//工种名称

    private String workerId;//工匠ID

    private String workerName;//工匠姓名

    private String workerMobile;//工匠电话

    private String supId;//管家ID

    private String supMobile;//管家电话

    private String content;//申诉内容

    private Date applyDate;//申请时间

    private String stateName;//申请状态

    private Double actualTotalAmount;//退款总价

    private Double totalAmount;//实退款

    private String state;

    private String repairNewNode;//最新处理节点

    @ApiModelProperty("可操作编码")
    private String associatedOperation;

    @ApiModelProperty("可操作编码描述")
    private String associatedOperationName;

    private List<OrderProgressDTO> orderProgressList;//节点信息显示
    private List<RefundRepairOrderMaterialDTO> orderWorkerList;//人工商品信息


}
