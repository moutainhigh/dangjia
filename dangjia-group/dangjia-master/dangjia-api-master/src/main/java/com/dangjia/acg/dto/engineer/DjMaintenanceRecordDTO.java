package com.dangjia.acg.dto.engineer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 10:09
 */
@Data
public class DjMaintenanceRecordDTO {

    private String id;
    private String houseId;
    private String houseAddress;//房子地址
    private String ownerName;//业主名称
    private String ownerMobile;//业主电话
    private Integer state;//状态 1:待审核 2:已审核
    private Date createDate;//发起时间
    private Integer stewardState;//管家处理状态 1：待处理 2：已处理
    private String ownerImage;//业主上传图片
    private List<String> ownerImages;
    private String ownerRemark;//业主备注
    private String supervisorId;//督导id
    private String supervisorName;//督导名称
    private Integer stewardSubsidy;//是否需要补贴管家 1:是 2:否
    private String serviceRemark;//客服备注
    private String stewardId;//接单管家id
    private String stewardName;//接单管家名称
    private Date stewardProcessingTime;//管家处理时间
    private Date stewardOrderTime;//管家接单时间
    private String stewardMobile;//管家电话
    private List<DjMaintenanceRecordResponsiblePartyDTO> djMaintenanceRecordResponsiblePartyDTOS;//维保责任方
    private List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS;//维保商品列表
    private Double sincePurchaseAmount;//自购金额
    private String remark;//自购商品备注
    private Integer ownerState;//状态 1:待业主确认 2:业主已确认 3:业主已拒绝
    private String stewardImage;//管家上传图片
    private List<String> stewardImages;
    private String stewardRemark;//管家备注

    private Integer complainType;//申诉类型：9-业主申请质保
    private Integer handleType;//客服处理状态0-待处理 1-驳回 2-接受 3-已处理 4-已结束
    private String userId;//处理人id
    private String workerMemberId;//工匠id
    private String workerMemberName;//工匠名称
    private String workerMobile;//工匠手机号码
    private String workerCreateDate;//工匠接单时间
    private String workerImage;//工匠上传图片
    private List<String> workerImages;//工匠上传图片list

}
