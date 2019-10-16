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
@Api(value = "支付订单流水", description = "支付订单流水")
public interface WebOrderAPI {

    @PostMapping("web/finance/order/getAllOrders")
    @ApiOperation(value = "支付订单流水(1:待付款2：支付中3：已支付4：已取消)", notes = "支付订单流水(1:待付款2：支付中3：已支付4：已取消)")
    ServerResponse getAllOrders(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("state") Integer state,
                                @RequestParam("searchKey") String searchKey);


    @PostMapping("web/finance/order/getOrderItem")
    @ApiOperation(value = "订单流水详情", notes = "订单流水详情")
    ServerResponse getOrderItem(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("businessNumber") String businessNumber);


    @PostMapping("web/finance/order/getOrderRedItem")
    @ApiOperation(value = "订单优惠券详情", notes = "订单优惠券详情")
    ServerResponse getOrderRedItem(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("businessId") String businessId);

    @PostMapping("job/auto/order/cancel")
    @ApiOperation(value = "自动取消订单（超过7天）", notes = "自动取消订单（超过7天）")
    void autoOrderCancel();
}
