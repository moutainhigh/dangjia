package com.dangjia.acg.common.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.http.HttpUtil;
import org.slf4j.LoggerFactory;

/**
 * @author Qiyuxiang
 * @date 2018-04-26 22:53:12
 **/
public class TemplateUtil {

  private static org.slf4j.Logger log = LoggerFactory.getLogger(TemplateUtil.class);

  public static boolean sendTemplateMsg(String requestUrl, String token, String template) {
    boolean flag = false;
    requestUrl = requestUrl.replace("ACCESS_TOKEN", token);
    String jsonStr = HttpUtil.sendPost(requestUrl, template);
    if (jsonStr != null) {
      JSONObject jsonResult = JSONObject.parseObject(jsonStr);
      int errorCode = jsonResult.getInteger("errcode");
      String errorMessage = jsonResult.getString("errmsg");
      if (errorCode == 0) {
        flag = true;
      } else {
        log.info("模板消息发送失败:" + errorCode + "," + errorMessage);
        flag = false;
      }
    }
    return flag;
  }

}
