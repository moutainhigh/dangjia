package com.dangjia.acg.common.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
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

    //支付成功回调接口
    public final static String notify_url = "master/app/pay/aliAsynchronous";

    public static ServerResponse getAlipaySign(String price,String out_trade_no,String basePath) {
        try {
            AlipayClient alipayClient = AlipayConfig.getAlipayClient();
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

            model.setOutTradeNo(out_trade_no);
            model.setTimeoutExpress("1.5h");
            model.setTotalAmount(price);
            model.setProductCode("QUICK_MSECURITY_PAY");
            request.setBizModel(model);
            request.setNotifyUrl(basePath + notify_url);
            model.setBody("蜂匠科技");
            model.setSubject("当家装修app");
            AlipayTradeAppPayResponse response =  alipayClient.sdkExecute(request);
            Map<String,String> map = new HashMap<String, String>();
            map.put("sign",response.getBody());
            return ServerResponse.createBySuccess("获取成功", map);
        }catch (AlipayApiException e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.ALI_PAY_ERROR, ServerCode.ALI_PAY_ERROR.getDesc());
        }
    }

    /**
     * web支付宝充值支付
     * @param price
     * @param out_trade_no
     * @param basePath
     * @return
     */
    public static ServerResponse getAlipaySignUrl(String price,String out_trade_no,String basePath) {
        try {
            AlipayClient alipayClient = AlipayConfig.getAlipayClient();
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setNotifyUrl(basePath + notify_url);
            //设置请求参数
            String bizContent = getBizContent(out_trade_no,price, "当家装修app","蜂匠科技");

            alipayRequest.setBizContent(bizContent);

            String codeUrl =  alipayClient.pageExecute(alipayRequest).getBody();
            return ServerResponse.createBySuccess("获取成功", codeUrl);
        }catch (AlipayApiException e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.ALI_PAY_ERROR, ServerCode.ALI_PAY_ERROR.getDesc());
        }
    }

    /**
     * 获取业务的关键内容
     *
     * @param out_trade_no 订单号
     * @param total_amount 付款金额
     * @param subject 订单名称
     * @param body 商品描述
     *
     * @return 拼接之后的json字符串
     */
    private static String getBizContent(String out_trade_no, String total_amount, String subject, String body) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"out_trade_no\":\"").append(out_trade_no).append("\",");
        sb.append("\"total_amount\":\"").append(total_amount).append("\",");
        sb.append("\"subject\":\"").append(subject).append("\",");
        sb.append("\"body\":\"").append(body).append("\",");
        sb.append("\"product_code\":\"QUICK_MSECURITY_PAY\"");
        sb.append("}");
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("============="+getAlipaySignUrl("123", "555565413854", "http://192.168.1.95/"));
    }
}
