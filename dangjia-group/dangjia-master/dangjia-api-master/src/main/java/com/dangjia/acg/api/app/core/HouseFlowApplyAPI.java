package com.dangjia.acg.api.app.core;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/11/26 0026
 * Time: 20:15
 */
@FeignClient("dangjia-service-master")
@Api(value = "业主验收审核", description = "业主验收审核")
public interface HouseFlowApplyAPI {

    @PostMapping("app/core/houseFlowApply/houseRecord")
    @ApiOperation(value = "工匠端工地记录", notes = "工匠端工地记录")
    ServerResponse houseRecord(@RequestParam("userToken")String userToken, @RequestParam("houseId")String houseId,
                               @RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize")Integer pageSize);

    @PostMapping("app/core/houseFlowApply/checkWorker")
    @ApiOperation(value = "每日申请直接审核", notes = "每日申请直接审核")
    ServerResponse checkWorker(@RequestParam("userToken")String userToken,@RequestParam("houseFlowApplyId")String houseFlowApplyId);

    @PostMapping("app/core/houseFlowApply/checkDetail")
    @ApiOperation(value = "验收详情", notes = "验收详情")
    ServerResponse checkDetail(@RequestParam("userToken")String userToken,@RequestParam("houseFlowApplyId")String houseFlowApplyId);

    @PostMapping("app/core/houseFlowApply/stewardCheckDetail")
    @ApiOperation(value = "管家端验收详情", notes = "管家端验收详情")
    ServerResponse stewardCheckDetail(@RequestParam("userToken")String userToken,@RequestParam("houseFlowApplyId")String houseFlowApplyId);
}
