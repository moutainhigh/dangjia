package com.dangjia.acg.service.pay;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.pay.AliPayUtil;
import com.dangjia.acg.common.pay.WeiXinPayUtil;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.house.IHouseDistributionMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.modle.house.HouseDistribution;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 17:58
 */
@Service
public class PayService {
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IBusinessOrderMapper businessOrderMapper;
    @Autowired
    private IPayOrderMapper payOrderMapper;
    @Autowired
    private IHouseDistributionMapper iHouseDistributionMapper;
    @Autowired
    private PaymentService paymentService;

    protected static final Logger LOG = LoggerFactory.getLogger(PayService.class);

    /**
     * 支付宝支付异步通知回调
     */
    public void aliAsynchronous(HttpServletResponse response,String out_trade_no,String trade_status){
        try{
            LOG.info("获取支付宝异步通知支付订单号" + out_trade_no);
            LOG.info("交易状态" + trade_status);
            //total_amount 单位元

            //交易成功
            if(trade_status.equals("TRADE_SUCCESS")){
                // 成功或app已回调
                if (this.asynchronousInform(out_trade_no)) {
                    response.setContentType("text/html;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("success");//返回成功关闭支付宝异步通知
                }else{
                    LOG.info("支付宝回调异常,赶快看看");
                }
            }else{
                LOG.info("支付宝异步通知支付失败");
            }
        }catch (Exception e){
            LOG.error("获取支付宝报文失败" + e.getMessage());
        }
    }

    /**
     * 微信支付异步通知回调
     */
    public void weixinAsynchronous(HttpServletRequest request,HttpServletResponse response){
        // 获取微信POST过来反馈信息
        String inputLine;
        String notityXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notityXml += inputLine;
            }
            request.getReader().close();
            Map m = parseXmlToList2(notityXml);
            String out_trade_no = m.get("out_trade_no").toString();
            String cash_fee = m.get("cash_fee").toString();
            String result_code = m.get("result_code").toString();
            LOG.info("接收的支付订单号：" + out_trade_no);
            LOG.info("接收的金额：" + cash_fee);//单位分
            LOG.info("业务结果" + result_code);

            // 成功或app已回调
            if(result_code.equals("SUCCESS")){
                if (this.asynchronousInform(out_trade_no)) { //前端或服务器回调成功
                    response.setContentType("text/html;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");// 返回的字符串数据
                }else{
                    LOG.info("微信回调异常,赶快看看");
                }
            }else{
                LOG.info("微信异步通知支付失败");
            }
        } catch (Exception e) {
            LOG.error("获取微信异步报文失败" + e.getMessage());
        }
    }

    private boolean asynchronousInform(String payOrderNumber){
        PayOrder payOrder = payOrderMapper.getByNumber(payOrderNumber);
        if (payOrder == null){
            LOG.info("支付订单不存在");
            return false;
        }
        if (payOrder.getState() == 2){
            LOG.info("支付订单已支付");
            return true;
        }
        if (payOrder.getState() == 0){//未支付
            ServerResponse serverResponse = paymentService.setServersSuccess(payOrder.getId());
            if(serverResponse.isSuccess()){//回调成功
                return true;
            }else {
                LOG.info("回调失败!!");
                return false;
            }
        }
        return false;
    }

    private Map parseXmlToList2(String xml) {
        Map retMap = new HashMap();
        try {
            StringReader read = new StringReader(xml);
            // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
            InputSource source = new InputSource(read);
            // 创建一个新的SAXBuilder
            SAXBuilder sb = new SAXBuilder();
            // 通过输入源构造一个Document
            Document doc = (Document) sb.build(source);
            Element root = doc.getRootElement();// 指向根节点
            List<Element> es = root.getChildren();
            if (es != null && es.size() != 0) {
                for (Element element : es) {
                    retMap.put(element.getName(), element.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

    /*
    微信签名
     */
    public ServerResponse getWeiXinSign(String businessOrderNumber){
        //API路径
        String basePath = configUtil.getValue(SysConfig.DANGJIA_API_LOCAL, String.class);
        //检测订单有效性
        checkOrder(businessOrderNumber);
        //生成支付流水
        PayOrder payOrder = getPayOrder("1",businessOrderNumber);
        String price = payOrder.getPrice().toString();
        String outTradeNo = payOrder.getNumber();//支付订单号
        return WeiXinPayUtil.getWeiXinSign(price,outTradeNo,basePath);
    }

    /*
    支付宝签名
     */
    public ServerResponse getAliSign(String businessOrderNumber){
        //API路径
        String basePath = configUtil.getValue(SysConfig.DANGJIA_API_LOCAL, String.class);
        //检测订单有效性
        checkOrder(businessOrderNumber);
        //生成支付流水
        PayOrder payOrder = getPayOrder("2",businessOrderNumber);
        String price = payOrder.getPrice().toString();
        String outTradeNo = payOrder.getNumber();//支付订单号
        return AliPayUtil.getAlipaySign(price,outTradeNo,basePath);
    }

    private void checkOrder(String businessOrderNumber){
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
        if(businessOrder.getType() == 5){
            HouseDistribution houseDistribution = iHouseDistributionMapper.selectByPrimaryKey(businessOrder.getTaskId());
            if (houseDistribution==null){
                throw new BaseException(ServerCode.ERROR, "该订单不存在");
            }
            if (houseDistribution.getState()==1){
                throw new BaseException(ServerCode.ERROR, "该订单不能重复支付");
            }
            if(houseDistribution.getPrice().compareTo(0D) <= 0 ){
                throw new BaseException(ServerCode.ERROR, "金额错误");
            }
        }

    }

    private PayOrder getPayOrder(String payState,String businessOrderNumber){
        PayOrder payOrder = new PayOrder();

        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo("number", businessOrderNumber).andEqualTo("state", 1);
        List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
        BusinessOrder businessOrder = businessOrderList.get(0);
        payOrder.setBusinessOrderNumber(businessOrderNumber);
        //payOrder.setBusinessOrderType(businessOrder.getType());  此字段弃用
        payOrder.setPrice(businessOrder.getPayPrice());//记录实付
        payOrder.setHouseId(businessOrder.getHouseId());
        payOrder.setNumber(System.currentTimeMillis()+"-"+generateWord());//生成支付订单号
        payOrder.setPayState(payState);//1微信，2支付宝
        payOrder.setState(0);// 0未支付,1已支付
        payOrderMapper.insert(payOrder);

        /****临时支付金额****/
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
