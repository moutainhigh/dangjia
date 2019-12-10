package com.dangjia.acg.controller.app.pay;

import com.dangjia.acg.api.app.pay.PaymentAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.pay.PayService;
import com.dangjia.acg.service.pay.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 14:36
 */
@RestController
public class PaymentController implements PaymentAPI {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PayService payService;

    @Override
    @ApiMethod
    public ServerResponse setServersSuccess(String businessOrderId, BigDecimal money, String image ){
        return paymentService.setServersSuccess(businessOrderId, money,image);
    }

    @Override
    @ApiMethod
    public ServerResponse setPaying(String userToken, String houseId) {
        return paymentService.setPaying(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse setPaySuccess(String userToken, String businessOrderNumber) {
        return paymentService.setPaySuccess(userToken, businessOrderNumber);
    }

    @Override
    @ApiMethod
    public ServerResponse setWebPaySuccess(String businessOrderNumber) {
        return paymentService.setWebPaySuccess(businessOrderNumber);
    }


    @Override
    @ApiMethod
    public ServerResponse getWeiXinSign(String userToken, String businessOrderNumber, Integer userRole) {
        return payService.getWeiXinSign(businessOrderNumber, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse getAliSign(String userToken, String businessOrderNumber) {
        return payService.getAliSign(businessOrderNumber);
    }

    @Override
    @ApiMethod
    public ServerResponse getPaymentOrder(String userToken, String taskId) {
        return paymentService.getPaymentOrder(userToken,  taskId);
    }

    @Override
    @ApiMethod
    public ServerResponse getPaymentPage(String userToken, String taskId, String cityId, String houseId,String productIds, Integer type) {
        return paymentService.getPaymentPage(userToken,  taskId,cityId,  houseId, productIds,type);
    }

    @Override
    @ApiMethod
    public ServerResponse generateOrder(String userToken,String cityId,String productIds,String workerId, String addressId){
        return paymentService.generateOrder(userToken, cityId,productIds, workerId,  addressId,2);
    }

    @Override
    @ApiMethod
    public ServerResponse generateBudgetOrder(String userToken,String cityId,String houseFlowId){
        return paymentService.generateBudgetOrder(userToken, cityId, houseFlowId);
    }
    @Override
    @ApiMethod
    public ServerResponse editOrder(String userToken, String orderId, String workerId, String addressId, String houseId){
        return paymentService.editOrder(userToken,orderId,workerId,addressId,houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryInsuranceInfo(HttpServletRequest request,String userToken, String workerId) {
        return paymentService.queryInsuranceInfo(userToken,workerId);
    }



}
