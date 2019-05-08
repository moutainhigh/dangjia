package com.dangjia.acg.dto.member;

import lombok.Data;

import java.util.Date;

/**
 * author: ysl
 * Date: 2019/1/7
 * Time: 16:00
 * 沟通记录
 */
@Data
public class CustomerRecordDTO {
    private String describes;//沟通记录
    protected Date createDate;// 创建日期
    protected Date remindTime;//提醒日期
    private String customerName;//客服名字
}
