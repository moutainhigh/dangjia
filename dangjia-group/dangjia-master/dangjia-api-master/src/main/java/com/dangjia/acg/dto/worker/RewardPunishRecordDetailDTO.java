package com.dangjia.acg.dto.worker;

import lombok.Data;

@Data
public class RewardPunishRecordDetailDTO {
    private String name;//工匠名称
    private String head;//工匠头像
    private String workerTypeId;//工种类型
    private String workerTypeName;//工种名称
    private String mobile;//工人手机号码
    private String punishName;//奖罚原因
    private String content;//奖罚说明
    private String createDate;//奖罚创建时间
    private String operatorName;//督导名称
}
