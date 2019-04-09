package com.dangjia.acg.api.web.deliver;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("dangjia-service-master")
@Api(value = "发货统计管理web接口", description = "发货统计管理web接口")
public interface WebSplitReportAPI {


    @PostMapping("web/deliver/split/report/suppliers")
    @ApiOperation(value = "指定房子所有发货的供应商", notes = "指定房子所有发货的供应商")
    ServerResponse getSplitReportSuppliers(@RequestParam("houseId") String houseId);

    @PostMapping("web/deliver/split/report/deliverOrder")
    @ApiOperation(value = "指定供应商所有的要货订单", notes = "指定供应商所有的要货订单")
    ServerResponse getSplitReportDeliverOrders(@RequestParam("supplierId") String supplierId);

    @PostMapping("web/deliver/split/report/deliverOrderItems")
    @ApiOperation(value = "要货订单明细", notes = "要货订单明细")
    ServerResponse getSplitReportDeliverOrderItems(@RequestParam("number") String number);



}
