package com.dangjia.acg.common.util.nimserver;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Firrela
 * @time 2016/3/7.
 */
public class NIMPost {

    public static String SYS_ACCID = "fengjiangit";  //AppKey

    public static String APPKEY = "fa7d0ea859179f00c81a70a8969f073a";  //AppKey
    public static String SECRET = "dcadf8b137a1";  //AppSecret
    /**
     * 用户相关
     */
    public static String CREATE = "https://api.netease.im/nimserver/user/create.action";//创建网易云通信
    public static String GETUINFOS = "https://api.netease.im/nimserver/user/getUinfos.action";//获取用户名片
    public static String UPDATE = "https://api.netease.im/nimserver/user/update.action";//修改用户token
    public static String UPDATEUINFO = "https://api.netease.im/nimserver/user/updateUinfo.action";//修改用户名片

    /**
     * 消息发送
     */
    public static String SEND_MSG = "https://api.netease.im/nimserver/msg/sendMsg.action";//发送消息
    public static String BROADCAST_MSG = "https://api.netease.im/nimserver/msg/broadcastMsg.action";//广播消息
    public static String SEND_BATCH_ATTACH_MSG = "https://api.netease.im/nimserver/msg/sendBatchAttachMsg.action";//推送消息

    /**
     * 群管理
     */
    public static String TEAM_CREATE = "https://api.netease.im/nimserver/team/create.action";//创建群
    public static String TEAM_ADD = "https://api.netease.im/nimserver/team/add.action";//拉人
    public static String TEAM_KICK = "https://api.netease.im/nimserver/team/kick.action";//踢人
    public static String TEAM_DETAIL = "https://api.netease.im/nimserver/team/queryDetail.action";//查询指定群的详细信息（群信息+成员详细信息）

    private static Logger logger = LoggerFactory.getLogger(NIMPost.class);

    public static String postNIMServer(String url, final HttpEntity entity, String appKey, String appSecret)
            throws IOException {
        HttpClientWyUtil httpClientUtil = new HttpClientWyUtil();
        HttpPost post = httpClientUtil.createPost(url, entity, null);

        // addHeader
        HttpClientWyUtil.addHeader(post, "AppKey", appKey);
        String nonce = UUIDUtil.getUUID();
        String curTime = String.valueOf(System.currentTimeMillis() / 1000);
        HttpClientWyUtil.addHeader(post, "Nonce", nonce);
        HttpClientWyUtil.addHeader(post, "CurTime", curTime);
        String checksum = getCheckSum(nonce, curTime, appSecret);
        HttpClientWyUtil.addHeader(post, "CheckSum", checksum);

        // logger
        logger.info("Nonce {} | CurlTime {} | CheckSum {}", new Object[]{nonce, curTime, checksum});

        return httpClientUtil.fetchData(post);
    }

    private static String getCheckSum(String nonce, String curTime, String appSecret) {
        String plaintext = new StringBuffer(appSecret).append(nonce).append(curTime).toString();
        return EncodeUtil.encode(plaintext, "SHA1");
    }
}
