package com.dangjia.acg.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: Qiyuxiang
 * @date: 2018/5/8
 */
@FeignClient("dangjia-service-job")
@Api(value = "定时任务接口", description = "定时任务接口")
public interface JobServiceAPI {


  @RequestMapping(value = "createTask", method = RequestMethod.POST)
  @ApiOperation(notes = "更新微信推送接口", value = "更新微信推送接口")
  void createTask(@ApiParam(name = "templateCode", value = "templateCode") @RequestParam("templateCode") String templateCode,
                                @ApiParam(name = "scheduled", value = "定时任务时间") @RequestParam("scheduled") String scheduled);
}
