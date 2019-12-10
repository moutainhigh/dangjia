package com.dangjia.acg.api.delivery;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Api(description = "订单明细表接口")
@FeignClient("dangjia-service-bill")
public interface DjDeliverOrderItemAPI {

    @PostMapping("app/order/queryPaymentToBeMade")
    @ApiOperation(value = "待付款/已取消详情", notes = "待付款/已取消详情")
    ServerResponse queryPaymentToBeMade(@RequestParam("orderId") String orderId);

    @PostMapping("app/order/queryHumpDetail")
    @ApiOperation(value = "待发货详情", notes = "待发货详情")
    ServerResponse queryHumpDetail(@RequestParam("orderId") String orderId);

    @PostMapping("app/order/deleteOrder")
    @ApiOperation(value = "删除订单", notes = "删除订单")
    ServerResponse deleteOrder(@RequestParam("orderId") String orderId);

    @PostMapping("app/order/queryOrderSnapshot")
    @ApiOperation(value = "订单快照", notes = "订单快照")
    ServerResponse queryOrderSnapshot(@RequestParam("orderId") String orderId);

    @PostMapping("app/order/setCancellationOrder")
    @ApiOperation(value = "取消订单", notes = "取消订单")
    ServerResponse setCancellationOrder(@RequestParam("orderId") String orderId);

    @PostMapping("app/order/setAcceptanceEvaluation")
    @ApiOperation(value = "验收评价", notes = "验收评价")
    ServerResponse setAcceptanceEvaluation(@RequestParam("userToken")String userToken,
                                           @RequestParam("jsonStr") String jsonStr,
                                           @RequestParam("splitDeliverId") String splitDeliverId);
}

