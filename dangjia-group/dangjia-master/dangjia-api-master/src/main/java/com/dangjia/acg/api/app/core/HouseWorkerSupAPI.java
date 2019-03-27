package com.dangjia.acg.api.app.core;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2019/3/27 0027
 * Time: 11:29
 */
@FeignClient("dangjia-service-master")
@Api(value = "施工业务补充", description = "施工业务补充")
public interface HouseWorkerSupAPI {

    @PostMapping("app/core/houseWorkerSup/applyShutdown")
    @ApiOperation(value = "工匠申请停工", notes = "工匠申请停工")
    ServerResponse applyShutdown(@RequestParam("userToken")String userToken, @RequestParam("houseFlowId")String houseFlowId, @RequestParam("applyDec")String applyDec,
                                 @RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate);
}
