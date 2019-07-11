package com.dangjia.acg.dto.matter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 实体类 - 装修指南
 */
@Data
public class RenovationManualDTO {
    protected String id;

    @ApiModelProperty("创建时间")
    protected Date createDate;// 创建日期

    @ApiModelProperty("修改时间")
    protected Date modifyDate;// 修改日期

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("阶段id")
    private String workerTypeId;//workertyid

    @ApiModelProperty("链接名称")
    private String urlName;//

    @ApiModelProperty("指南内容")
    private String test;//

    @ApiModelProperty("链接地址")
    private String url;//

    @ApiModelProperty("装修类型")
    private String types;//

    @ApiModelProperty("状态0:可用；1:不可用")
    private Integer state;//

    @ApiModelProperty("排序序号")
    private Integer orderNumber;//

    @ApiModelProperty("封面图片")
    private String image;

    @ApiModelProperty("阶段名称")
    private String workerTypeName;

    @ApiModelProperty("图片地址")
    private String imageUrl;

    @ApiModelProperty("访问量")
    private Integer num;//
}