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





    @PostMapping("/app/deliverOrder/queryOrderNumber")
    @ApiOperation(value = "查询我要装修首页", notes = "查询我要装修首页")
    ServerResponse queryOrderNumber(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId);


    @PostMapping("app/design/getDesignImag")
    @ApiOperation(value = "获取设计图", notes = "获取设计图")
    ServerResponse getDesignImag(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("houseId") String houseId);

    @PostMapping("app/design/getDesignInfo")
    @ApiOperation(value = "获取设计验收过程", notes = "获取设计验收过程")
    ServerResponse getDesignInfo(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("houseId") String houseId);

    @PostMapping("app/design/getActuaryInfo")
    @ApiOperation(value = "获取精算信息", notes = "获取精算信息")
    ServerResponse getActuaryInfo(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("houseId") String houseId);

    @PostMapping("app/design/getCollectInfo")
    @ApiOperation(value = "查询验收过程", notes = "查询验收过程")
    ServerResponse getCollectInfo(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("houseId") String houseId);


    @PostMapping("app/order/queryDeliverOrderListByStatus")
    @ApiOperation(value = "根据订单状态查询订单详情列表", notes = "根据订单状态查询订单详情")
    ServerResponse queryDeliverOrderListByStatus(@RequestParam("pageDTO") PageDTO pageDTO,
                                                 @RequestParam("userToken") String userToken,
                                                 @RequestParam("houseId") String houseId,
                                                 @RequestParam("cityId") String queryId,
                                                 @RequestParam("orderStatus") String orderStatus);

    @PostMapping("app/deliverOrderItem/deliverOrderItemDetail")
    @ApiOperation(value = "新版订单详情明细", notes = "新版订单详情明细")
    ServerResponse deliverOrderItemDetail(@RequestParam("orderId") String orderId,@RequestParam("orderStatus")  String  orderStatus,@RequestParam("userToken")  String  userToken  );


}
