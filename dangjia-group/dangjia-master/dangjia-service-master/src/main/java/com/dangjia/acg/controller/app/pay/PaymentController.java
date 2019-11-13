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
    public ServerResponse getPaymentOrder(String userToken, String houseId, String taskId, Integer type) {
        return paymentService.getPaymentOrder(userToken, houseId, taskId, type);
    }

    /**
     * 支付页面(通用)
     */

    @Override
    @ApiMethod
    public ServerResponse getPaymentAllOrder(String userToken, String businessOrderNumber, Integer type) {
        if (type == null) {
            type = 0;
        }
        return paymentService.getPaymentAllOrder(userToken, businessOrderNumber, type);
    }

    @Override
    @ApiMethod
    public ServerResponse getPaymentPage(String userToken, String houseId, String taskId,String cityId, Integer type) {
        return paymentService.getPaymentPage(userToken, houseId, taskId,cityId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse generateOrder(String userToken,String cityId,String houseId, String workerId, String addressId,String productIds){
        return paymentService.generateOrder(userToken, cityId, houseId,workerId, addressId,productIds);
    }


    @Override
    @ApiMethod
    public ServerResponse queryInsuranceInfo(HttpServletRequest request,String userToken, String workerId) {
        return paymentService.queryInsuranceInfo(userToken,workerId);
    }



    @Override
    @ApiMethod
    public ServerResponse generateBudgetOrder(String userToken,String cityId,String houseFlowId, String addressId){
        return paymentService.generateBudgetOrder(userToken, cityId, houseFlowId,addressId);
    }

}
