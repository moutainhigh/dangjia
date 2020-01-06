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
 */
@FeignClient("dangjia-service-master")
@Api(value = "订单要货操作", description = "订单要货操作")
public interface OrderAPI {

    @PostMapping("app/order/orderDetail")
    @ApiOperation(value = "", notes = "子订单详情")
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

    @PostMapping("app/deliver/order/getOrderItemList")
    @ApiOperation(value = "已添加", notes = "已添加要货单明细")
    ServerResponse getOrderItemList(@RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId);

    @PostMapping("app/deliver/order/saveOrderSplit")
    @ApiOperation(value = "提交到要货", notes = "提交到要货")
    ServerResponse saveOrderSplit(@RequestParam("productArr") String productArr,
                                  @RequestParam("houseId") String houseId,
                                  @RequestParam("userToken") String userToken);


      /**
     * 删除已经购物的订单
     *
     * @param userToken
     * @param orderId
     * @return
     */
    @PostMapping("app/order/delBusinessOrderById")
    @ApiOperation(value = "删除订单", notes = "删除订单")
    ServerResponse delBusinessOrderById(@RequestParam("userToken") String userToken, @RequestParam("orderId") String orderId);


    /**
     * 删除已经购物的订单
     *
     * @param userToken
     * @param orderId
     * @return
     */
    @PostMapping("app/order/cancleBusinessOrderById")
    @ApiOperation(value = "取消订单", notes = "取消订单")
    ServerResponse cancleBusinessOrderById(@RequestParam("userToken") String userToken, @RequestParam("orderId") String orderId);

    @PostMapping("app/order/getDiffOrderById")
    @ApiOperation(value = "设计精算--补差价订单详情", notes = "设计精算--补差价订单详情")
    ServerResponse getDiffOrderById(@RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId);

    @PostMapping("app/order/getBudgetOrderById")
    @ApiOperation(value = "设计精算--原订单查询", notes = "设计精算--原订单查询")
    ServerResponse getBudgetOrderById(@RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId);


    /**
     * 设计、精算原订单退款
     * @param userToken 用户TOKEN
     * @param houseId 房子ID
     * @param orderId 订单ID
     * @param diffOrderId 补差价订单ID
     * @return
     */
    @PostMapping("app/order/refundBudgetOrderInfo")
    @ApiOperation(value = "设计精算--批量退款原订单，修改补差价订单", notes = "设计精算--批量退款原订单，修改补差价订单")
    ServerResponse refundBudgetOrderInfo(@RequestParam("userToken") String userToken,
                                         @RequestParam("houseId") String houseId,
                                         @RequestParam("orderId") String orderId,
                                         @RequestParam("diffOrderId") String diffOrderId);


    /**
     * 设计图纸不合格--审核设计图提交
     * @param userToken 用户token
     * @param houseId 房子ID
     * @param taskId 任务ID
     * @param type 类型：1当家平台设计，2平台外设计，3结束精算
     * @return
     */
    @PostMapping("app/order/saveDesignDrawingReview")
    @ApiOperation(value = "设计图纸不合格--审核设计图提交", notes = "设计图纸不合格--审核设计图提交")
    ServerResponse saveDesignDrawingReview(@RequestParam("userToken") String userToken,
                                         @RequestParam("houseId") String houseId,
                                         @RequestParam("taskId") String taskId,
                                         @RequestParam("type") Integer type);

}
