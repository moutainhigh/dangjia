package com.dangjia.acg.dto.supervisor;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PatrolRecordDTO {
    private String patrolRecordId;//记录ID
    private String operatorId;//用户ID
    private String operatorName;//用户名称
    private String operatorMobile;//用户手机
    private String houseId;//房子ID
    private String houseName;//房子名称
    private String content;//巡查内容or奖罚说明
    private Integer type;//0:奖励;1:处罚,2:巡查
    private String images;//巡查图片or奖罚图片
    private List<String> imageList;//巡查图片or奖罚图片全地址
    private String memberId;//被奖惩的用户ID
    private String memberName;//被奖惩的用户名称
    private String memberMobile;//被奖惩的用户手机
    private String memberHead;//被奖惩的用户头像
    private String workerTypeName;//被奖惩的工种名称
    private Integer workerTypeType;//被奖惩的工种
    private Date createDate;// 创建日期
    private String rewardPunishCorrelation;//奖罚原因
    private String rewardPunishId;//奖罚ID
}
