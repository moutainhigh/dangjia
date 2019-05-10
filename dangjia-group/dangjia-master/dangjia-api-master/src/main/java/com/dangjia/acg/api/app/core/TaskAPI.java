package com.dangjia.acg.api.app.core;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 20:41
 */
@FeignClient("dangjia-service-master")
@Api(value = "业主APP任务接口", description = "业主APP任务接口")
public interface TaskAPI {

    @PostMapping("app/core/task/getTaskList")
    @ApiOperation(value = "任务列表", notes = "任务列表")
    ServerResponse getTaskList(@RequestParam("userToken")String userToken,@RequestParam("userRole")String userRole);
}
