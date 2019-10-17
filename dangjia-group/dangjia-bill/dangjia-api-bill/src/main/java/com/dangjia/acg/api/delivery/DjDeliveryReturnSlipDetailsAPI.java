package com.dangjia.acg.api.delivery;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 下午 3:53
 */
@Api(description = "发货退货单详情接口")
@FeignClient("dangjia-service-bill")
public interface DjDeliveryReturnSlipDetailsAPI {

    @PostMapping("/delivery/djDeliveryReturnSlip/queryTaskDetails")
    @ApiOperation(value = "任务详情", notes = "任务详情")
    ServerResponse queryTaskDetails(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("splitId") String splitId,
                                    @RequestParam("invoiceType") Integer invoiceType);


}
