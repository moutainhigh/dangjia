package com.dangjia.acg.dto.sale.client;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/22
 * Time: 11:37
 */
@Data
public class CustomerIndexDTO {
    private String clueId;
    private String mcId;
    private String name;//客户名称
    private String phone;//号码
    private Date modifyDate;//时间
    private String houseName;//房子名称
    private Date createDate;
    private String labelIdArr;//标签id
    private String userName;//跟进人
    private String userId;
    private String memberId;
    private Integer tips;//消息提示 0:无提示 1：提示
    private Integer phaseStatus;
    private String stage;
    private String wechat;
}
