package com.dangjia.acg.api.delivery;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Api(description = "所有订单表(装修所需的订单流水表)接口")
@FeignClient("dangjia-service-bill")
public interface DjDeliverOrderAPI {
    /**
     * OrderAPI
     * 下单后--订单查询--全部订单
     */
    @PostMapping("/app/deliverOrder/queryAllDeliverOrder")
    @ApiOperation(value = "下单后--订单查询--全部订单", notes = "下单后--订单查询--全部订单")
    ServerResponse queryAllDeliverOrder(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("pageDTO") PageDTO pageDTO,
                                       @RequestParam("userId") String userId,
                                       @RequestParam("cityId") String cityId);

    /**
     * 下单后--订单查询--订单详情展示
     */
    @PostMapping("/app/deliverOrder/queryAllDeliverOrderDetail")
    @ApiOperation(value = "下单后--订单查询--订单详情展示", notes = "下单后--订单查询--订单详情展示")
    ServerResponse queryAllDeliverOrderItem(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("pageDTO") PageDTO pageDTO,
                                       @RequestParam("userId") String userId,
                                       @RequestParam("cityId") String cityId);

    /**
     * 当前花费
     */
    @PostMapping("/app/deliverOrder/xx3")
    @ApiOperation(value = "下单后--当前花费", notes = "下单后--当前花费")
    ServerResponse xx3(@RequestParam("request") HttpServletRequest request,
                       @RequestParam("pageDTO") PageDTO pageDTO,
                       @RequestParam("userId") String userId,
                       @RequestParam("cityId") String cityId);

    /**
     * 录入价格
     */
    @PostMapping("/app/deliverOrder/xx2")
    @ApiOperation(value = "下单后--录入价格", notes = "下单后--录入价格")
    ServerResponse xx2(@RequestParam("request") HttpServletRequest request,
                      @RequestParam("pageDTO") PageDTO pageDTO,
                      @RequestParam("userId") String userId,
                      @RequestParam("cityId") String cityId);

    /**
     * 下单后--查看工匠详情
     */
    @PostMapping("/app/deliverOrder/xx")
    @ApiOperation(value = "下单后--查看工匠详情", notes = "下单后--查看工匠详情")
    ServerResponse xx(@RequestParam("request") HttpServletRequest request,
                                              @RequestParam("pageDTO") PageDTO pageDTO,
                                              @RequestParam("userId") String userId,
                                              @RequestParam("cityId") String cityId);

}
