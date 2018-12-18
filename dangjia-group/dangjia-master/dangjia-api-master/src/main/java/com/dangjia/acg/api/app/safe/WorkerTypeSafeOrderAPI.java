package com.dangjia.acg.api.app.safe;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:50
 */
@FeignClient("dangjia-service-master")
@Api(value = "工序保险接口", description = "工序保险接口")
public interface WorkerTypeSafeOrderAPI {

    @PostMapping("app/safe/workerTypeSafeOrder/changeSafeType")
    @ApiOperation(value = "切换工序保险", notes = "切换工序保险")
    ServerResponse changeSafeType(@RequestParam("userToken")String userToken, @RequestParam("houseFlowId")String houseFlowId,
                                  @RequestParam("workerTypeSafeId")String workerTypeSafeId, @RequestParam("selected")int selected);

    @PostMapping("app/safe/order/list")
    @ApiOperation(value = "我的质保卡", notes = "我的质保卡")
    ServerResponse queryMySafeTypeOrder(String userToken, String houseId, PageDTO pageDTO);

    @PostMapping("app/safe/order/detail")
    @ApiOperation(value = "我的质保卡明细", notes = "我的质保卡明细")
    ServerResponse getMySafeTypeOrderDetail(String id);
}
