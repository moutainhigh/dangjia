package com.dangjia.acg.service.pay;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.pay.AliPayUtil;
import com.dangjia.acg.common.pay.WeiXinPayUtil;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.house.IHouseDistributionMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.modle.house.HouseDistribution;
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

    @Autowired
    private IHouseDistributionMapper iHouseDistributionMapper;
    /*
    微信签名
     */
    public ServerResponse getWeiXinSign(String userToken, String businessOrderNumber,int type){
        //检测订单有效性
        checkOrder( businessOrderNumber, type);
        //生成支付流水
        PayOrder payOrder = getPayOrder("1",userToken,businessOrderNumber,type);
        String price = payOrder.getPrice().toString();
        String outTradeNo = payOrder.getNumber();//支付订单号
        return WeiXinPayUtil.getWeiXinSign(price,outTradeNo);
    }

    /*
    支付宝签名
     */
    public ServerResponse getAliSign(String userToken, String businessOrderNumber,int type){
        //检测订单有效性
        checkOrder( businessOrderNumber, type);
        //生成支付流水
        PayOrder payOrder = getPayOrder("2",userToken,businessOrderNumber,type);
        String price = payOrder.getPrice().toString();
        String outTradeNo = payOrder.getNumber();//支付订单号
        return AliPayUtil.getAlipaySign(price, outTradeNo);
    }

    private void checkOrder(String businessOrderNumber,int type){
        if(type==1){
            HouseDistribution businessOrder = iHouseDistributionMapper.selectByPrimaryKey(businessOrderNumber);
            if (businessOrder==null){
                throw new BaseException(ServerCode.ERROR, "该订单不存在");
            }
            if (businessOrder.getState()==1){
                throw new BaseException(ServerCode.ERROR, "该订单不能重复支付");
            }
            if(businessOrder.getPrice().compareTo(0D) <= 0 ){
                throw new BaseException(ServerCode.ERROR, "金额错误");
            }
        }else {
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo("number", businessOrderNumber).andEqualTo("state", 1);
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            if (businessOrderList.size() == 0){
                throw new BaseException(ServerCode.ERROR, "该订单不存在");
            }
            BusinessOrder businessOrder = businessOrderList.get(0);
            if(businessOrder.getPayPrice().compareTo(new BigDecimal(0)) <= 0 ){
                throw new BaseException(ServerCode.ERROR, "金额错误");
            }
            if (businessOrder.getState()==3){
                throw new BaseException(ServerCode.ERROR, "该订单不能重复支付");
            }
        }
    }

    private PayOrder getPayOrder(String payState,String userToken, String businessOrderNumber,int type){
        PayOrder payOrder = new PayOrder();
        if(type==1){
            HouseDistribution businessOrder = iHouseDistributionMapper.selectByPrimaryKey(businessOrderNumber);
            payOrder.setBusinessOrderNumber(businessOrderNumber);
            payOrder.setPrice(new BigDecimal(businessOrder.getPrice()));//记录实付
            payOrder.setBusinessOrderType("3");
        }else {
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo("number", businessOrderNumber).andEqualTo("state", 1);
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            BusinessOrder businessOrder = businessOrderList.get(0);
            payOrder.setHouseId(businessOrder.getHouseId());
            payOrder.setBusinessOrderNumber(businessOrder.getNumber());
            payOrder.setPrice(businessOrder.getPayPrice());//记录实付
            if (businessOrder.getType() == 1 || businessOrder.getType() == 2){
                payOrder.setBusinessOrderType("1");
            }else{
                payOrder.setBusinessOrderType("2");
            }
        }
        payOrder.setNumber(System.currentTimeMillis()+"-"+generateWord());//生成支付订单号
        payOrder.setPayState(payState);//1微信，2支付宝
        payOrder.setState(0);// 0未支付,1已支付
        payOrderMapper.insert(payOrder);
        //临时支付金额
        payOrder.setPrice(new BigDecimal("0.01"));
        return payOrder;
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
