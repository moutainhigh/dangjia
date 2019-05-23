package com.dangjia.acg.common.util;

import java.util.HashMap;
import java.util.Map;

public class JsmsUtil {

	private static final String appkey = "0989b0db447914c7bcb17a46";
 	private static final String masterSecret = "fb15cefa3f693c0d6a45fe51";

	private static final String SEND_CUSTOMER_SERVICE="157646"; //客服确定开工
	private static final String SEND_CONSTRUCTION_PLANS ="157647";//设计师上传施工图
	private static final String SEND_ACTUARIES ="157648";//精算师上传精算
	private static final String SEND_BIG_HOUSEKEEPER ="157649";//大管家抢单成功
	private static final String SEND_BUTLER_ABANDONING ="157650";//大管家放弃
	private static final String SEND_BIG_HOUSEKEEPER_STARTS="157651"; //大管家开工 ---无参数
	private static final String SEND_CHANGE_THE_HOUSEKEEPER="157653"; //工程部换大管家---无参数
	private static final String SEND_CRAFTSMAN="157654"; //工匠抢单成功
	private static final String SEND_CRAFTSMAN_GIVE_UP="157655"; //工匠放弃
	private static final String SEND_CHANGING_CRAFTSMEN ="157656"; //工程部换工匠---无参数
	private static final String SEND_AUDIT_PASS="157657"; //阶段/整体完工审核通过
	private static final String SEND_COMPLETION_ACCEPTANCE="157658"; //大管家申请竣工验收
	private static final String SEND_ACTUARIAL_ADOPTION="157659"; //业主审核精算通过
	private static final String SEND_APPLY_OK="157265"; //申请审核通过后发送给工人
	private static final String SEND_SUPPLIER="157263"; //发给供应商下单了
	private static final String SEND_REGISTER_APPROVED="157268"; //注册通过审核
	private static final String SEND_GRAB_OK="157267"; //注册通过审核

	private static final String SEND_DESIGNER="160069"; //发给设计师业主支付设计费了


	/**
	 *  发给设计师业主支付设计费了
	 */
	public static String sendDesigner(String phone,String yzphone,String houseName) {
		Map<String,String> temp_para=new HashMap();
		temp_para.put("yzphone",yzphone);
		temp_para.put("house_name",houseName);
		String result =sendSMS(phone,SEND_DESIGNER,temp_para);
		return result;
	}
	/**
     *  发给供应商下单了
     */
	public static String sendSupplier(String phone,String address) {
		Map<String,String> temp_para=new HashMap();
		temp_para.put("order_address",address);
		String result =sendSMS(phone,SEND_SUPPLIER,temp_para);
		return result;
	}
	/**
     *  注册通过审核
     *
     */
	public static String registerApproved(String phone,String name,String workerTypeName) {
		Map<String,String> temp_para=new HashMap();
		temp_para.put("name",name);
		temp_para.put("workerTypeName",workerTypeName);
		String result =sendSMS(phone,SEND_REGISTER_APPROVED,temp_para);
		return result;
	}


	/**
	 * 验证码短信
	 * @param code 验证码COde
	 * @param phone 接收手机号
	 * @return
	 */
	public static String SMS(int code, String phone) {
		Map<String,String> temp_para=new HashMap();
		temp_para.put("code",String.valueOf(code));
		String result =sendSMS(phone,"157269",temp_para);
		return result;
	}

	public static String sendTempSMS(String phone,String mid,String residential,String building,String unit,String number) {
		Map<String,String> temp_para=new HashMap();
		temp_para.put("residential",residential);
		temp_para.put("building",building);
		temp_para.put("unit",unit);
		temp_para.put("housenumber",number);
		String result =sendSMS(phone,mid,temp_para);
		return result;
	}
	/**
	 * 发送短信
	 * @param phone 接收手机号
	 * @param mid 模板ID
	 * @param params 短信变量列表LIS
	 * @return
	 */
	public static String sendSMS(String phone, String mid, Map params) {
		try {
			SMSClient client = new SMSClient(masterSecret, appkey);
			SMSPayload payload = SMSPayload.newBuilder()
				.setMobileNumber(phone)
				.setTempId(Integer.parseInt(mid))
				.setTempPara(params)
				.build();
			SendSMSResult res = client.sendTemplateSMS(payload);
			String result =res.toString();
			return result;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return "";
	}


}
