package com.dangjia.acg.dto.sale.store;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/22
 * Time: 14:40
 */
@Data
public class MonthlyTargetDTO {
    private String modifyDate;
    private Integer targetNumber;//目标数
    private Integer Complete;//下单数
}
