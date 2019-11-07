package com.dangjia.acg.api.app.deliver;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 10:55
 *
 */
@FeignClient("dangjia-service-master")
@Api(value = "订单要货操作", description = "订单要货操作")
public interface OrderAPI {

    @PostMapping("app/order/orderDetail")
    @ApiOperation(value = "子订单详情", notes = "子订单详情")
    ServerResponse orderDetail(@RequestParam("orderId") String orderId);

    @PostMapping("app/order/orderList")
    @ApiOperation(value = "订单详情", notes = "订单详情")
    ServerResponse orderList(@RequestParam("businessOrderId") String businessOrderId);

    @PostMapping("app/order/businessOrderList")
    @ApiOperation(value = "订单列表", notes = "订单列表")
    ServerResponse businessOrderList(@RequestParam("pageDTO") PageDTO pageDTO,
                                     @RequestParam("userToken") String userToken,
                                     @RequestParam("houseId") String houseId,
                                     @RequestParam("queryId") String queryId);


    @PostMapping("app/order/queryDeliverOrderListByStatus")
    @ApiOperation(value = "根据订单状态查询订单列表", notes = "根据订单状态查询订单列表")
    ServerResponse queryDeliverOrderListByStatus(@RequestParam("pageDTO") PageDTO pageDTO,
                                                  @RequestParam("userToken") String userToken,
                                                  @RequestParam("houseId") String houseId,
                                                  @RequestParam("cityId") String queryId,
                                                  @RequestParam("orderStatus") String orderStatus);

    @PostMapping("app/deliver/order/confirmOrderSplit")
    @ApiOperation(value = "管家确认要货", notes = "管家确认要货")
    ServerResponse confirmOrderSplit(@RequestParam("houseId") String houseId,
                                     @RequestParam("userToken") String userToken);

    @PostMapping("app/deliver/order/abrufbildungSubmitOrder")
    @ApiOperation(value = "补货提交订单接口", notes = "补货提交订单接口")
    ServerResponse abrufbildungSubmitOrder(@RequestParam("userToken") String userToken,
                                           @RequestParam("cityId") String cityId,
                                           @RequestParam("houseId") String houseId,
                                           @RequestParam("mendOrderId") String mendOrderId,
                                           @RequestParam("addressId") String addressId);

    @PostMapping("app/deliver/order/setOrderQuantity")
    @ApiOperation(value = "补货提交订单回调接口", notes = "补货提交订单回调接口")
    ServerResponse setOrderQuantity(@RequestParam("mendOrderId") String mendOrderId);

    @PostMapping("app/deliver/order/getOrderItemList")
    @ApiOperation(value = "已添加要货单明细", notes = "已添加要货单明细")
    ServerResponse getOrderItemList(@RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId);

    @PostMapping("app/deliver/order/saveOrderSplit")
    @ApiOperation(value = "提交到要货", notes = "提交到要货")
    ServerResponse saveOrderSplit(@RequestParam("productArr") String productArr,
                                  @RequestParam("houseId") String houseId,
                                  @RequestParam("userToken") String userToken);

    /**
     * 批量退款  调用正红的批量退款
     */

    /**
     *  --删除已经购物的订单
     */
    @PostMapping("app/order/delBusinessOrderById")
    @ApiOperation(value = "删除订单", notes = "删除订单")
    ServerResponse delBusinessOrderById(@RequestParam("userToken") String userToken, @RequestParam("orderId") String orderId);


}
