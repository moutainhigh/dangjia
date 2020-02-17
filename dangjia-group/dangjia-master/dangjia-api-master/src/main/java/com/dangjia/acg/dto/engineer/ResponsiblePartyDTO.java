package com.dangjia.acg.dto.engineer;

import lombok.Data;

@Data
public class ResponsiblePartyDTO {

    private String accountflowRecordId;//流水记录ID
    private String maintenanceRecordId;//维保单ID
    private String address;//房子地址
    private String createDate;//创建时间
    private String state;//变动类型：6质保金扣除，7质保返还



}
