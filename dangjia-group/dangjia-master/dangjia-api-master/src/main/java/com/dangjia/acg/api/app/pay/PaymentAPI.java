package com.dangjia.acg.api.app.pay;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("app/pay/payment/correct")
    @ApiOperation(value = "已付款，材料人工冲正", notes = "已付款，材料人工冲正")
    void budgetCorrect(String businessOrderNumber,  String payState, String houseFlowId);

    @PostMapping("app/pay/payment/setPaying")
    @ApiOperation(value = "待付款提前付款", notes = "待付款提前付款")
    ServerResponse setPaying(@RequestParam("userToken") String userToken,
                             @RequestParam("houseId") String houseId);

    @PostMapping("app/pay/payment/setPaySuccess")
    @ApiOperation(value = "支付成功回调", notes = "支付成功回调")
    ServerResponse setPaySuccess(@RequestParam("userToken") String userToken,
                                 @RequestParam("businessOrderNumber") String businessOrderNumber);

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
                                   @RequestParam("houseId") String houseId,
                                   @RequestParam("taskId") String taskId,
                                   @RequestParam("type") Integer type);

    @PostMapping("app/pay/payment/order")
    @ApiOperation(value = "支付页面接口(通用)", notes = "支付页面接口(通用)")
    ServerResponse getPaymentAllOrder(@RequestParam("userToken") String userToken,
                                      @RequestParam("businessOrderNumber") String businessOrderNumber,
                                      @RequestParam("type") Integer type);

    @PostMapping("app/pay/payment/getPage")
    @ApiOperation(value = "接口", notes = "购物车接口")
    ServerResponse getPaymentPage(@RequestParam("userToken") String userToken,
                                  @RequestParam("houseId") String houseId,
                                  @RequestParam("taskId") String taskId,
                                  @RequestParam("cityId") String cityId,
                                  @RequestParam("type") Integer type);

    @PostMapping("app/order/generate/shop")
    @ApiOperation(value = "购物车提交订单接口", notes = "购物车提交订单接口")
    ServerResponse generateOrder(@RequestParam("userToken") String userToken,
                                 @RequestParam("cityId") String cityId,
                                 @RequestParam("houseId") String houseId,
                                 @RequestParam("workerId") String workerId,
                                 @RequestParam("addressId") String addressId);


    @PostMapping("app/order/generate/budget")
    @ApiOperation(value = "精算提交订单接口", notes = "精算提交订单接口")
    ServerResponse generateBudgetOrder(@RequestParam("userToken") String userToken,
                                       @RequestParam("cityId") String cityId,
                                       @RequestParam("houseFlowId") String houseFlowId,
                                       @RequestParam("addressId") String addressId);
}
