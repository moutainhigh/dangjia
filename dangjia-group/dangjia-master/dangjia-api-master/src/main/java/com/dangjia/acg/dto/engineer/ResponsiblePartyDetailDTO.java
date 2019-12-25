package com.dangjia.acg.dto.engineer;

import lombok.Data;

@Data
public class ResponsiblePartyDetailDTO {
    private String createDate;
    private String address;
    private double totalAmount;//维保总金额
    private String proportion;//维保责任方占比
    private String workerTypeId;//工匠类型
    private String workerTypeName;//工种类型名称
    private String content;//缴纳描述
    private Double needRetentionMoney;//所需质保金
    private Double retentionMoney;//现有质保金
    private Double paidRetentionMoney;//需要缴纳质保金额
    private String houseId;//房子id
}
