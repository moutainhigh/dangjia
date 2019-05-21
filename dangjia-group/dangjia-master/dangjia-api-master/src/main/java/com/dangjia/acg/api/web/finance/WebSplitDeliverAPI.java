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
    @ApiOperation(value = "所有供应商", notes = "所有供应商")
    ServerResponse getAllSplitDeliver(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("applyState") Integer applyState,
                                      @RequestParam("searchKey") String searchKey,
                                      @RequestParam("beginDate") String beginDate,
                                      @RequestParam("endDate") String endDate);

    /**
     * 修改供应商发货单信息
     */
    @PostMapping("web/finance/splitDeliver/setSplitDeliver")
    @ApiOperation(value = "修改供应商发货单信息", notes = "修改供应商发货单信息")
    ServerResponse setSplitDeliver(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("withdrawDeposit") SplitDeliver splitDeliver);


    @PostMapping("web/finance/splitDeliver/getOrderSplitList")
    @ApiOperation(value = "根据供应商Id查看要货单列表", notes = "根据供应商Id查看要货单列表")
    ServerResponse getOrderSplitList(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("supplierId") String supplierId);

    /**
     * 收货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消,4部分收
     */
    @PostMapping("web/finance/splitDeliver/splitDeliverList")
    @ApiOperation(value = "供应商查看货单详情", notes = "供应商查看货单详情")
    ServerResponse splitDeliverList(@RequestParam("splitDeliverId") String splitDeliverId);

}
