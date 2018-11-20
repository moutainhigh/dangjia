package com.dangjia.acg.common.pay;

import com.dangjia.acg.common.response.ServerResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 10:31
 */
public class WeiXinPayUtil {

    //微信的应用ID(固定值)
    public final  static String appid="wx3d76cd502417fb9a";
    //微信商号(固定值)
    public final static String mch_id="1500837831";
    //付费方法类型(固定值)
    public final static String trade_type="APP";
    //商户密钥(固定值) 公众号共用
    public final static String key="8888888888fengjiangitV9999999999";
    //统一下单API接口链接  (固定值)
    public final static String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //支付成功回调接口
    //public final static String notify_url = "https://api.fengjiangit.com:8080/dangjia/app/pay!weixinAsynchronous.action";
    public final static String notify_url = "https://test.fengjiangit.com:8081/dangjia/app/pay!weixinAsynchronous.action";

    //微信公众号h5成功回调接口
    public final static String notify_h5 = "https://test.fengjiangit.com:8081/dangjia/app/pay!publicAsynchronous.action";

    /**
     * 微信支付调用接口
     * @return JSON形式的参数值
     */
    public static ServerResponse getWeiXinSign(String price, String out_trade_no) {
        Map<String,String> m = new HashMap<String,String>();
        try {
            String price1 = Integer.toString((int)((Double.parseDouble(price)*100)));
            System.out.println("价格是："+price1+"分");
            //将获取到的map值转换为xml格式
            m.put("appid", WeiXinPayUtil.appid);//微信的应用ID(固定值)
            m.put("body","当家App-微信支付");//商品描述（其实可有可无）
            m.put("mch_id", WeiXinPayUtil.mch_id);//微信支付商户信息号（固定值）
            m.put("nonce_str",out_trade_no);//支付订单号 不长于32位
            m.put("notify_url",notify_url);//接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
            m.put("out_trade_no",out_trade_no);//商户系统内部的订单号,32个字符内、可包含字母
            m.put("total_fee",price1);//订单总金额，单位为分
            m.put("trade_type", WeiXinPayUtil.trade_type);//固定值
            Map<String,String> sPara = WeiXinPayUtil.paraFilter(m);
            String prestr = WeiXinPayUtil.createLinkString(sPara);
            String mysign = WeiXinPayUtil.sign(prestr, WeiXinPayUtil.key, "utf-8").toUpperCase();
            m.put("sign", mysign);//sign签名,第一次随机签名
            //打包要发送的xml
            String respXml = WeiXinPayUtil.getRequestXML(m);
            //发起服务器请求
            String result = WeiXinPayUtil.httpRequest(WeiXinPayUtil.url, "POST", respXml);
            Map<?,?> map = WeiXinPayUtil.doXMLParse(result);
            //返回状态码
            String return_code = (String) map.get("return_code");
            //返回给APP端需要的参数
            Map<String, String> callbackMap = new HashMap<String, String>();
            if(return_code=="SUCCESS"||return_code.equals(return_code)) {
                //返回的预付单信息
                String prepay_id = (String) map.get("prepay_id");
                Long timeStamp = System.currentTimeMillis() / 1000;
                callbackMap.put("appid", WeiXinPayUtil.appid);
                callbackMap.put("partnerid", WeiXinPayUtil.mch_id);//微信支付商户信息号（固定值）
                callbackMap.put("prepayid", prepay_id);
                callbackMap.put("noncestr",map.get("nonce_str").toString());//随机字符串，不长于32位
                callbackMap.put("timestamp", timeStamp.toString());
                callbackMap.put("package", "Sign=WXPay");
                Map<String,String> sPara2 = WeiXinPayUtil.paraFilter(callbackMap);
                String prestrTow = WeiXinPayUtil.createLinkString(sPara2);
                String mysignTow = WeiXinPayUtil.sign(prestrTow, WeiXinPayUtil.key, "utf-8").toUpperCase();
                //sign签名,第二次随机签名
                callbackMap.put("sign", mysignTow);
                callbackMap.put("packages", "Sign=WXPay");
                return ServerResponse.createBySuccess("获取成功", callbackMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("微信app支付下单出错");
        }
        return ServerResponse.createBySuccessMessage("获取失败");
    }

    /**
     * 拼接XML请求路径，获取签名
     * @param m
     */
    @SuppressWarnings("rawtypes")
    private static String getRequestXML(Map<String, String> m){
        StringBuffer buffer = new StringBuffer();
        buffer.append("<xml>");
        Set<?> set = m.entrySet();
        Iterator<?> iterator = set.iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            buffer.append("<"+key+">"+value+"</"+key+">");
        }
        buffer.append("</xml>");
        return buffer.toString();
    }
    /**
     * 生成32位随机数
     *
     * @return 随机数
     */
    public static String getRandomNumbers() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 32; i++) {
            result += random.nextInt(10);
        }
        return result;
    }

    /**
     * 签名字符串
     * @param text 需要签名的字符串
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    private static String sign(String text, String key, String input_charset) {
        text = text + "&key=" + key;
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }

    /**
     * 签名字符串
     *  @param text 需要签名的字符串
     * @param sign 签名结果
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign, String key, String input_charset) {
        text = text + key;
        String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
        if (mysign.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }

    /** 获取内容字节
     * @param content
     * @param charset
     * @return
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    /**
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    private static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    private static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    /**
     *  通过微信地址请求获取返回值
     * @param requestUrl 请求地址
     * @param requestMethod 请求方法
     * @param outputStr 参数
     */
    private static String httpRequest(String requestUrl,String requestMethod,String outputStr){
        // 创建SSLContext
        StringBuffer buffer = null;
        try{
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            //往服务器端写内容
            if(null !=outputStr){
                OutputStream os=conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }
            // 读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }
    public static String urlEncodeUTF8(String source){
        String result=source;
        try {
            result=java.net.URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("服务器内容读取失败" + e);
        }
        return result;
    }
    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     */
    private static Map doXMLParse(String strxml) throws Exception {
        if(null == strxml || "".equals(strxml)) {
            return null;
        }
        Map m = new HashMap();
        InputStream in = StringInputStream(strxml);
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if(children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }
            m.put(k, v);
        }
        //关闭流
        in.close();
        return m;
    }
    /**
     * 获取子结点的xml
     * @param children
     * @return String
     */
    private static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }
    private static InputStream StringInputStream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }
}
