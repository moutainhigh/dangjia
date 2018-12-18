package com.dangjia.acg.controller;

import cn.jmessage.api.message.MessageListResult;
import com.dangjia.acg.api.MessageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.dto.MessageBodyDTO;
import com.dangjia.acg.dto.MessagePayloadDTO;
import com.dangjia.acg.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息相关维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@RestController
public class MessageController implements MessageAPI {

    @Autowired
    private MessageService messageService;

    /**
     *  个人发送消息到指定的人
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param targetId 目标id single填充用户名
     * @param fromId 发送者的用户名（必填）
     * @param text 消息内容 （必填）
     */
    @Override
    @ApiMethod
    public  void sendSingleTextByAdmin(String appType,String targetId, String fromId,String text) {
        messageService.sendSingleTextByAdmin( appType, targetId,  fromId, text);
    }

    /**
     *  个人发送消息到指定群组
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param targetId 目标id 填写Group id （必填）
     * @param fromId 发送者的用户名（必填）
     * @param text 消息内容 （必填）
     */
    @Override
    @ApiMethod
    public  void sendGroupTextByAdmin(String appType,String targetId, String fromId,String text) {
        messageService.sendGroupTextByAdmin( appType, targetId,  fromId, text);
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
    @Override
    @ApiMethod
    public  void sendImageMessage(String appType, MessageBodyDTO bodyDTO, MessagePayloadDTO payloadDTO) {
        messageService.sendImageMessage( appType, bodyDTO,  payloadDTO);
    }

    /**
     *  获取无游标的消息列表（首次），将返回游标，以后的请求将使用光标获取消息。
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param count 总数
     * @param begin_time 开始时间
     * @param end_time 结束时间
     * @return
     */
    @Override
    @ApiMethod
    public  MessageListResult getMessageList(String appType,int count, String begin_time, String end_time) {
        return  messageService.getMessageList( appType, count,  begin_time,end_time);
    }
    /**
     *  获取有游标的消息列表，使用光标获取消息。
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param cursor 光标通过getMessageList获得
     * @return
     */
    @Override
    @ApiMethod
    public  MessageListResult getMessageListByCursor(String appType,String cursor) {
        return  messageService.getMessageListByCursor( appType,cursor);
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
    @Override
    @ApiMethod
    public  MessageListResult getUserMessageList(String appType,String username, int count, String begin_time, String end_time) {
        return  messageService.getUserMessageList( appType, username,  count,  begin_time,  end_time);
    }

    /**
     *  获取有游标的用户消息列表，使用光标获取消息。
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param cursor 光标通过getUserMessageList获得
     * @return
     */
    @Override
    @ApiMethod
    public  MessageListResult getUserMessagesByCursor(String appType,String username,String cursor) {
        return  messageService.getUserMessagesByCursor( appType, username,  cursor);
    }

    /**
     * 消息回收
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 发送此msg的用户名
     * @param msgid 	消息msgid
     */
    @Override
    @ApiMethod
    public  void retractMessage(String appType,String username,int msgid) {
        messageService.retractMessage( appType, username,  msgid);
    }
    /**
     * 系统通告，通知所有人
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param title 通知标题
     * @param alert 通知内容
     */
    @Override
    @ApiMethod
    public  void sendSysPush(String appType,String title,String alert,String speak) {
        messageService.sendSysPush( appType, title,  alert, speak);
    }

    /**
     * 系统通告，通知指定注册ID
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param memberId memberID数组集合
     * @param title 通知标题
     * @param alert 通知内容
     */
    @Override
    @ApiMethod
    public  void sendMemberIdPush(String appType,String[] memberId,String title,String alert,String speak){
        messageService.sendMemberIdPush( appType,memberId, title,  alert, speak);
    }
}
