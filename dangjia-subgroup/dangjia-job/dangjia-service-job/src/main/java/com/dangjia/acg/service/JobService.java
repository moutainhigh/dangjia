package com.dangjia.acg.service;

import com.dangjia.acg.dao.ConfigUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

/**
 * 定时任务
 *
 * @author: Qiyuxiang
 * @date: 2018/5/8
 */
@Service
public class JobService {


  private static org.slf4j.Logger log = LoggerFactory.getLogger(JobService.class);

  @Autowired
  private ThreadPoolTaskScheduler threadPoolTaskScheduler;


  @Autowired
  private ConfigUtil configUtil;


  @Bean
  public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
    return new ThreadPoolTaskScheduler();
  }

  public void createTask(String templateCode, String scheduled) {
    // 新增任务
    log.info(">>>>>>>>> 新增定时任务@templateCode:" + templateCode + " @scheduled:" + scheduled);


  }

}
