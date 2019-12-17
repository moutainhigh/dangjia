package com.dangjia.acg.dto.member;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/19 0019
 * Time: 10:30
 * 提现详情
 */
@Data
public class WithdrawDTO {
    private List<BrandCardDTO> brandCardDTOList;
    private String mobile;//电话
    private BigDecimal surplusMoney;//可取余额
    private String message;//头部提示信息
    private String messageUrl;//头部提示信息跳转地址
    private List<RuleDTO> ruleList;//提现规则

    @Data
    public static class RuleDTO {
        private double maxMoney;//最大金额（不包含，-1为无穷大）
        private double minMoney;//最小金额（包含）
        private int rate;//费率，千分值
        private double extraMoney;//额外收取金额
        private String ruleMessage;//收费规则描述
    }
}
