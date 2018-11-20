package com.dangjia.acg.controller;

import cn.jmessage.api.reportv2.GroupStatListResult;
import cn.jmessage.api.reportv2.MessageStatListResult;
import cn.jmessage.api.reportv2.UserStatListResult;
import com.dangjia.acg.api.ReportAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@RestController
public class ReportController implements ReportAPI {

    @Autowired
    private ReportService reportService;

    /**
     * 用户统计
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param startTime 开始时间time_unit为DAY的时候格式为yyyy-MM-dd
     * @param duration 请求时的持续时长，DAY最大为60天
     */
    @Override
    @ApiMethod
    public UserStatListResult getUserStat(String appType,String startTime, int duration) {
        return reportService.getUserStat( appType, startTime,  duration);
    }
    /**
     * 消息统计
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param timeUnit (必填）查询维度目前有HOUR DAY MONTH三个维度可以选
     * @param start （必填）开始时间time_unit为HOUR时格式为yyyy-MM-dd HH，DAY的时候格式为yyyy-MM-dd，MONTH的时候格式为yyyy-MM
     * @param duration 请求时的持续时长HOOK只支持查询当天的统计，DAY最大为60天，MOTH为两个月
     */
    @Override
    @ApiMethod
    public MessageStatListResult getMessageStat(String appType,String timeUnit, String start, int duration) {
        return reportService.getMessageStat( appType, timeUnit,  start,duration);
    }
    /**
     * 群组统计
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param start （必填）开始时间time_unit为DAY的时候格式为yyyy-MM-dd
     * @param duration 请求时的持续时长，DAY最大为60天
     */
    @Override
    @ApiMethod
    public GroupStatListResult getGroupStat(String appType,String start, int duration) {
        return reportService.getGroupStat( appType,   start,duration);
    }

}
