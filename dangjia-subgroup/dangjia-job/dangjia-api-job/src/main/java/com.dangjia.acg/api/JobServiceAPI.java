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


}
