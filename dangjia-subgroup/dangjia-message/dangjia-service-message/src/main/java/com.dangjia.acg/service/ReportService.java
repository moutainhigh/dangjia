package com.dangjia.acg.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jmessage.api.JMessageClient;
import cn.jmessage.api.reportv2.GroupStatListResult;
import cn.jmessage.api.reportv2.MessageStatListResult;
import cn.jmessage.api.reportv2.UserStatListResult;
import org.springframework.stereotype.Service;

/**
 * 统计维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@Service
public class ReportService  extends BaseService{

    /**
     * 用户统计
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param startTime 开始时间time_unit为DAY的时候格式为yyyy-MM-dd
     * @param duration 请求时的持续时长，DAY最大为60天
     */
    public UserStatListResult getUserStat(String appType,String startTime, int duration) {
        try {

            JMessageClient mClient = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            UserStatListResult result = mClient.getUserStatistic(startTime, duration);
            LOG.info("Got result: " + result);
            return result;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }
    /**
     * 消息统计
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param timeUnit (必填）查询维度目前有HOUR DAY MONTH三个维度可以选
     * @param start （必填）开始时间time_unit为HOUR时格式为yyyy-MM-dd HH，DAY的时候格式为yyyy-MM-dd，MONTH的时候格式为yyyy-MM
     * @param duration 请求时的持续时长HOOK只支持查询当天的统计，DAY最大为60天，MOTH为两个月
     */
    public MessageStatListResult getMessageStat(String appType,String timeUnit, String start, int duration) {
        try {

            JMessageClient mClient = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            MessageStatListResult result = mClient.getMessageStatistic(timeUnit, start, duration);
            LOG.info("Got result: " + result);
            return result;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }
    /**
     * 群组统计
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param start （必填）开始时间time_unit为DAY的时候格式为yyyy-MM-dd
     * @param duration 请求时的持续时长，DAY最大为60天
     */
    public GroupStatListResult getGroupStat(String appType,String start, int duration) {
        try {

            JMessageClient mClient = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            GroupStatListResult result = mClient.getGroupStatistic(start, duration);
            LOG.info("Got result: " + result);
            return result;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }

}
