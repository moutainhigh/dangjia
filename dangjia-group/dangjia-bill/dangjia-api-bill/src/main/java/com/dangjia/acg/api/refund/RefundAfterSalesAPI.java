package com.dangjia.acg.api.refund;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/10/2019
 * Time: 下午 4:53
 */
@Api(description = "退款/售后")
@FeignClient("dangjia-service-bill")
public interface RefundAfterSalesAPI {

    @PostMapping("/refund/refundOrder/queryRefundOrderList")
    @ApiOperation(value = "查询需退款的订单", notes = "查询需退款的订单")
    ServerResponse queryRefundOrderList(@RequestParam("userToken") String userToken,
                                        @RequestParam("cityId") String cityId,
                                        @RequestParam("houseId") String houseId);


}
