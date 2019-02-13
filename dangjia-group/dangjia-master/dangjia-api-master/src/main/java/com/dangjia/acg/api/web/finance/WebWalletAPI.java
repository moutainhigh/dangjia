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
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:52
 */
@FeignClient("dangjia-service-master")
@Api(value = "工人和业主支付订单流水", description = "工人和业主支付订单流水")
public interface WebWalletAPI {

    /**
     * 所有订单流水
     */
    @PostMapping("web/finance/wallet/getAllWallet")
    @ApiOperation(value = "工人和业主支付订单流水", notes = "工人和业主支付订单流水")
    ServerResponse getAllWallet(@RequestParam("request") HttpServletRequest request, @RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("workerId")String workerId, @RequestParam("houseId")String houseId);


}
