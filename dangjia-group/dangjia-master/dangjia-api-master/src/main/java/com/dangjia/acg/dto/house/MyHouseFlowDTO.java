package com.dangjia.acg.dto.house;

import lombok.Data;

import java.util.Date;

/**
 * @author Ruking.Cheng
 * @descrilbe 工匠任务返回体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/3 4:53 PM
 */
@Data
public class MyHouseFlowDTO {
    private String houseFlowId;//任务id
    private String houseId;//houseId
    private String memberId;//用户ID
    private String workerTypeId;//工种ID
    private String price = "¥0.00";//价格
    private String houseName;//地址
    private Date releaseTime;//发布时间
    private String square;//面积
    private String memberName="";//业主姓名
    private String isItNormal;//正常施工
    private String houseIsStart;//没有今日开工记录
    private int taskNumber;//任务数量
}
