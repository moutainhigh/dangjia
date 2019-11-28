package com.dangjia.acg.api.app.pay;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * author: Ronalcheng
 * Date: 2018/11/6 0006
 * Time: 10:41
 */
@FeignClient("dangjia-service-master")
@Api(value = "业主支付接口", description = "业主支付接口")
public interface PaymentAPI {

    @PostMapping("web/pay/payment/pos")
    @ApiOperation(value = "POS确认支付", notes = "POS确认支付")
    ServerResponse setServersSuccess(String businessOrderId, BigDecimal money, String image );

    @PostMapping("app/pay/payment/setPaying")
    @ApiOperation(value = "待付款提前付款", notes = "待付款提前付款")
    ServerResponse setPaying(@RequestParam("userToken") String userToken,
                             @RequestParam("houseId") String houseId);

    @PostMapping("app/pay/payment/setPaySuccess")
    @ApiOperation(value = "支付成功回调", notes = "支付成功回调")
    ServerResponse setPaySuccess(@RequestParam("userToken") String userToken,
                                 @RequestParam("businessOrderNumber") String businessOrderNumber);

    @PostMapping("web/pay/payment/setPaySuccess")
    @ApiOperation(value = "支付成功回调", notes = "支付成功回调")
    ServerResponse setWebPaySuccess(@RequestParam("businessOrderNumber") String businessOrderNumber);

    @PostMapping("app/pay/payment/getWeiXinSign")
    @ApiOperation(value = "获取微信签名信息", notes = "获取微信签名信息")
    ServerResponse getWeiXinSign(@RequestParam("userToken") String userToken,
                                 @RequestParam("businessOrderNumber") String businessOrderNumber,
                                 @RequestParam("userRole")Integer userRole);

    @PostMapping("app/pay/payment/getAliSign")
    @ApiOperation(value = "获取支付宝签名信息", notes = "获取支付宝签名信息")
    ServerResponse getAliSign(@RequestParam("userToken") String userToken,
                              @RequestParam("businessOrderNumber") String businessOrderNumber);

    @PostMapping("app/pay/payment/getOrder")
    @ApiOperation(value = "支付页面接口", notes = "支付页面接口")
    ServerResponse getPaymentOrder(@RequestParam("userToken") String userToken,
                                   @RequestParam("taskId") String taskId);


    @PostMapping("app/pay/payment/getPage")
    @ApiOperation(value = "接口", notes = "购物车接口")
    ServerResponse getPaymentPage(@RequestParam("userToken") String userToken,
                                  @RequestParam("taskId") String taskId,
                                  @RequestParam("cityId") String cityId,
                                  @RequestParam("houseId") String houseId,
                                  @RequestParam("productIds") String productIds,
                                  @RequestParam("type") Integer type);

    @PostMapping("app/order/generate/shop")
    @ApiOperation(value = "购物车提交订单接口", notes = "购物车提交订单接口")
    ServerResponse generateOrder(@RequestParam("userToken") String userToken,
                                 @RequestParam("cityId") String cityId,
                                 @RequestParam("productIds") String productIds,
                                 @RequestParam("workerId")String workerId,
                                 @RequestParam("addressId")String addressId);

    @PostMapping("app/order/edit")
    @ApiOperation(value = "订单更新接口", notes = "订单更新接口")
    ServerResponse editOrder(@RequestParam("userToken")String userToken,
                             @RequestParam("orderId")String orderId,
                             @RequestParam("workerId")String workerId,
                             @RequestParam("addressId")String addressId,
                             @RequestParam("houseId")String houseId);

    @PostMapping("app/order/generate/budget")
    @ApiOperation(value = "精算提交订单接口", notes = "精算提交订单接口")
    ServerResponse generateBudgetOrder(@RequestParam("userToken") String userToken,
                                       @RequestParam("cityId") String cityId,
                                       @RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("app/pay/payment/queryInsuranceInfo")
    @ApiOperation(value = "查询保险信息", notes = "查询保险信息")
    ServerResponse queryInsuranceInfo(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("userToken") String userToken,
                                      @RequestParam("workerId") String workerId);

}
