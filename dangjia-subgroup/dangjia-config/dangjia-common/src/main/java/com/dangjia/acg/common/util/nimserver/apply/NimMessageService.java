package com.dangjia.acg.common.util.nimserver.apply;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.util.nimserver.NIMPost;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 用户维护
 * @author: QiYuXiang
 * @date: 2020/02/24
 */
public class NimMessageService {

    private static Logger LOG = LoggerFactory.getLogger(NimMessageService.class);

    /**
     * 个人发送消息到指定群组
     *
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param targetId 目标id 填写Group id （必填）
     * @param fromId   发送者的用户名（必填）
     * @param text     消息内容 （必填）
     */
    public static void sendGroupTextByAdmin(String appType, String targetId, String fromId, String text) {

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json =new JSONObject();
            params.add(new BasicNameValuePair("from", fromId));
            params.add(new BasicNameValuePair("to", targetId));
            params.add(new BasicNameValuePair("ope", "1"));
            params.add(new BasicNameValuePair("type", "0"));
            json.put("msg",text);
            params.add(new BasicNameValuePair("body", JSON.toJSONString(json)));
            //UTF-8编码,解决中文问题
            HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            String res = NIMPost.postNIMServer(NIMPost.SEND_MSG, entity, NIMPost.APPKEY, NIMPost.SECRET);
            LOG.info(res);
        } catch (Exception e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("Error Message: " + e.getMessage());
        }
    }


}

