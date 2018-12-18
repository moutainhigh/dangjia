package com.dangjia.acg.api.web.deliver;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("dangjia-service-master")
@Api(value = "发货管理web接口", description = "发货管理web接口")
public interface WebOrderSplitAPI {


    @PostMapping("web/deliver/orderSplit/sentSplitDeliver")
    @ApiOperation(value = "供应商发货", notes = "供应商发货")
    ServerResponse sentSplitDeliver(@RequestParam("splitDeliverId")String splitDeliverId);

    @PostMapping("web/deliver/orderSplit/splitDeliverList")
    @ApiOperation(value = "供应商发货单列表", notes = "供应商发货单列表")
    ServerResponse splitDeliverList(@RequestParam("supplierId")String supplierId, @RequestParam("shipState")int shipState);

    @PostMapping("web/deliver/orderSplit/sentSupplier")
    @ApiOperation(value = "发送供应商", notes = "发送供应商")
    ServerResponse sentSupplier(@RequestParam("orderSplitId")String orderSplitId, @RequestParam("splitItemList")String splitItemList);

    @PostMapping("web/deliver/orderSplit/cancelOrderSplit")
    @ApiOperation(value = "取消打回", notes = "取消打回")
    ServerResponse cancelOrderSplit(@RequestParam("orderSplitId")String orderSplitId);

    @PostMapping("web/deliver/orderSplit/orderSplitItemList")
    @ApiOperation(value = "要货单看明细", notes = "要货单看明细")
    ServerResponse orderSplitItemList(@RequestParam("orderSplitId")String orderSplitId);

    @PostMapping("web/deliver/orderSplit/getHouseList")
    @ApiOperation(value = "材料员看房子列表", notes = "材料员看房子列表")
    ServerResponse getHouseList(@RequestParam("pageNum")Integer pageNum,@RequestParam("pageSize")Integer pageSize);

    @PostMapping("web/deliver/orderSplit/getOrderSplitList")
    @ApiOperation(value = "根据房子id查询要货单列表", notes = "根据房子id查询要货单列表")
    ServerResponse getOrderSplitList(@RequestParam("houseId")String houseId);
}
