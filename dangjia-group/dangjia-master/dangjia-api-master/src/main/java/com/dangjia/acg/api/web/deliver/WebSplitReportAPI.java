package com.dangjia.acg.api.web.deliver;

import com.dangjia.acg.common.model.PageDTO;
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

    /*根据供应商和房子查看要货订单*/
    @PostMapping("web/deliver/split/report/deliverOrder")
    @ApiOperation(value = "指定供应商所有的要货订单", notes = "指定供应商所有的要货订单")
    ServerResponse getSplitReportDeliverOrders(@RequestParam("houseId") String houseId,@RequestParam("supplierId") String supplierId);

    /*根据要货单号查询要货单明细*/
    @PostMapping("web/deliver/split/report/deliverOrderItems")
    @ApiOperation(value = "要货订单明细", notes = "要货订单明细")
    ServerResponse getSplitReportDeliverOrderItems(@RequestParam("number") String number);


    @PostMapping("web/deliver/split/report/goodsSuppliers")
    @ApiOperation(value = "要货订单供应商（商品维度）", notes = "要货订单供应商（商品维度）")
    ServerResponse getSplitReportGoodsSuppliers(String houseId,String productSn);

    @PostMapping("web/deliver/split/report/goods")
    @ApiOperation(value = "要货单商品列表统计（商品维度）", notes = "要货单商品列表统计（商品维度）")
    ServerResponse getSplitReportGoodsOrderItems(@RequestParam("pageDTO") PageDTO pageDTO, String houseId);

    /*供应商结算统计查看*/
    @PostMapping("web/deliver/split/report/House")
    @ApiOperation(value = "指定供应商所有发货的房子", notes = "指定供应商所有发货的房子")
    ServerResponse getSplitReportHouse(@RequestParam("supplierId") String supplierId);

}
