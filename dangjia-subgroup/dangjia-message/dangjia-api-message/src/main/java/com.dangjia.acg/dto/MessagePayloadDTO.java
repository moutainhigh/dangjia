package com.dangjia.acg.dto;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MessagePayloadDTO {

    @Desc(value = "版本号 目前是1  ")
    @ApiModelProperty("版本号 目前是1  ")
    private Integer mVersion;

    @Desc(value = "发送目标类型 single - 个人，group - 群组 ，chatroom - 聊天室 ")
    @ApiModelProperty("发送目标类型 single - 个人，group - 群组， chatroom - 聊天室 ")
    private String mTargetType;

    @Desc(value = "目标id single填username group 填Group id chatroom 填chatroomid（必填） ")
    @ApiModelProperty("目标id single填username group 填Group id chatroom 填chatroomid（必填） ")
    private String mTargetId;

    @Desc(value = "发送消息者身份 当前只限admin用户，必须先注册admin用户 （必填） ")
    @ApiModelProperty("发送消息者身份 当前只限admin用户，必须先注册admin用户 （必填） ")
    private String mFromType;

    @Desc(value = "发送者的username （必填) ")
    @ApiModelProperty("发送者的username （必填） ")
    private String mFromId;

    @Desc(value = "跨应用目标appkey ")
    @ApiModelProperty("跨应用目标appkey ")
    private String mTargetAppKey;

    @Desc(value = "发送者展示名（选填） ")
    @ApiModelProperty("发送者展示名（选填） ")
    private String mFromName;

    @Desc(value = "接受者展示名（选填） ")
    @ApiModelProperty("接受者展示名（选填） ")
    private String mTargetName;

    @Desc(value = "消息是否离线存储 true或者false，默认为false，表示需要离线存储（选填） ")
    @ApiModelProperty("消息是否离线存储 true或者false，默认为false，表示需要离线存储（选填） ")
    private boolean mNoOffline = false;

    @Desc(value = "消息是否在通知栏展示 true或者false，默认为false，表示在通知栏展示（选填） ")
    @ApiModelProperty("消息是否在通知栏展示 true或者false，默认为false，表示在通知栏展示（选填） ")
    private boolean mNoNotification = false;
}
