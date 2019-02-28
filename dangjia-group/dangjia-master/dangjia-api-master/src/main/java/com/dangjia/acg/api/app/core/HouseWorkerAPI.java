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
 * Time: 18:56
 */
@FeignClient("dangjia-service-master")
@Api(value = "工人和工序关联接口", description = "工人和工序关联接口")
public interface HouseWorkerAPI {

    @PostMapping("app/core/houseWorker/task")
    @ApiOperation(value = "根据工人id查询所有房子任务", notes = "根据工人id查询所有房子任务")
    ServerResponse queryWorkerHouse(@RequestParam("userToken") String userToken);

    @PostMapping("app/core/houseWorker/setWorkerGrab")
    @ApiOperation(value = "工匠抢单", notes = "工匠抢单")
    ServerResponse setWorkerGrab(@RequestParam("userToken") String userToken, @RequestParam("cityId") String cityId, @RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("app/core/houseWorker/setChangeWorker")
    @ApiOperation(value = "业主换人", notes = "业主换人")
    ServerResponse setChangeWorker(@RequestParam("userToken") String userToken, @RequestParam("houseWorkerId") String houseWorkerId);

    @PostMapping("app/core/houseWorker/getConstructionByWorkerId")
    @ApiOperation(value = "根据工人查询自己的施工界面", notes = "根据工人查询自己的施工界面")
    ServerResponse getConstructionByWorkerId(@RequestParam("userToken") String userToken, @RequestParam("cityId") String cityId);

    @PostMapping("app/core/houseWorker/getMyHomePage")
    @ApiOperation(value = "获取我的界面", notes = "获取我的界面")
    ServerResponse getMyHomePage(@RequestParam("userToken") String userToken, @RequestParam("cityId") String cityId);


    @PostMapping("app/core/houseWorker/setHouseFlowApply")
    @ApiOperation(value = "提交审核、停工", notes = "提交审核、停工")
    ServerResponse setHouseFlowApply(@RequestParam("userToken") String userToken, @RequestParam("applyType") Integer applyType, @RequestParam("houseFlowId") String houseFlowId,
                                            @RequestParam("suspendDay") Integer suspendDay, @RequestParam("applyDec") String applyDec, @RequestParam("imageList") String imageList,
                                            @RequestParam("houseFlowId2") String houseFlowId2);

    @PostMapping("app/core/houseWorker/getAdvanceInAdvance")
    @ApiOperation(value = "提前进场", notes = "提前进场")
    ServerResponse getAdvanceInAdvance(@RequestParam("userToken") String userToken, @RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("app/core/houseWorker/getHouseFlowList")
    @ApiOperation(value = "查询工地列表", notes = "查询工地列表")
    ServerResponse getHouseFlowList(@RequestParam("userToken") String userToken);

    @PostMapping("app/core/houseWorker/setSwitchHouseFlow")
    @ApiOperation(value = "切换工地", notes = "切换工地")
    ServerResponse setSwitchHouseFlow(@RequestParam("userToken") String userToken, @RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("app/core/houseWorker/setSupervisorApply")
    @ApiOperation(value = "大管家申请验收", notes = "大管家申请验收")
    ServerResponse setSupervisorApply(@RequestParam("userToken") String userToken, @RequestParam("houseFlowId") String houseFlowId);

}
