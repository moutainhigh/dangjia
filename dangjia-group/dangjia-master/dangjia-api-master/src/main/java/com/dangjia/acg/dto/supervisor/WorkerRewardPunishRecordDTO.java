package com.dangjia.acg.dto.supervisor;

import lombok.Data;

@Data
public class WorkerRewardPunishRecordDTO {
    private String name;//督导名称
    private String mobile;//联系方式
    private String shipAddress;//房子地址
    private String type;//类型
    private String id;//惩罚记录表ID
    private  String rewordPunishCorrelationId;
}
