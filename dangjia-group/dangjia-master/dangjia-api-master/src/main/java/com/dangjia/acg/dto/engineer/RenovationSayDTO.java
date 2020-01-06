package com.dangjia.acg.dto.engineer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 */
@Data
public class RenovationSayDTO {

    private Date createDate;// 创建日期
    private Integer fabulous; //点赞量
    private Integer browse;//浏览量
    private Integer share;//分享量
    private String id;
    private String content;//内容
    private String contentImage;//装修说内容图
    private List<String> contentImages;//装修说内容图list
    private String coverImage;//装修说封面
    private String coverImages;//装修说封面
//    private List<String> arrContentImages;//装修说封面 + 装修说内容图
}
