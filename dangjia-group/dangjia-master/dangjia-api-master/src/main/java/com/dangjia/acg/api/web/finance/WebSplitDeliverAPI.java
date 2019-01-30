package com.dangjia.acg.api.web.finance;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:52
 */
@FeignClient("dangjia-service-master")
@Api(value = "供应商发货订单", description = "供应商发货订单")
public interface WebSplitDeliverAPI {

    /**
     * 所有供应商发货订单
     */
    @PostMapping("web/finance/splitDeliver/getAllSplitDeliver")
    @ApiOperation(value = "所有供应商发货订单", notes = "所有供应商发货订单")
    ServerResponse getAllSplitDeliver(@RequestParam("request") HttpServletRequest request, @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("applyState") Integer applyState, @RequestParam("beginDate") String beginDate,
                                      @RequestParam("endDate")  String endDate);

    /**
     * 修改供应商发货单信息
     */
    @PostMapping("web/finance/splitDeliver/setSplitDeliver")
    @ApiOperation(value = "修改供应商发货单信息", notes = "修改供应商发货单信息")
    ServerResponse setSplitDeliver(@RequestParam("request") HttpServletRequest request, @RequestParam("withdrawDeposit") SplitDeliver splitDeliver);

}
