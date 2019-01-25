package com.dangjia.acg.controller.app.pay;

import com.dangjia.acg.api.app.pay.PaymentAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.pay.PayService;
import com.dangjia.acg.service.pay.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

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
    public ServerResponse setPaying(String userToken,String houseId){
        return paymentService.setPaying(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse setPaySuccess(String userToken,String businessOrderNumber,Integer type){
        if(type==null){ type=0;}
        return paymentService.setPaySuccess(userToken,businessOrderNumber, type);
    }
    @Override
    @ApiMethod
    public ServerResponse getWeiXinSign(String userToken, String businessOrderNumber,Integer type){
        if(type==null){ type=0;}
        return payService.getWeiXinSign(userToken,businessOrderNumber, type);
    }
    @Override
    @ApiMethod
    public ServerResponse getAliSign(String userToken, String businessOrderNumber,Integer type){
        if(type==null){ type=0;}
        return payService.getAliSign(userToken,businessOrderNumber, type);
    }
    @Override
    @ApiMethod
    public ServerResponse getPaymentOrder(String userToken, String houseId, String taskId,int type){
        return paymentService.getPaymentOrder(userToken,houseId,taskId,type);
    }
    /**
     * 支付页面(通用)
     */

    @Override
    @ApiMethod
    public ServerResponse getPaymentAllOrder(String userToken, String businessOrderNumber,Integer type){
        if(type==null){ type=0;}
        return paymentService.getPaymentAllOrder(userToken,businessOrderNumber,type);
    }
    @Override
    @ApiMethod
    public ServerResponse getPaymentPage(String userToken, String houseId, String taskId,int type){
        return paymentService.getPaymentPage(userToken,houseId,taskId,type);
    }
}
