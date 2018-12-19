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

}
