package com.dangjia.acg.dto.finance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 14:41
 * 提现信息
 */
@Data
public class WebWithdrawDTO {
    private String id; //id
    private String name; //工匠姓名
    private String mobile;//电话
    private String workerId;//工人id
    private Integer state;//0未处理,1同意（成功） 2不同意(驳回)
    private BigDecimal money;//本次提现金额
    private String bankName;//银行名字
    private String cardNumber;//卡号
    private Date processingDate;//处理时间
    private Date createDate;// 创建日期
    private Date modifyDate;// 修改日期

    private Integer curWeekAddNum;//本周新增
    private Integer curWeekSuccessNum;//本周 成功的
    private Integer curWeekNoHandleNum;//本周 待处理的
    private Integer allNoHandleNum;//所有待处理的
}
