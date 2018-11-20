package com.dangjia.acg.dto;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MessageBodyDTO {

    @Desc(value = "消息内容 ")
    @ApiModelProperty("消息内容 ")
    private String text;

    @Desc(value = "String 文件上传之后服务器端所返回的key，用于之后生成下载的url")
    @ApiModelProperty("String 文件上传之后服务器端所返回的key，用于之后生成下载的url")
    private String mediaId;

    @Desc(value = "long 文件的crc32校验码，用于下载大图的校验")
    @ApiModelProperty("long 文件的crc32校验码，用于下载大图的校验")
    private Long mediaCrc32;

    @Desc(value = "int 图片原始宽度")
    @ApiModelProperty("int 图片原始宽度")
    private Integer width;

    @Desc(value = "int 图片原始高度")
    @ApiModelProperty("int 图片原始高度")
    private Integer height;

    @Desc(value = "String 图片格式")
    @ApiModelProperty("String 图片格式")
    private String format;

    @Desc(value = "int 文件大小（字节数）")
    @ApiModelProperty("int 文件大小（字节数）")
    private Integer fsize;

    @Desc(value = "int 音频时长")
    @ApiModelProperty("int 音频时长")
    private Integer duration = -1;

    @Desc(value = "String 音频hash值")
    @ApiModelProperty("String 音频hash值")
    private String hash;
}
