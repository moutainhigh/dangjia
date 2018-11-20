package com.dangjia.acg.controller;

import com.dangjia.acg.api.JobServiceAPI;
import com.dangjia.acg.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务
 *
 * @author: Qiyuxiang
 * @date: 2018/5/8
 */
@RestController
public class JobController implements JobServiceAPI {

  @Autowired
  public JobService jobService;

  @Override
  public void createTask(String templateCode, String scheduled) {
    jobService.createTask(templateCode, scheduled);
  }
}
