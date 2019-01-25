package com.dangjia.acg.timer;

import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.config.ConfigMessageAPI;
import com.dangjia.acg.api.data.TechnologyRecordAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ConfigMessage;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.house.House;
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
public class HouseTask {

  @Autowired
  private TechnologyRecordAPI technologyRecordAPI;

  @Autowired
  private ConfigMessageAPI configMessageAPI;
  @Autowired
  private HouseAPI houseAPI;



  private Logger log = LoggerFactory.getLogger(HouseTask.class);
  private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * 检测每日还未开工提醒
   * 今日开工提醒（上午11点整）
   */
  @Scheduled(cron = "0 0 11 * * ?") //每天上午11点整触发
  public void couponActivityOverdue() {
    log.info(format.format(new Date()) + "开始执行今日开工提醒任务...");
    List<HouseFlow> houseFlowList =technologyRecordAPI.unfinishedFlow(null);
    for (HouseFlow houseFlow:houseFlowList ) {
      House house=houseAPI.getHouseById(houseFlow.getHouseId());
      addConfigMessage(houseFlow.getWorkerId(),"今日开工提醒",String.format(DjConstants.PushMessage.CRAFTSMAN_NOT_START,house.getHouseName()));
    }
    log.info(format.format(new Date()) + "结束执行今日开工提醒任务...");
  }

  /**
   * 推送至个人消息
   * @param memberId 接收人
   * @param title 推送标题
   * @param alert 推送内容
   * @return
   */
  public ServerResponse addConfigMessage(String memberId,  String title, String alert){
    ConfigMessage configMessage=new ConfigMessage();
    configMessage.setAppType("2");
    configMessage.setTargetUid(memberId);
    configMessage.setTargetType("0");
    configMessage.setName(title);
    configMessage.setText(alert);
    configMessage.setType(2);
    return configMessageAPI.addConfigMessage(null,configMessage);
  }
}
