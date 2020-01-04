package com.dangjia.acg.common.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.http.HttpUtil;
import netscape.javascript.JSException;

/**
 * @author Ruking.Cheng
 * @descrilbe 小程序网络请求封装
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2020/1/4 1:44 PM
 */
public class MininProgramUtil {
    private static final String SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String APP_ID = "wx17091542223c15eb";//小程序 appId
    private static final String SECRET = "d0827c2694273dd4835582457d9b17a4";//小程序 appSecret
    private static final String GRANT_TYPE = "authorization_code";//授权类型，此处只需填写 authorization_code

    public static JSONObject jscode2session(String code) throws Exception {
        String param = "appid=" + APP_ID + "&secret=" + SECRET + "&js_code=" + code + "&grant_type=" + GRANT_TYPE;
        //发起服务器请求
        String result = HttpUtil.httpRequest(SESSION_URL, "GET", param);
        return JSON.parseObject(result);
    }

    public static void main(String[] args) {
        try {
            JSONObject object = jscode2session("023b0ag90fkmTz1PCgf909ILf90b0agj");
            System.out.println("=====openid========" + object.getString("openid"));
            System.out.println("=====session_key========" + object.getString("session_key"));
            System.out.println("=====unionid========" + object.getString("unionid"));
            System.out.println("=====errcode========" + object.getInteger("errcode"));
            System.out.println("=====errmsg========" + object.getString("errmsg"));
        } catch (Exception e) {
            System.out.println("=====Exception========" + e.getMessage());
        }

    }
}
