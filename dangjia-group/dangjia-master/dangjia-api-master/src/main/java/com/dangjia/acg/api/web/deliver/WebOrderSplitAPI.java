package com.dangjia.acg.api.web.deliver;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


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
    @ApiOperation(value = "发货任务--货单详情--清单", notes = "发货任务--货单详情--清单")
    ServerResponse splitDeliverDetail(@RequestParam("splitDeliverId") String splitDeliverId);

    @PostMapping("web/deliver/orderSplit/splitDeliverList")
    @ApiOperation(value = "供应商发货单列表", notes = "供应商发货单列表")
    ServerResponse splitDeliverList(@RequestParam("supplierId") String supplierId,
                                    @RequestParam("shipState") Integer shipState);

    @PostMapping("web/deliver/orderSplit/withdrawSupplier")
    @ApiOperation(value = "从供应商撤回发货单", notes = "从供应商撤回发货单")
    ServerResponse withdrawSupplier(@RequestParam("orderSplitId") String orderSplitId);

    /**
     * 发送给供应商，分发任务
     * @param orderSplitId 要货单ID
     * @param splitDeliverId 发货单ID(重新发货时为必填）
     * @param splitItemList [{id:”aa”,supplierId:”xx”},{id:”bb”,supplierId:”xx”}] 分发明细 id要货单明细ID，supplierId 供应商ID

     * @return
     */
    @PostMapping("web/deliver/orderSplit/sentSupplier")
    @ApiOperation(value = "发送供应商(分发任务)", notes = "发送供应商（分发任务）")
    ServerResponse sentSupplier(
            @RequestParam("orderSplitId") String orderSplitId,
            @RequestParam("splitDeliverId") String splitDeliverId,
            @RequestParam("splitItemList") String splitItemList);

    @PostMapping("web/deliver/orderSplit/saveSentSupplier")
    @ApiOperation(value = "分发供应商--生成发货单", notes = "分发供应商--生成发货单")
    ServerResponse saveSentSupplier(
            @RequestParam("orderSplitId") String orderSplitId,
            @RequestParam("splitDeliverId") String splitDeliverId,
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("installName") String installName,
            @RequestParam("installMobile") String installMobile,
            @RequestParam("deliveryName") String deliberyName,
            @RequestParam("deliveryMobile") String deliveryMobile);


    /**
     * 部分收货申诉接口
     * @param splitDeliverId 发货单ID
     * @param splitItemList 发货单明细列表
     * @param type 类型：1.认可部分收货，2申请平台申诉
     * @return
     */
    @PostMapping("web/deliver/orderSplit/platformComplaint")
    @ApiOperation(value = "部分收货申诉接口", notes = "部分收货申诉接口")
    ServerResponse platformComplaint(
            @RequestParam("splitDeliverId") String splitDeliverId,
            @RequestParam("splitItemList") String splitItemList,
            @RequestParam("type") Integer type);

    @PostMapping("web/deliver/orderSplit/cancelOrderSplit")
    @ApiOperation(value = "取消打回", notes = "取消打回")
    ServerResponse cancelOrderSplit(@RequestParam("orderSplitId") String orderSplitId);

    @PostMapping("web/deliver/orderSplit/cancelSplitDeliver")
    @ApiOperation(value = "撤回发货单", notes = "撤回发货单")
    ServerResponse cancelSplitDeliver(@RequestParam("splitDeliverId") String splitDeliverId);

    @PostMapping("web/deliver/orderSplit/orderSplitItemList")
    @ApiOperation(value = "货单列表--分发任务/重新发货列表", notes = "货单列表--分发任务/重新发货列表")
    ServerResponse orderSplitItemList(@RequestParam("orderSplitId") String orderSplitId,@RequestParam("splitDeliverId") String splitDeliverId);

    @PostMapping("web/deliver/orderSplit/getOrderSplitDeliverList")
    @ApiOperation(value = "货单列表--货单详情列表 ", notes = "货单列表--货单详情列表")
    ServerResponse getOrderSplitDeliverList(@RequestParam("orderSplitId") String orderSplitId);


    @PostMapping("web/deliver/orderSplit/getHouseList")
    @ApiOperation(value = "货单列表(列表)", notes = "货单列表（发货任务列表）")
    ServerResponse getHouseList(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("cityId") String cityId,
                                @RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("likeAddress") String likeAddress,
                                @RequestParam("startDate")String startDate,
                                @RequestParam("endDate")String endDate
    );

    @PostMapping("web/deliver/orderSplit/getOrderSplitList")
    @ApiOperation(value = "根据地址和店铺查询地应的货单列表", notes = "根据地址和店铺查询对应的货单列表")
    ServerResponse getOrderSplitList(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("userId")String userId,
                                     @RequestParam("cityId") String cityId,
                                     @RequestParam("pageDTO") PageDTO pageDTO,
                                     @RequestParam("addressId") String addressId,
                                     @RequestParam("houseId") String houseId,
                                     @RequestParam("storefrontId") String storefrontId);

    @PostMapping("web/deliver/orderSplit/setSplitDeliver")
    @ApiOperation(value = "修改 供应商结算状态", notes = "修改 供应商结算状态")
    ServerResponse setSplitDeliver(@RequestParam("splitDeliver") SplitDeliver splitDeliver);




}
