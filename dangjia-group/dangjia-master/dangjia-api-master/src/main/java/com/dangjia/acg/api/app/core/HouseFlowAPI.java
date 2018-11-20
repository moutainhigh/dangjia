package com.dangjia.acg.api.app.core;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.core.HouseFlow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 17:48
 */
@FeignClient("dangjia-service-master")
@Api(value = "工序接口", description = "工序接口")
public interface HouseFlowAPI {

    @PostMapping("app/core/houseFlow/getGrabList")
    @ApiOperation(value = "抢单列表", notes = "抢单列表")
    ServerResponse getGrabList(@RequestParam("userToken")String userToken,@RequestParam("cityId")String cityId);

    @PostMapping("app/core/houseFlow/setGrabVerification")
    @ApiOperation(value = "抢单验证", notes = "抢单验证")
    ServerResponse setGrabVerification(@RequestParam("userToken")String userToken,@RequestParam("houseFlowId")String houseFlowId);

    @PostMapping("app/core/houseFlow/setGiveUpOrder")
    @ApiOperation(value = "放弃此单", notes = "放弃此单")
    ServerResponse setGiveUpOrder(@RequestParam("userToken")String userToken,@RequestParam("houseFlowId")String houseFlowId);

    @PostMapping("app/core/houseFlow/setRefuse")
    @ApiOperation(value = "拒单", notes = "拒单")
    ServerResponse setRefuse(@RequestParam("userToken")String userToken,@RequestParam("houseFlowId")String houseFlowId);

    @PostMapping("app/core/houseFlow/setConfirmStart")
    @ApiOperation(value = "确认开工", notes = "确认开工")
    ServerResponse setConfirmStart(@RequestParam("userToken")String userToken,@RequestParam("houseFlowId")String houseFlowId);

    @PostMapping("app/core/houseFlow/getFlowByhouseIdNot12")
    @ApiOperation(value = "根据houseId查询除设计精算外的可用工序", notes = "根据houseId查询除设计精算外的可用工序")
    List<HouseFlow> getFlowByhouseIdNot12(@RequestParam("houseId")String houseId);

    @PostMapping("app/core/houseFlow/getHouseFlowByHidAndWty")
    @ApiOperation(value = "根据houseId和工种类型查询HouseFlow", notes = "根据houseId和工种类型查询HouseFlow")
    HouseFlow  getHouseFlowByHidAndWty(@RequestParam("houseId")String houseId,@RequestParam("workerType")Integer workerType);

}
