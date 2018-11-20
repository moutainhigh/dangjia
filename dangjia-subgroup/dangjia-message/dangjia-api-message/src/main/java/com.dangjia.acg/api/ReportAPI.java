package com.dangjia.acg.api;

import cn.jmessage.api.reportv2.GroupStatListResult;
import cn.jmessage.api.reportv2.MessageStatListResult;
import cn.jmessage.api.reportv2.UserStatListResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 统计维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@FeignClient("dangjia-service-message")
@Api(value = "统计维护接口", description = "统计维护维护接口")
public interface ReportAPI {


    /**
     * 用户统计
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param startTime 开始时间time_unit为DAY的时候格式为yyyy-MM-dd
     * @param duration 请求时的持续时长，DAY最大为60天
     */
    @RequestMapping(value = "getUserStat", method = RequestMethod.POST)
    @ApiOperation(value = "用户统计", notes = "用户统计")
     UserStatListResult getUserStat(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="startTime",value = "开始时间time_unit为DAY的时候格式为yyyy-MM-dd")@RequestParam("startTime") String startTime,
            @ApiParam(name ="duration",value = "请求时的持续时长，DAY最大为60天")@RequestParam("duration") int duration);
    /**
     * 消息统计
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param timeUnit (必填）查询维度目前有HOUR DAY MONTH三个维度可以选
     * @param start （必填）开始时间time_unit为HOUR时格式为yyyy-MM-dd HH，DAY的时候格式为yyyy-MM-dd，MONTH的时候格式为yyyy-MM
     * @param duration 请求时的持续时长HOOK只支持查询当天的统计，DAY最大为60天，MOTH为两个月
     */
    @RequestMapping(value = "getMessageStat", method = RequestMethod.POST)
    @ApiOperation(value = "消息统计", notes = "消息统计")
     MessageStatListResult getMessageStat(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="timeUnit",value = "(必填）查询维度目前有HOUR DAY MONTH三个维度可以选")@RequestParam("timeUnit") String timeUnit,
            @ApiParam(name ="start",value = "(必填）开始时间time_unit为HOUR时格式为yyyy-MM-dd HH，DAY的时候格式为yyyy-MM-dd，MONTH的时候格式为yyyy-MM")@RequestParam("start")  String start,
            @ApiParam(name ="duration",value = "请求时的持续时长HOOK只支持查询当天的统计，DAY最大为60天，MOTH为两个月")@RequestParam("duration")int duration);
    /**
     * 群组统计
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param start （必填）开始时间time_unit为DAY的时候格式为yyyy-MM-dd
     * @param duration 请求时的持续时长，DAY最大为60天
     */
    @RequestMapping(value = "getGroupStat", method = RequestMethod.POST)
    @ApiOperation(value = "群组统计", notes = "群组统计")
     GroupStatListResult getGroupStat(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="start",value = "（必填）开始时间time_unit为DAY的时候格式为yyyy-MM-dd")@RequestParam("start") String start,
            @ApiParam(name ="duration",value = "请求时的持续时长，DAY最大为60天")@RequestParam("duration") int duration) ;

}
