package com.dangjia.acg.timer;

import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.api.web.finance.WebOrderAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 订单定时器
 *
 * @author qiyuxiang
 * @date 2018/11/23
 */
@Component
public class OrderTask {
    @Autowired
    private WebOrderAPI webOrderAPI;

    @Autowired
    private DjSupplierAPI djSupplierAPI;

    private Logger log = LoggerFactory.getLogger(OrderTask.class);

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI ;

  /**
   * 订单超时检测任务
   */
  @Scheduled(cron = "0 0 0 * * ?") //每天凌晨(24点执行一次)
  public void absenteeism() {
      log.info(format.format(new Date()) + "开始执行订单超时检测任务...");
      webOrderAPI.autoOrderCancel();
      log.info(format.format(new Date()) + "结束执行订单超时检测任务...");

      log.info(format.format(new Date()) + "开始计算供应商可提现金额任务...");
      djSupplierAPI.setSurplusMoney();
      log.info(format.format(new Date()) + "结束计算供应商可提现金额任务...");

      log.info(format.format(new Date()) + "开始计算店铺可提现金额任务...");
      basicsStorefrontAPI.setStorefrontSurplusMoney();
      log.info(format.format(new Date()) + "结束计算店铺可提现金额任务...");
  }
}
