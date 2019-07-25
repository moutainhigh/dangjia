package com.dangjia.acg.api.web.deliver;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.deliver.SplitDeliver;
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
    ServerResponse sentSplitDeliver(@RequestParam("splitDeliverId") String splitDeliverId);

    @PostMapping("web/deliver/orderSplit/rejectionSplitDeliver")
    @ApiOperation(value = "收货拒收", notes = "收货拒收")
    ServerResponse rejectionSplitDeliver(@RequestParam("splitDeliverId") String splitDeliverId);

    @PostMapping("web/deliver/orderSplit/splitDeliverDetail")
    @ApiOperation(value = "发货单明细", notes = "发货单明细")
    ServerResponse splitDeliverDetail(@RequestParam("splitDeliverId") String splitDeliverId);

    @PostMapping("web/deliver/orderSplit/splitDeliverList")
    @ApiOperation(value = "供应商发货单列表", notes = "供应商发货单列表")
    ServerResponse splitDeliverList(@RequestParam("supplierId") String supplierId,
                                    @RequestParam("shipState") int shipState);

    @PostMapping("web/deliver/orderSplit/withdrawSupplier")
    @ApiOperation(value = "从供应商撤回发货单", notes = "从供应商撤回发货单")
    ServerResponse withdrawSupplier(@RequestParam("orderSplitId") String orderSplitId);

    @PostMapping("web/deliver/orderSplit/sentSupplier")
    @ApiOperation(value = "发送供应商", notes = "发送供应商")
    ServerResponse sentSupplier(@RequestParam("orderSplitId") String orderSplitId,
                                @RequestParam("splitItemList") String splitItemList);

    @PostMapping("web/deliver/orderSplit/cancelOrderSplit")
    @ApiOperation(value = "取消打回", notes = "取消打回")
    ServerResponse cancelOrderSplit(@RequestParam("orderSplitId") String orderSplitId);

    @PostMapping("web/deliver/orderSplit/cancelSplitDeliver")
    @ApiOperation(value = "发货单取消打回(仅还原库存)", notes = "发货单取消打回")
    ServerResponse cancelSplitDeliver(@RequestParam("splitDeliverId") String splitDeliverId);

    @PostMapping("web/deliver/orderSplit/orderSplitItemList")
    @ApiOperation(value = "要货单看明细", notes = "要货单看明细")
    ServerResponse orderSplitItemList(@RequestParam("orderSplitId") String orderSplitId);

    @PostMapping("web/deliver/orderSplit/getHouseList")
    @ApiOperation(value = "材料员看房子列表", notes = "材料员看房子列表")
    ServerResponse getHouseList(@RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("likeAddress") String likeAddress,
                                @RequestParam("startDate")String startDate,
                                @RequestParam("endDate")String endDate
    );

    @PostMapping("web/deliver/orderSplit/getOrderSplitList")
    @ApiOperation(value = "根据房子id查询要货单列表", notes = "根据房子id查询要货单列表")
    ServerResponse getOrderSplitList(@RequestParam("houseId") String houseId);

    @PostMapping("web/deliver/orderSplit/setSplitDeliver")
    @ApiOperation(value = "修改 供应商结算状态", notes = "修改 供应商结算状态")
    ServerResponse setSplitDeliver(@RequestParam("splitDeliver") SplitDeliver splitDeliver);

}
