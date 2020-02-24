package com.dangjia.acg.modle.say;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2020/01/06
 * Time: 16:32
 */
@Data
@Entity
@Table(name = "dj_renovation_say")
@ApiModel(description = "装修说")
@FieldNameConstants(prefix = "")
public class RenovationSay extends BaseEntity {

    @Column(name = "fabulous")
    @Desc(value = "点赞量")
    @ApiModelProperty("点赞量")
    private Integer fabulous;

    @Column(name = "browse")
    @Desc(value = "浏览量")
    @ApiModelProperty("浏览量")
    private Integer browse;

    @Column(name = "share")
    @Desc(value = "分享量")
    @ApiModelProperty("分享量")
    private Integer share;

    @Column(name = "content")
    @Desc(value = "内容")
    @ApiModelProperty("内容")
    private String content;

    @Column(name = "cover_image")
    @Desc(value = "封面图")
    @ApiModelProperty("封面图")
    private String coverImage;

    @Column(name = "content_image")
    @Desc(value = "内容图")
    @ApiModelProperty("内容图")
    private String contentImage;


    @Transient
    private Integer whetherThumbUp;//是否点赞 1:点赞 0:未点赞


}
