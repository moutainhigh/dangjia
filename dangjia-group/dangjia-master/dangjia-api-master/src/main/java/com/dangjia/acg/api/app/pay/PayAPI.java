package com.dangjia.acg.api.app.pay;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author: Ronalcheng
 * Date: 2019/3/4 0004
 * Time: 10:36
 */
@FeignClient("dangjia-service-master")
@Api(value = "服务端回调", description = "服务端回调")
public interface PayAPI {


    @PostMapping("app/pay/aliAsynchronous")
    @ApiOperation(value = "支付宝异步回调", notes = "支付宝异步回调")
    void aliAsynchronous(@RequestParam("request")HttpServletRequest request,
                            @RequestParam("response") HttpServletResponse response,
                         @RequestParam("out_trade_no") String out_trade_no,
                         @RequestParam("trade_status") String trade_status);

    @PostMapping("app/pay/weixinAsynchronous")
    @ApiOperation(value = "微信异步回调", notes = "微信异步回调")
    void weixinAsynchronous(@RequestParam("request")HttpServletRequest request,
                            @RequestParam("response") HttpServletResponse response);
}
