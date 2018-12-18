package com.dangjia.acg.timer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.web.red.ActivityAPI;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 活动优惠券定时器
 *
 * @author qiyuxiang
 * @date 2018/11/23
 */
@Component
public class ActivityTask {

  @Autowired
  private ActivityAPI activityAPI;

  private Logger log = LoggerFactory.getLogger(ActivityTask.class);
  private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * 检测活动是否过期
   * 每天凌晨(24点)执行一次
   */
  @Scheduled(cron = "0 0 0 * * ?") //每天凌晨(24点执行一次)
//  @Scheduled(cron = "0 0/5 * * * ?")//5分钟执行一次
//  @Scheduled(cron = "0/30 * * * * ?")//30秒执行一次
  public void couponActivityOverdue() {
    log.info(format.format(new Date()) + "开始执行活动检测任务...");
    Activity activity=new Activity();
    activity.setDeleteState(0);
    activity.setEndDate(new Date());
    ServerResponse serverResponse=activityAPI.queryActivitys(null,new PageDTO(),activity,"0");
    if(serverResponse.getResultObj()!=null){
      JSONObject pageInfo=(JSONObject)serverResponse.getResultObj();
      JSONArray activityList=(JSONArray)pageInfo.get("list");
      for (int i = 0; i < activityList.size(); i++) {
        JSONObject job = activityList.getJSONObject(i);
        activityAPI.closeActivity(job.getString("id"));
      }
    }
    log.info(format.format(new Date()) + "结束执行活动检测任务...");
  }

  /**
   * 检测优惠券是否过期
   * 每天凌晨(24点)执行一次
   */
  @Scheduled(cron = "0 0 0 * * ?") //每天凌晨(24点执行一次)
//  @Scheduled(cron = "0 0/5 * * * ?")//5分钟执行一次
//  @Scheduled(cron = "0/30 * * * * ?")//30秒执行一次
  public void couponActivityRedOverdue() {
    log.info(format.format(new Date()) + "开始执行优惠券检测任务...");
    ActivityRedPack activity=new ActivityRedPack();
    activity.setDeleteState(0);
    activity.setEndDate(new Date());
    ServerResponse serverResponse=activityAPI.queryActivityRedPacks(null,new PageDTO(),activity,"0");
    if(serverResponse.getResultObj()!=null){
      JSONObject pageInfo=(JSONObject)serverResponse.getResultObj();
      JSONArray activityList=(JSONArray)pageInfo.get("list");
      for (int i = 0; i < activityList.size(); i++) {
        JSONObject job = activityList.getJSONObject(i);
        activityAPI.closeActivityRedPack(job.getString("id"));
      }
    }
    log.info(format.format(new Date()) + "结束执行优惠券检测任务...");
  }

  /**
   * 检测优惠券是否过期
   * 每天凌晨(24点)执行一次
   */
  @Scheduled(cron = "0 0 0 * * ?") //每天凌晨(24点执行一次)
//  @Scheduled(cron = "0 0/5 * * * ?")//5分钟执行一次
//  @Scheduled(cron = "0/30 * * * * ?")//30秒执行一次
  public void couponActivityRedRecordOverdue() {
    log.info(format.format(new Date()) + "开始执行优惠券检测任务...");
    List<ActivityRedPackRecord>  activityList=activityAPI.queryRedPackRecord();
    if(activityList!=null&&activityList.size()>0){
      for (int i = 0; i < activityList.size(); i++) {
        ActivityRedPackRecord job = activityList.get(i);
        activityAPI.closeActivityRedPackRecord(job.getId());
      }
    }
    log.info(format.format(new Date()) + "结束执行优惠券检测任务...");
  }
}
