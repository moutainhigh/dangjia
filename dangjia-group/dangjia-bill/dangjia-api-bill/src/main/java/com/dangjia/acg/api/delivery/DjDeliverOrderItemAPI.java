package com.dangjia.acg.api.delivery;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Api(description = "订单明细表接口")
@FeignClient("dangjia-service-bill")
public interface DjDeliverOrderItemAPI {
    /**
     * 下单后--订单查询--订单详情展示
     */
    @PostMapping("/app/deliverOrder/queryAllDeliverOrderDetail")
    @ApiOperation(value = "下单后--订单查询--订单详情展示", notes = "下单后--订单查询--订单详情展示")
    ServerResponse queryAllDeliverOrderDetail(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("pageDTO") PageDTO pageDTO,
                                              @RequestParam("orderId") String orderId,
                                            @RequestParam("userId") String userId,
                                            @RequestParam("cityId") String cityId);
}
