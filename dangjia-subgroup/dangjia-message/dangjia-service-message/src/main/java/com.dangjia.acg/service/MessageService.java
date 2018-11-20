package com.dangjia.acg.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jmessage.api.JMessageClient;
import cn.jmessage.api.common.model.message.MessageBody;
import cn.jmessage.api.common.model.message.MessagePayload;
import cn.jmessage.api.message.MessageListResult;
import cn.jmessage.api.message.MessageType;
import cn.jmessage.api.message.SendMessageResult;
import com.dangjia.acg.dto.MessageBodyDTO;
import com.dangjia.acg.dto.MessagePayloadDTO;
import org.springframework.stereotype.Service;

/**
 * 消息相关维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@Service
public class MessageService  extends BaseService {


    /**
     *  个人发送消息到指定的人
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param targetId 目标id single填充用户名
     * @param fromId 发送者的用户名（必填）
     * @param text 消息内容 （必填）
     */
    public  void sendSingleTextByAdmin(String appType,String targetId, String fromId,String text) {

        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            MessageBody body = MessageBody.text(text);
            SendMessageResult result = client.sendSingleTextByAdmin(targetId, fromId, body);
            LOG.info(String.valueOf(result.getMsg_id()));
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *  个人发送消息到指定群组
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param targetId 目标id 填写Group id （必填）
     * @param fromId 发送者的用户名（必填）
     * @param text 消息内容 （必填）
     */
    public  void sendGroupTextByAdmin(String appType,String targetId, String fromId,String text) {

    	try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
    		MessageBody body = MessageBody.text(text);
    		SendMessageResult result = client.sendGroupTextByAdmin(targetId, fromId, body);
    		LOG.info(String.valueOf(result.getMsg_id()));
    	} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    /**
     *  发送图片消息到
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param bodyDTO Json对象的消息体
     *                 .MediaId 文件上传之后服务器端所返回的key，用于之后生成下载的url（必填）
     *                 .MediaCrc32 文件的crc32校验码，用于下载大图的校验 （必填）
     *                 .Width 图片原始宽度（必填）
     *                 .Height 图片原始高度（必填）
     *                 .Format 图片格式（必填）
     *                 .Fsize 文件大小（字节数）（必填）
     * @param payloadDTO Json对象的消息配置
     *                 .Version 版本号 目前是1 （必填）
     *                 .TargetType 发送目标类型 single - 个人，group - 群组 ，chatroom - 聊天室（必填）
     *                 .TargetId 目标id single填username， group 填Group id ，chatroom 填chatroomid（必填）
     *                 .FromType 发送消息者身份 当前只限admin用户，必须先注册admin用户 （必填）
     *                 .FromId 发送者的username （必填）
     */
    public  void sendImageMessage(String appType, MessageBodyDTO bodyDTO, MessagePayloadDTO payloadDTO) {

        JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
        MessageBody messageBody = new MessageBody.Builder()
                .setMediaId(bodyDTO.getMediaId())
                .setMediaCrc32(bodyDTO.getMediaCrc32())
                .setWidth(bodyDTO.getWidth())
                .setHeight(bodyDTO.getHeight())
                .setFormat(bodyDTO.getFormat())
                .setFsize(bodyDTO.getFsize())
                .build();

        MessagePayload payload = MessagePayload.newBuilder()
                .setVersion(payloadDTO.getMVersion())
                .setTargetType(payloadDTO.getMTargetType())
                .setTargetId(payloadDTO.getMTargetId())
                .setFromType(payloadDTO.getMFromType())
                .setFromId(payloadDTO.getMFromId())
                .setMessageType(MessageType.IMAGE)
                .setMessageBody(messageBody)
                .build();

        try {
            SendMessageResult res = client.sendMessage(payload);
            System.out.println(res.getMsg_id());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }

    }

    /**
     *  获取无游标的消息列表（首次），将返回游标，以后的请求将使用光标获取消息。
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param count 总数
     * @param begin_time 开始时间
     * @param end_time 结束时间
     * @return
     */
    public  MessageListResult getMessageList(String appType,int count, String begin_time, String end_time) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            MessageListResult result = client.getMessageList(count, begin_time, end_time);
//            String cursor = result.getCursor();//游标
//            if (null != cursor && StringUtils.isNotEmpty(cursor)) {
//                MessageListResult secondResult = client.getMessageListByCursor(cursor);
//                return secondResult;
//            }
            return result;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }
    /**
     *  获取有游标的消息列表，使用光标获取消息。
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param cursor 光标通过getMessageList获得
     * @return
     */
    public  MessageListResult getMessageListByCursor(String appType,String cursor) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            MessageListResult secondResult = client.getMessageListByCursor(cursor);
            return secondResult;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }

    /**
     * 获取无游标的用户消息列表（首次），将返回游标，以后的请求将使用光标获取消息
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param count 总数
     * @param begin_time 开始时间
     * @param end_time 结束时间
     * @return
     */
    public  MessageListResult getUserMessageList(String appType,String username, int count, String begin_time, String end_time) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            MessageListResult result = client.getUserMessages(username, count, begin_time, end_time);
            String cursor = result.getCursor();
            MessageListResult secondResult = client.getUserMessagesByCursor(username, cursor);
            return result;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }

    /**
     *  获取有游标的用户消息列表，使用光标获取消息。
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param cursor 光标通过getUserMessageList获得
     * @return
     */
    public  MessageListResult getUserMessagesByCursor(String appType,String username,String cursor) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            MessageListResult secondResult = client.getUserMessagesByCursor(username, cursor);
            return secondResult;
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            return null;
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
            return null;
        }
    }

    /**
     * 消息回收
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 发送此msg的用户名
     * @param msgid 	消息msgid
     */
    public  void retractMessage(String appType,String username,int msgid) {
        try {

            JMessageClient client = new JMessageClient(getAppkey(appType), getMasterSecret(appType));
            ResponseWrapper result = client.retractMessage(username, msgid);
            LOG.info(result.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }
}
