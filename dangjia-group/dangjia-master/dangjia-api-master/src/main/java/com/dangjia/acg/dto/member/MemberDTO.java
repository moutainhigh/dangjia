package com.dangjia.acg.dto.member;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 7/1/2020
 * Time: 下午 3:39
 */
@Data
public class MemberDTO {

    private String head;

    private String mobile;

    private Integer obligationCount;//待付款数量
    private Integer deliverCount;//待发货数量
    private Integer receiveCount;//待收货数量

    private Integer discountCouponCount;//优惠券数量

    private Integer bankCardCount;//银行卡数量

    private BigDecimal surplusMoney;//余额

    private String name;

    private long airtime;//活动播报时间

    private Date orderPayTime;
}
