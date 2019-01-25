package com.dangjia.acg.dto.finance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 14:41
 */
@Data
public class WebWalletDTO {
    private Double workerPrice;//总赚到(所有订单工钱整体完工相加)

    //private BigDecimal haveMoney;//已获钱,可取余额加上押金
    private BigDecimal surplusMoney;//可取余额,已获钱减押金
    private BigDecimal retentionMoney;//实际滞留金
    private Integer houseOrder;//所有接单量

    private Double outAll;//支出总钱
    private Double income;//收入总钱
}
