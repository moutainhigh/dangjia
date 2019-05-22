package com.dangjia.acg.api.app.repair;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2019/1/8 0008
 * Time: 14:03
 */
@FeignClient("dangjia-service-master")
@Api(value = "变更单", description = "变更单")
public interface ChangeOrderAPI {

    @PostMapping(value = "app/repair/changeOrder/supCheckChangeOrder")
    @ApiOperation(value = "管家审核变更单", notes = "管家审核变更单")
    ServerResponse supCheckChangeOrder(@RequestParam("userToken") String userToken,
                                       @RequestParam("changeOrderId") String changeOrderId,
                                       @RequestParam("check") Integer check);

    @PostMapping(value = "app/repair/changeOrder/changeOrderDetail")
    @ApiOperation(value = "变更单详情", notes = "变更单详情")
    ServerResponse changeOrderDetail(@RequestParam("changeOrderId") String changeOrderId);

    @PostMapping(value = "app/repair/changeOrder/queryChangeOrder")
    @ApiOperation(value = "查询变更单列表", notes = "查询变更单列表")
    ServerResponse queryChangeOrder(@RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId,
                                    @RequestParam("type") Integer type);

    @PostMapping(value = "app/repair/changeOrder/workerSubmit")
    @ApiOperation(value = "提交变更单", notes = "提交变更单")
    ServerResponse workerSubmit(@RequestParam("userToken") String userToken,
                                @RequestParam("houseId") String houseId,
                                @RequestParam("type") Integer type,
                                @RequestParam("contentA") String contentA,
                                @RequestParam("contentB") String contentB,
                                @RequestParam("workerTypeId") String workerTypeId);


    @PostMapping(value = "app/repair/changeOrder/checkHouseFlowApply")
    @ApiOperation(value = "申请退人工或业主验收检测", notes = "申请退人工或业主验收检测")
    ServerResponse checkHouseFlowApply(@RequestParam("userToken") String userToken,
                                @RequestParam("houseId") String houseId,
                                @RequestParam("type") Integer type,
                                @RequestParam("workerTypeId") String workerTypeId);
}
