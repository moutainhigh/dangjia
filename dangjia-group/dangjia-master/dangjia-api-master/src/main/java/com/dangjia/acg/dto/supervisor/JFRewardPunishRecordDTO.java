package com.dangjia.acg.dto.supervisor;

import lombok.Data;

@Data
public class JFRewardPunishRecordDTO {

    private String type;//类型
    private String  createDate;//时间
    private String  name;//对象
    private String  workerType;//工种
    private String workerTypeName;//工种名称
    private String  mobile;//联系方式
    private String  address;//房子地址
    private String  rpcName;//奖励|惩罚原因
    private String  content;// 奖励|惩罚说明
    private String images;
    private String [] imagesDetail;

}
