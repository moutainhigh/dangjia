package com.dangjia.acg.dto.core;

import lombok.Data;

/**
 * @author Ruking.Cheng
 * @descrilbe 抢单列表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/12/1 10:22 AM
 */
@Data
public class AllgrabBean {
    private String houseName;
    private String houseFlowId;//任务id
    private String square;//面积
    private String houseMember;//	业主名称
    private String workerTypeId;//	工种类型的id
    private String workertotal;//价格
    private String releaseTime;//发布时间
    private String butType;//按钮状态  0=抢单  1=已被抢单（灰色）
    private long countDownTime;//倒计时（可抢单时间）
}
