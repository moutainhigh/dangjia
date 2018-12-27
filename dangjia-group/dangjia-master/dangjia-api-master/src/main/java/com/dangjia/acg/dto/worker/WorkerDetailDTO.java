package com.dangjia.acg.dto.worker;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author: Ronalcheng
 * Date: 2018/11/28 0028
 * Time: 19:27
 */
@Data
public class WorkerDetailDTO {

    private String workerId;
    private String workerTypeName;
    private String head;//工匠头像
    private String praiseRate;//好评率
    private String name;//姓名
    private String mobile;//手机号
    private BigDecimal evaluationScore;//评价积分
    private long countOrder;//总单数
    private String isStart;//是否开工
    private long everyDay;//每日完工天数
    private long totalDay;//总开工天数
    private long suspendDay;//暂停天数
    private int patrol;//巡查标准
    private long patrolled;//已巡查次数
}
