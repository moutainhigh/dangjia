package com.dangjia.acg.common.util.nimserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Firrela
 * @time 2016/3/7.
 */
public class MainTest {

    private static Logger logger = LoggerFactory.getLogger(MainTest.class);

    public static final void main(String[] args) throws IOException {
        AccessToken accessToken=new AccessToken();
        accessToken.setMemberId("13755051551");
        accessToken.setUserToken("d5d5s68e85666");
        accessToken.setPhone("13755051551");
        Member member=new Member();
        member.setHead("https://static-obs.fengjiangit.com/qrcode/img_tx01.png");
        member.setNickName("小99");
        member.setName("九九");
        accessToken.setMember(member);
//        String res = createUser(accessToken);
//        System.out.println("--------"+res);
//
//        String res2 = getUinfos(accessToken.getMemberId());
//        System.out.println("--------"+res2);


        String res2 = sendMsg("13755051551","13755051550");
        System.out.println("--------"+res2);
        //TODO: 对结果的业务处理，如解析返回结果，并保存成功注册的用户
    }

    public static String createUser(AccessToken accessToken) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accid", accessToken.getMemberId()));
        params.add(new BasicNameValuePair("name", CommonUtil.isEmpty(accessToken.getMember().getName())?accessToken.getMember().getNickName():accessToken.getMember().getName()));
        params.add(new BasicNameValuePair("token", accessToken.getUserToken()));
        params.add(new BasicNameValuePair("icon", accessToken.getMember().getHead()));
        //UTF-8编码,解决中文问题
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");

        String res = NIMPost.postNIMServer(NIMPost.CREATE, entity, NIMPost.APPKEY, NIMPost.SECRET);
        logger.info("createUser httpRes: {}", res);
        return res;
    }

    public static String getUinfos(String userTokens) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONArray json =new JSONArray();
        json.addAll(Arrays.asList(userTokens.split(",")));
        params.add(new BasicNameValuePair("accids", JSON.toJSONString(json)));
        //UTF-8编码,解决中文问题
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");

        String res = NIMPost.postNIMServer(NIMPost.GETUINFOS, entity, NIMPost.APPKEY, NIMPost.SECRET);
        return res;
    }

    public static String sendMsg(String accids,String toaccids) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json =new JSONObject();
        params.add(new BasicNameValuePair("from", accids));
        params.add(new BasicNameValuePair("to", toaccids));
        params.add(new BasicNameValuePair("ope", "0"));
        params.add(new BasicNameValuePair("type", "0"));
        json.put("msg","哈哈哈哈哈哈44444");
        params.add(new BasicNameValuePair("body", JSON.toJSONString(json)));
        //UTF-8编码,解决中文问题
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");

        String res = NIMPost.postNIMServer(NIMPost.SEND_MSG, entity, NIMPost.APPKEY, NIMPost.SECRET);
        return res;
    }
}
