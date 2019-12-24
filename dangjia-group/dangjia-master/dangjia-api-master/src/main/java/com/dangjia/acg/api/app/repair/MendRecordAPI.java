package com.dangjia.acg.api.app.repair;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/12/24 0024
 * Time: 14:00
 */
@FeignClient("dangjia-service-master")
@Api(value = "要补退记录", description = "要补退记录")
public interface MendRecordAPI {

    @PostMapping(value = "app/repair/mendRecord/mendOrderDetail")
    @ApiOperation(value = "审/要/补/退明细", notes = "审/要/补/退明细")
    ServerResponse mendOrderDetail(@RequestParam("userToken") String userToken,
                                   @RequestParam("mendOrderId") String mendOrderId,
                                   @RequestParam("type") Integer type);

    @PostMapping(value = "app/repair/mendRecord/mendDeliverDetail")
    @ApiOperation(value = "供应商退明细", notes = "供应商退明细")
    ServerResponse mendDeliverDetail(@RequestParam("userToken") String userToken,
                                     @RequestParam("mendDeliverId") String mendDeliverId);

    @PostMapping(value = "app/repair/mendRecord/recordList")
    @ApiOperation(value = "记录列表", notes = "记录列表")
    ServerResponse recordList(@RequestParam("userToken") String userToken,
                              @RequestParam("roleType") Integer roleType,
                              @RequestParam("houseId") String houseId,
                              @RequestParam("queryId") String queryId,
                              @RequestParam("type") Integer type);

    @PostMapping(value = "app/repair/mendRecord/mendList")
    @ApiOperation(value = "要补退记录", notes = "要补退记录")
    ServerResponse mendList(@RequestParam("userToken") String userToken,
                            @RequestParam("houseId") String houseId,
                            @RequestParam("roleType") Integer roleType);

    @PostMapping(value = "app/repair/mendRecord/backOrder")
    @ApiOperation(value = "撤回补货要货订单", notes = "撤回补货要货订单")
    ServerResponse backOrder(@RequestParam("mendOrderId") String mendOrderId, @RequestParam("type") Integer type);
}
