package com.dangjia.acg.api.web.finance;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 10:55
 */
@FeignClient("dangjia-service-master")
@Api(value = "所有订单流水", description = "所有订单流水")
public interface WebOrderAPI {

    @PostMapping("web/finance/order/getAllOrders")
    @ApiOperation(value = "所有订单流水", notes = "所有订单流水")
    ServerResponse getAllOrders(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("likeMobile") String likeMobile,
                                @RequestParam("likeAddress") String likeAddress);

//    @PostMapping("web/finance/order/orderList")
//    @ApiOperation(value = "订单详情", notes = "订单详情")
//    ServerResponse orderList(@RequestParam("businessOrderId") String businessOrderId);

}
