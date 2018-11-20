package com.dangjia.acg.api;

import cn.jmessage.api.message.MessageListResult;
import com.dangjia.acg.dto.MessageBodyDTO;
import com.dangjia.acg.dto.MessagePayloadDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 消息相关维护
 * @author: QiYuXiang
 * @date: 2018/10/24
 */
@FeignClient("dangjia-service-message")
@Api(value = "消息相关维护接口", description = "消息相关维护接口")
public interface MessageAPI {


    /**
     *  个人发送消息到指定的人
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param targetId 目标id single填充用户名
     * @param fromId 发送者的用户名（必填）
     * @param text 消息内容 （必填）
     */
    @RequestMapping(value = "sendSingleTextByAdmin", method = RequestMethod.POST)
    @ApiOperation(value = "个人发送消息到指定的人", notes = "个人发送消息到指定的人")
    void sendSingleTextByAdmin(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="targetId",value = "目标id single填充用户名")@RequestParam("targetId") String targetId,
            @ApiParam(name ="fromId",value = "发送者的用户名（必填）")@RequestParam("fromId") String fromId,
            @ApiParam(name ="text",value = "消息内容 （必填）")@RequestParam("text") String text) ;

    /**
     *  个人发送消息到指定群组
     * @param appType   应用类型（zx=当家装修，gj=当家工匠）
     * @param targetId 目标id 填写Group id （必填）
     * @param fromId 发送者的用户名（必填）
     * @param text 消息内容 （必填）
     */
    @RequestMapping(value = "sendGroupTextByAdmin", method = RequestMethod.POST)
    @ApiOperation(value = "个人发送消息到指定群组", notes = "个人发送消息到指定群组")
    void sendGroupTextByAdmin(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="targetId",value = " 目标id 填写Group id （必填）")@RequestParam("targetId") String targetId,
            @ApiParam(name ="fromId",value = "发送者的用户名（必填）")@RequestParam("fromId") String fromId,
            @ApiParam(name ="text",value = "消息内容 （必填）")@RequestParam("text") String text) ;

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
    @RequestMapping(value = "sendImageMessage", method = RequestMethod.POST)
    @ApiOperation(value = "发送图片消息到", notes = "发送图片消息到")
    void sendImageMessage(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="bodyDTO",value = "Json对象的消息体(具体参数配置查看极光API[https://docs.jiguang.cn/jmessage/server/rest_api_im/#_18])")@RequestParam("bodyDTO") MessageBodyDTO bodyDTO,
            @ApiParam(name ="payloadDTO",value = "Json对象的参数体(具体参数配置查看极光API[https://docs.jiguang.cn/jmessage/server/rest_api_im/#_18])")@RequestParam("payloadDTO") MessagePayloadDTO payloadDTO) ;


    /**
     *  获取无游标的消息列表（首次），将返回游标，以后的请求将使用光标获取消息。
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param count 总数
     * @param begin_time 开始时间
     * @param end_time 结束时间
     * @return
     */
    @RequestMapping(value = "getMessageList", method = RequestMethod.POST)
    @ApiOperation(value = "获取无游标的消息列表（首次），将返回游标，以后的请求将使用光标获取消息。", notes = "获取无游标的消息列表（首次），将返回游标，以后的请求将使用光标获取消息。")
    MessageListResult getMessageList(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="count",value = "总数")@RequestParam("count") int count,
            @ApiParam(name ="begin_time",value = "开始时间")@RequestParam("begin_time") String begin_time,
            @ApiParam(name ="end_time",value = "结束时间")@RequestParam("end_time") String end_time);
    /**
     *  获取有游标的消息列表，使用光标获取消息。
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param cursor 光标通过getMessageList获得
     * @return
     */
    @RequestMapping(value = "getMessageListByCursor", method = RequestMethod.POST)
    @ApiOperation(value = "获取有游标的消息列表，使用光标获取消息。", notes = "获取有游标的消息列表，使用光标获取消息。")
    MessageListResult getMessageListByCursor(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="cursor",value = "光标,通过getMessageList获得")@RequestParam("cursor") String cursor) ;

    /**
     * 获取无游标的用户消息列表（首次），将返回游标，以后的请求将使用光标获取消息
     * @param appType  应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param count 总数
     * @param begin_time 开始时间
     * @param end_time 结束时间
     * @return
     */
    @RequestMapping(value = "getUserMessageList", method = RequestMethod.POST)
    @ApiOperation(value = "获取无游标的用户消息列表（首次），将返回游标，以后的请求将使用光标获取消息", notes = "获取无游标的用户消息列表（首次），将返回游标，以后的请求将使用光标获取消息")
    MessageListResult getUserMessageList(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "用户名")@RequestParam("username") String username,
            @ApiParam(name ="count",value = "总数")@RequestParam("count") int count,
            @ApiParam(name ="begin_time",value = "开始时间")@RequestParam("begin_time") String begin_time,
            @ApiParam(name ="end_time",value = "结束时间")@RequestParam("end_time") String end_time) ;

    /**
     *  获取有游标的用户消息列表，使用光标获取消息。
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 用户名
     * @param cursor 光标通过getUserMessageList获得
     * @return
     */
    @RequestMapping(value = "getUserMessagesByCursor", method = RequestMethod.POST)
    @ApiOperation(value = "获取有游标的用户消息列表，使用光标获取消息。", notes = "获取有游标的用户消息列表，使用光标获取消息。")
    MessageListResult getUserMessagesByCursor(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "用户名")@RequestParam("username") String username,
            @ApiParam(name ="cursor",value = "光标，通过getUserMessageList获得")@RequestParam("cursor") String cursor);

    /**
     * 消息回收
     * @param appType 应用类型（zx=当家装修，gj=当家工匠）
     * @param username 发送此msg的用户名
     * @param msgid 	消息msgid
     */
    @RequestMapping(value = "retractMessage", method = RequestMethod.POST)
    @ApiOperation(value = "消息回收", notes = "消息回收")
    void retractMessage(
            @ApiParam(name ="appType",value = "应用类型（zx=当家装修，gj=当家工匠）")@RequestParam("appType") String appType,
            @ApiParam(name ="username",value = "发送此msg的用户名")@RequestParam("username") String username,
            @ApiParam(name ="msgid",value = "消息msgid")@RequestParam("msgid") int msgid) ;
}
