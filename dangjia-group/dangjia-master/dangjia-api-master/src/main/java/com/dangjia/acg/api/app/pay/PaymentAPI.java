package com.dangjia.acg.api.app.pay;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/11/6 0006
 * Time: 10:41
 */
@FeignClient("dangjia-service-master")
@Api(value = "业主支付接口", description = "业主支付接口")
public interface PaymentAPI {

    @PostMapping("app/pay/payment/setPaying")
    @ApiOperation(value = "待付款提前付款", notes = "待付款提前付款")
    ServerResponse setPaying(@RequestParam("userToken")String userToken,
                                 @RequestParam("houseId")String houseId);

    @PostMapping("app/pay/payment/setPaySuccess")
    @ApiOperation(value = "支付成功回调", notes = "支付成功回调")
    ServerResponse setPaySuccess(@RequestParam("userToken")String userToken,
                                 @RequestParam("businessOrderNumber")String businessOrderNumber);

    @PostMapping("app/pay/payment/getWeiXinSign")
    @ApiOperation(value = "获取微信签名信息", notes = "获取微信签名信息")
    ServerResponse getWeiXinSign(@RequestParam("userToken")String userToken,
                                 @RequestParam("businessOrderNumber")String businessOrderNumber);

    @PostMapping("app/pay/payment/getAliSign")
    @ApiOperation(value = "获取支付宝签名信息", notes = "获取支付宝签名信息")
    ServerResponse getAliSign(@RequestParam("userToken")String userToken,
                                 @RequestParam("businessOrderNumber")String businessOrderNumber);

    @PostMapping("app/pay/payment/getOrder")
    @ApiOperation(value = "支付页面接口", notes = "支付页面接口")
    ServerResponse getPaymentOrder(@RequestParam("userToken")String userToken, @RequestParam("houseId")String houseId,
                                  @RequestParam("taskId")String taskId,  @RequestParam("type")int type);

    @PostMapping("app/pay/payment/getPage")
    @ApiOperation(value = "购物车接口", notes = "购物车接口")
    ServerResponse getPaymentPage(@RequestParam("userToken")String userToken, @RequestParam("houseId")String houseId,
                                  @RequestParam("taskId")String taskId,  @RequestParam("type")int type);
}
