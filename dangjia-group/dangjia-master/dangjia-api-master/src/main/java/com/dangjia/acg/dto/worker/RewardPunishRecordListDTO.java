package com.dangjia.acg.dto.worker;

import lombok.Data;

@Data
public class RewardPunishRecordListDTO {
    private String id;//奖罚记录表id
    private String type;//奖罚类型
    private String name;//奖罚名称
    private String workerTypeId;//工种id
    private String createDate;//奖罚时间
    private String operatorName;//督导
    private String images ;//奖罚图片集合
    private String[] imagesUrl;
    private String workerTypeName;//工种名称
    RewardPunishRecordDetailDTO rewardPunishRecordDetailDTO;//详情对象
}
