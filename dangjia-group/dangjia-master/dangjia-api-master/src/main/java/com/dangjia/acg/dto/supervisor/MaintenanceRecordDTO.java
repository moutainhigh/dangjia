package com.dangjia.acg.dto.supervisor;

import lombok.Data;

@Data

public class MaintenanceRecordDTO {

    private String stewardId;//接单管家id
    private String houseId;//房子id
    private String modifyDate;//修改时间
    private String sincePurchaseAmount;//自购金额
    private String supervisorId;//督导id
    private Integer dataStatus;//数据状态
    private String remark;//自购商品备注
    private String stewardRemark;//管家备注
    private Integer stewardState;//管家处理状态 1：待处理 2：已处理
    private String stewardProcessingTime;//管家处理时间
    private String ownerRemark;//业主备注
    private String ownerMobile;//业主电话号码
    private String ownerName;//业主名称
    private String ownerImage;//业主上传图片(逗号分隔)
    private String stewardImage;//管家上传图片(逗号分隔)
    private String[] ownerImageDetail;//
    private String id;//表id
    private String serviceRemark;//客服备注
    private Integer state;//状态 1:待审核 2:已通过 3:已拒绝
    private String createDate;//创建时间
    private String memberId;//业主id

}
