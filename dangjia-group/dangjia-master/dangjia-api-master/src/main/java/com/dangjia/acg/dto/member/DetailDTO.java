package com.dangjia.acg.dto.member;

import lombok.Data;

import java.util.Date;

/**
 * author: Ronalcheng
 * Date: 2018/12/18 0018
 * Time: 19:11
 */
@Data
public class DetailDTO {
    private String workerDetailId;//流水id
    private Integer state;//账户对象：0=余额  1=滞留金
    private String image;//图标
    private String name;
    private Date createDate;
    private String money;//钱

    private Integer type;//0=汇总  1=流水

    private String time;//月份
    private Double outMoneyTotal;//支出总钱
    private Double inMoneyTotal;//收入总钱
}
