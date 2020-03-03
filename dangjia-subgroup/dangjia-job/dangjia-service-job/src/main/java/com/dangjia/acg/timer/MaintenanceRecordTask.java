package com.dangjia.acg.timer;

import com.dangjia.acg.api.web.engineer.DjMaintenanceRecordAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 退款、退货退款售后
 *
 * @author fzh
 * @date 2019/11/05
 */
@Component
public class MaintenanceRecordTask {
    @Autowired
    private DjMaintenanceRecordAPI djMaintenanceRecordAPI;



    private Logger log = LoggerFactory.getLogger(MaintenanceRecordTask.class);
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 定时审核完工申请
     */
    @Scheduled(cron = "0 0/1 * * * ?")//1分钟执行一次
    public void couponApply() {
        log.info(format.format(new Date()) + "维保时间，到期自动验收任务...");
        djMaintenanceRecordAPI.saveAcceptanceApplicationJob();//工匠申请维保，业主验收，到期自动处理
        log.info(format.format(new Date()) + "维保时间，到期自动验收任务");
    }

}
