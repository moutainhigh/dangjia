package com.dangjia.acg.modle.config;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 推送消息历史表
 */
@Data
@Entity
@Table(name = "dj_config_message")
@ApiModel(description = "推送消息历史表")
@FieldNameConstants(prefix = "")
public class ConfigMessage extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "消息标题")
    @ApiModelProperty("消息标题")
    private String name;

    @Column(name = "text")
    @Desc(value = "消息内容")
    @ApiModelProperty("消息内容")
    private String text;

    @Column(name = "speak")
    @Desc(value = "语音内容")
    @ApiModelProperty("语音内容")
    private String speak;

    @Column(name = "icon")
    @Desc(value = "消息图标")
    @ApiModelProperty("消息图标")
    private String icon;


    @Column(name = "app_type")
    @Desc(value = "来源应用（1:业主端，2:工匠端）")
    @ApiModelProperty("来源应用（1:业主端，2:工匠端）")
    private String appType;

    @Column(name = "type")
    @Desc(value = "动作类型（0:直接跳转URL，1:跳转支付，2:只显示，3:登录，4:工匠端抢单界面，5:工匠端施工界面）")
    @ApiModelProperty("动作类型（0:直接跳转URL，1:跳转支付，2:只显示，3:登录，4:工匠端抢单界面，5:工匠端施工界面）")
    private Integer type;

    @Column(name = "data")
    @Desc(value = "动作内容（type==0为跳转地址，type==1为房子id，type==2无）")
    @ApiModelProperty("动作内容（type==0为跳转地址，type==1为房子id，type==2无）")
    private String data;

    @Column(name = "from_uid")
    @Desc(value = "发送者的username")
    @ApiModelProperty("发送者的username）")
    private String fromUid;

    @Column(name = "target_type")
    @Desc(value = "发送目标类型 0 - 个人，1 - 系统")
    @ApiModelProperty("发送目标类型 0 - 个人，1 - 系统")
    private String targetType;

    @Column(name = "target_uid")
    @Desc(value = "目标id 个人填username ,群组填Group id ,聊天室 填chatroomid")
    @ApiModelProperty("目标id 个人填username ,群组填Group id ,聊天室 填chatroomid")
    private String targetUid;
    //所有图片字段加入域名和端口，形成全路径
    public void initPath(String address){
        this.icon= StringUtils.isEmpty(this.icon)?null:address+this.icon;
    };
}