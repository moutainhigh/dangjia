package com.dangjia.acg.dto.engineer;

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
    private List<String> contentImages;//装修说内容图List
    private String coverImage;//装修说封面
    private String coverImages;//装修说封面
    private Integer whetherThumbUp;//是否点赞 1:点赞 0:未点赞
}
