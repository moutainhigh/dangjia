package com.dangjia.acg.controller.app.pay;

import com.dangjia.acg.api.app.pay.PayAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.service.pay.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author: Ronalcheng
 * Date: 2019/3/4 0004
 * Time: 10:38
 */
@RestController
public class PayController implements PayAPI {
    @Autowired
    private PayService payService;

    @Override
    @ApiMethod
    public void aliAsynchronous(HttpServletRequest request, HttpServletResponse response,
                                String out_trade_no,String trade_status){
        payService.aliAsynchronous(response,out_trade_no,trade_status);
    }

    @Override
    @ApiMethod
    public void weixinAsynchronous(HttpServletRequest request, HttpServletResponse response){
        payService.weixinAsynchronous(request,response);
    }
}
