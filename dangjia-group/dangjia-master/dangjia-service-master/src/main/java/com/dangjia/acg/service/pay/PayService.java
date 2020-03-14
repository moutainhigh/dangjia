package com.dangjia.acg.service.pay;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.pay.AliPayUtil;
import com.dangjia.acg.common.pay.WeiXinPayUtil;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseDistributionMapper;
import com.dangjia.acg.mapper.member.IMemberAuthMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IMasterSupplierPayOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.modle.house.HouseDistribution;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAuth;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import com.dangjia.acg.modle.supplier.DjSupplierPayOrder;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.Utils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private IMasterSupplierPayOrderMapper masterSupplierPayOrderMapper;

    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IMemberAuthMapper memberAuthMapper;
    @Autowired
    private IPayOrderMapper payOrderMapper;
    @Autowired
    private IHouseDistributionMapper iHouseDistributionMapper;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;

    @Value("${spring.profiles.active}")
    private String active;
    protected static final Logger LOG = LoggerFactory.getLogger(PayService.class);

    /**
     * 支付宝支付异步通知回调
     */
    public void aliAsynchronous(HttpServletResponse response, String out_trade_no, String trade_status) {
        try {
            LOG.info("获取支付宝异步通知支付订单号" + out_trade_no);
            LOG.info("交易状态" + trade_status);
            //total_amount 单位元
            PrintWriter out = response.getWriter();
            //交易成功
            if (trade_status.equals("TRADE_SUCCESS")) {
                // 成功或app已回调
                if (this.asynchronousInform(out_trade_no)) {
                    response.setContentType("text/html;charset=UTF-8");
                    out.println("success");//返回成功关闭支付宝异步通知
                } else {
                    out.println("fail");//返回成功关闭支付宝异步通知
                    LOG.info("支付宝回调异常,赶快看看");
                }
            } else {
                out.println("fail");//返回成功关闭支付宝异步通知
                LOG.info("支付宝异步通知支付失败");
            }
        } catch (Exception e) {
            LOG.error("获取支付宝报文失败" + e.getMessage());
        }
    }

    /**
     * 微信支付异步通知回调
     */
    public void weixinAsynchronous(HttpServletRequest request, HttpServletResponse response) {
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
            if (result_code.equals("SUCCESS")) {
                if (this.asynchronousInform(out_trade_no)) { //前端或服务器回调成功
                    response.setContentType("text/html;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");// 返回的字符串数据
                } else {
                    LOG.info("微信回调异常,赶快看看");
                }
            } else {
                LOG.info("微信异步通知支付失败");
            }
        } catch (Exception e) {
            LOG.error("获取微信异步报文失败" + e.getMessage());
        }
    }

    private boolean asynchronousInform(String payOrderNumber) {
        PayOrder payOrder = payOrderMapper.getByNumber(payOrderNumber);
        if (payOrder == null) {
            LOG.info("支付订单不存在");
            return false;
        }
        if (payOrder.getState() == 2) {
            LOG.info("支付订单已支付");
            return true;
        }
        if (payOrder.getState() == 0) {//未支付
            ServerResponse serverResponse = paymentService.setServersSuccess(payOrder.getId());
            if (serverResponse.isSuccess()) {//回调成功
                return true;
            } else {
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
    获取支付地址
     */
    public ServerResponse getPayURL(String businessOrderNumber) {
        //API路径
        String basePath = configUtil.getValue(SysConfig.DANGJIA_API_LOCAL, String.class);
        LOG.info(basePath + "getWeiXinSignURL**********************************************");
        String payState="1";
        try {
        //检测订单有效性
            payState=checkOrder(businessOrderNumber);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage(e.getMessage());
        }
        //生成支付流水
        PayOrder payOrder = getPayOrder("4", businessOrderNumber);
        String price = payOrder.getPrice().toString();
        String outTradeNo = payOrder.getNumber();//支付订单号
        if("1".equals(payState)){
            return WeiXinPayUtil.getWeiXinSignURL(price, outTradeNo, basePath, null);
        }else{
            return AliPayUtil.getAlipaySignUrl(price, outTradeNo, basePath);
        }

    }
    /*
   微信签名
    */
    public ServerResponse getWeiXinSign(String userToken,String businessOrderNumber, String openId,Integer userRole) {
        //API路径
        String basePath = configUtil.getValue(SysConfig.DANGJIA_API_LOCAL, String.class);
        LOG.info(basePath + "getWeiXinSign**********************************************");

        try {
            //检测订单有效性
            checkOrder(businessOrderNumber);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage(e.getMessage());
        }
        //生成支付流水
        PayOrder payOrder = getPayOrder("1", businessOrderNumber);
        String price = payOrder.getPrice().toString();
        String outTradeNo = payOrder.getNumber();//支付订单号
        if(userRole==null){
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            Example example = new Example(MemberAuth.class);
            example.createCriteria()
                    .andEqualTo(MemberAuth.OPEN_TYPE, 1)
                    .andEqualTo(MemberAuth.MEMBER_ID, member.getId())
                    .andEqualTo(MemberAuth.DATA_STATUS, 0);
            List<MemberAuth> memberAuthList = memberAuthMapper.selectByExample(example);
            if (memberAuthList == null && memberAuthList.size() == 0) {
                return ServerResponse.createByErrorMessage("未绑定微信！");
            }
            return WeiXinPayUtil.getWeiXinH5Sign(price, outTradeNo, basePath,memberAuthList.get(0).getOpenid());
        }else {
            return WeiXinPayUtil.getWeiXinSign(price, outTradeNo, basePath, userRole);
        }
    }

    /*
    支付宝签名
     */
    public ServerResponse getAliSign(String businessOrderNumber) {
        //API路径
        String basePath = configUtil.getValue(SysConfig.DANGJIA_API_LOCAL, String.class);
        LOG.info(basePath + "getAliSign**********************************************");

        //检测订单有效性
        try {
            //检测订单有效性
            checkOrder(businessOrderNumber);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage(e.getMessage());
        }
        //生成支付流水
        PayOrder payOrder = getPayOrder("2", businessOrderNumber);
        String price = payOrder.getPrice().toString();
        String outTradeNo = payOrder.getNumber();//支付订单号
        return AliPayUtil.getAlipaySign(price, outTradeNo, basePath);
    }

    /* POS支付 */
    public ServerResponse getPOSSign(String businessOrderNumber) {
       try {
           //检测订单有效性
           checkOrder(businessOrderNumber);
           //生成支付流水
           PayOrder payOrder = payOrderMapper.getByNumber(businessOrderNumber);
           if (payOrder == null) {
               payOrder = getPayOrder("3", businessOrderNumber);
           }else{
               payOrder.setPayState("3");
               payOrderMapper.updateByPrimaryKeySelective(payOrder);
           }
           return ServerResponse.createBySuccess("检查成功",payOrder.getId());
       }catch (Exception e){
           return ServerResponse.createByErrorMessage("检查失败-原因："+e.getMessage());
       }
    }
    private String checkOrder(String businessOrderNumber) {
        if (CommonUtil.isEmpty(businessOrderNumber)) {
            throw new BaseException(ServerCode.ERROR, "该订单不存在");
        }
        String payState="0";
        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo("number", businessOrderNumber).andEqualTo("state", 1);
        List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
        if (businessOrderList.size() == 0) {
            throw new BaseException(ServerCode.ERROR, "该订单不存在");
        }
        BusinessOrder businessOrder = businessOrderList.get(0);
        if (businessOrder.getPayPrice().compareTo(new BigDecimal(0)) <= 0) {
            throw new BaseException(ServerCode.ERROR, "金额错误");
        }
        if (businessOrder.getState() == 3) {
            throw new BaseException(ServerCode.ERROR, "该订单不能重复支付");
        }

        if (businessOrder.getType() == 3) {
            DjSupplierPayOrder djSupplierPayOrder = masterSupplierPayOrderMapper.selectByPrimaryKey(businessOrder.getTaskId());
            if (djSupplierPayOrder == null) {
                throw new BaseException(ServerCode.ERROR, "该订单不存在");
            }
            if (djSupplierPayOrder.getState() == 1) {
                throw new BaseException(ServerCode.ERROR, "该订单不能重复支付");
            }
            if (djSupplierPayOrder.getPrice().compareTo(0D) <= 0) {
                throw new BaseException(ServerCode.ERROR, "金额错误");
            }
            payState=djSupplierPayOrder.getPayState();
        }
        if (businessOrder.getType() == 5) {
            HouseDistribution houseDistribution = iHouseDistributionMapper.selectByPrimaryKey(businessOrder.getTaskId());
            if (houseDistribution == null) {
                throw new BaseException(ServerCode.ERROR, "该订单不存在");
            }
            if (houseDistribution.getState() == 1) {
                throw new BaseException(ServerCode.ERROR, "该订单不能重复支付");
            }
            if (houseDistribution.getPrice().compareTo(0D) <= 0) {
                throw new BaseException(ServerCode.ERROR, "金额错误");
            }
        }
        return payState;
    }

    /**
     *
     * @param payState 1微信，2支付宝，3POS，4微信(PC)，5支付宝扫码(PC)
     * @param businessOrderNumber
     * @return
     */
    private PayOrder getPayOrder(String payState, String businessOrderNumber) {
        PayOrder payOrder = new PayOrder();
        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo(BusinessOrder.NUMBER, businessOrderNumber).andEqualTo(BusinessOrder.STATE, 1);
        List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
        if (businessOrderList == null || businessOrderList.size() <= 0) {
            throw new BaseException(ServerCode.ERROR, "未找到订单");
        }
        BusinessOrder businessOrder = businessOrderList.get(0);
        payOrder.setBusinessOrderNumber(businessOrderNumber);
        payOrder.setPrice(businessOrder.getPayPrice());//记录实付
        payOrder.setHouseId(businessOrder.getHouseId());
        payOrder.setNumber(System.currentTimeMillis() + "-" + Utils.generateWord());//生成支付订单号
        payOrder.setPayState(payState);//1微信，2支付宝
        payOrder.setState(0);// 0未支付,1已支付
        payOrderMapper.insert(payOrder);

        if (active != null && !"pre".equals(active)) {
            payOrder.setPrice(new BigDecimal("0.01"));
        }

        return payOrder;
    }
}
