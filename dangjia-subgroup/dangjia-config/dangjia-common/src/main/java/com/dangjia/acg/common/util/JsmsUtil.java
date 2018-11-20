package com.dangjia.acg.common.util;

import cn.jsms.api.SendSMSResult;
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.SMSPayload;

import java.util.HashMap;
import java.util.Map;

public class JsmsUtil {

	private static final String appkey = "0989b0db447914c7bcb17a46";
 	private static final String masterSecret = "fb15cefa3f693c0d6a45fe51";
    /**
     * 申请审核通过后发送给工人
     */
	public static String applyOK(String phone,String residential,String building,String unit,String number) {
		Map<String,String> temp_para=new HashMap();
		temp_para.put("residential",residential);
		temp_para.put("building",building);
		temp_para.put("unit",unit);
		temp_para.put("housenumber",number);
		String result =sendSMS(phone,"157265",temp_para);
		return result;
	}
	/**
     *  发给供应商下单了
     */
	public static String sendSupplier(String phone,String address) {
		Map<String,String> temp_para=new HashMap();
		temp_para.put("order_address",address);
		String result =sendSMS(phone,"157263",temp_para);
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
		String result =sendSMS(phone,"157268",temp_para);
		return result;
	}
	/**
     *  抢单审核通过
     *  
     */
	public static String grabOK(String phone,String residential,String building,String unit,String number) {
		Map<String,String> temp_para=new HashMap();
		temp_para.put("residential",residential);
		temp_para.put("building",building);
		temp_para.put("unit",unit);
		temp_para.put("housenumber",number);
		String result =sendSMS(phone,"157267",temp_para);
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

	public static void main(String[] args) {
		applyOK("13755051550","欣欣小区","99","9","909");
	}


}
