package com.dangjia.acg.common.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.http.HttpUtil;

/**
 * @author Qiyuxiang
 * @date 2018-04-26 23:13:17
 **/
public class AccessTokenUtil {

  public static String getAccessToken(String url, String appId, String secret) {
    String params = "grant_type=client_credential" + "&appid=" + appId + "&secret=" + secret;
    String data = HttpUtil.sendGet(url, params);
    JSONObject jsonObject = JSON.parseObject(data);
    return jsonObject.get("access_token").toString();
  }
  public static JSONObject getAccessTokenJson(String url, String appId, String secret) {
    String params = "grant_type=client_credential" + "&appid=" + appId + "&secret=" + secret;
    String data = HttpUtil.sendGet(url, params);
    JSONObject jsonObject = JSON.parseObject(data);
    return jsonObject;
  }
}
