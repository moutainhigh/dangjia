package com.dangjia.acg.common.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.pay.domain.AlipayConfig;
import com.dangjia.acg.common.response.ServerResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付工具类
 *
 * @author Ronalcheng
 */
public class AliPayUtil {
    public static ServerResponse getAlipaySign(String price,String out_trade_no) {
        try {
            AlipayClient alipayClient = AlipayConfig.getAlipayClient();
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

            model.setOutTradeNo(out_trade_no);
            model.setTimeoutExpress("1.5h");
            model.setTotalAmount(price);
            model.setProductCode("QUICK_MSECURITY_PAY");
            request.setBizModel(model);
            request.setNotifyUrl("https://test.fengjiangit.com:8081/dangjia/app/pay!aliAsynchronous.action");
            model.setBody("蜂匠科技");
            model.setSubject("当家装修app");
            AlipayTradeAppPayResponse response = (AlipayTradeAppPayResponse) alipayClient.sdkExecute(request);
            Map<String,String> map = new HashMap<String, String>();
            map.put("sign",response.getBody());
            return ServerResponse.createBySuccess("获取成功", map);
        }catch (AlipayApiException e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.ALI_PAY_ERROR, ServerCode.ALI_PAY_ERROR.getDesc());
        }
    }
}
