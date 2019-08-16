package com.dangjia.acg.timer;

import com.dangjia.acg.api.config.ConfigMessageAPI;
import com.dangjia.acg.api.sale.rob.RobAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.clue.ClueTalkDTO;
import com.dangjia.acg.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Component
public class ClueTalkTask {
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private RobAPI robAPI;
    @Autowired
    private ConfigMessageAPI configMessageAPI;

    private Logger log = LoggerFactory.getLogger(ClueTalkTask.class);
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 沟通提醒（上午10点整）
     */
//    @Scheduled(cron = "0 0 0,10,18,21 * * ?") //每天的0点、10点、18点、21点都执行一次
//    @Scheduled(cron = "0 0 10 * * ?") //每天上午10点整触发
    @Scheduled(cron = "0 0/1 * * * ?")//1分钟执行一次
    public void messageTips() {
        log.info(format.format(new Date()) + "开始执行沟通记录提醒任务...");
        List<ClueTalkDTO> todayDescribes = robAPI.getTodayDescribes();
        for (ClueTalkDTO todayDescribe : todayDescribes) {
            configMessageAPI.addConfigMessage(todayDescribe.getTargetUid(),"沟通提醒",
                    "您有待沟通的客户【" + todayDescribe.getPhone() + "】",0, configUtil.getValue(SysConfig.PUBLIC_SALE_APP_ADDRESS, String.class)
                            + Utils.getCustomerDetails(todayDescribe.getMemberId(), todayDescribe.getClueId(), todayDescribe.getPhaseStatus(), todayDescribe.getStage().toString()));
        }
        log.info(format.format(new Date()) + "结束执行沟通记录提醒任务...");
    }

//    @Scheduled(cron = "0/1 * * * * ?")//1s执行一次
    @Scheduled(cron = "0 0/1 * * * ?")//1分钟执行一次
    public void notEnteredGrabSheet() {
        log.info(format.format(new Date()) + "开始执行销售抢单计时任务...");
        robAPI.notEnteredGrabSheet();
        log.info(format.format(new Date()) + "结束执行销售抢单计时任务...");
    }





}
