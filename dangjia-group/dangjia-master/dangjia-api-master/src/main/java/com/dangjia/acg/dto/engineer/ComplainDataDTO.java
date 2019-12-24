package com.dangjia.acg.dto.engineer;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 */
@Data
public class ComplainDataDTO {

    private Integer status;//'处理状态.0:待处理。1.驳回。2.接受,
    private Date createDate;//申诉时间
    private String description;//申诉描述,
    private String image;//申诉图片
    private List<String> images;//
    private Date handleDate;//平台处理时间
    private String name;//工匠名称
    private String mobile;//工匠电话
    private String head;//工匠头像

    private Integer type;//1-投诉中 2-已完成
}
