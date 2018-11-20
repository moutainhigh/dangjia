package com.dangjia.acg.service.pay;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.pay.AliPayUtil;
import com.dangjia.acg.common.pay.WeiXinPayUtil;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 17:58
 */
@Service
public class PayService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IBusinessOrderMapper businessOrderMapper;
    @Autowired
    private IPayOrderMapper payOrderMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;

    /*
    微信签名
     */
    public ServerResponse getWeiXinSign(String userToken, String businessOrderNumber){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        if(accessToken == null){//无效的token
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"无效的token,请重新登录或注册!");
        }
        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo("number", businessOrderNumber).andEqualTo("state", 1);
        List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
        if (businessOrderList.size() == 0){
            return ServerResponse.createByErrorMessage("该订单不存在");
        }
        BusinessOrder businessOrder = businessOrderList.get(0);
        if(businessOrder.getPayPrice().compareTo(new BigDecimal(0)) <= 0 ){
            return ServerResponse.createByErrorMessage("金额错误");
        }

        PayOrder payOrder = new PayOrder();
        payOrder.setHouseId(businessOrder.getHouseId());
        payOrder.setNumber(System.currentTimeMillis()+"-"+generateWord());//生成支付订单号
        payOrder.setBusinessOrderNumber(businessOrder.getNumber());
        payOrder.setPayState("1");//1微信，2支付宝
        payOrder.setState(0);// 0未支付,1已支付
        payOrder.setPrice(businessOrder.getPayPrice());//记录实付
        payOrderMapper.insert(payOrder);
        //String price = businessOrder.getPayPrice().toString();
        String price = "0.01";
        String outTradeNo = payOrder.getNumber();//支付订单号
        return WeiXinPayUtil.getWeiXinSign(price,outTradeNo);
    }

    /*
    支付宝签名
     */
    public ServerResponse getAliSign(String userToken, String businessOrderNumber){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        if(accessToken == null){//无效的token
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"无效的token,请重新登录或注册!");
        }
        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo("number", businessOrderNumber).andEqualTo("state", 1);
        List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
        if (businessOrderList.size() == 0){
            return ServerResponse.createByErrorMessage("该订单不存在");
        }
        BusinessOrder businessOrder = businessOrderList.get(0);
        if(businessOrder.getPayPrice().compareTo(new BigDecimal(0)) <= 0 ){
            return ServerResponse.createByErrorMessage("金额错误");
        }

        PayOrder payOrder = new PayOrder();
        payOrder.setHouseId(businessOrder.getHouseId());
        payOrder.setNumber(System.currentTimeMillis()+"-"+generateWord());//生成支付订单号
        payOrder.setBusinessOrderNumber(businessOrder.getNumber());
        payOrder.setPayState("2");//1微信，2支付宝
        payOrder.setState(0);// 0未支付,1已支付
        payOrder.setPrice(businessOrder.getPayPrice());//记录实付
        payOrderMapper.insert(payOrder);
        //String price = businessOrder.getPayPrice().toString();
        String price = "0.01";
        String outTradeNo = payOrder.getNumber();//支付订单号
        return AliPayUtil.getAlipaySign(price, outTradeNo);
    }

    private String generateWord() {
        String[] beforeShuffle = new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z" };
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(5, 9);
        return result;
    }
}
