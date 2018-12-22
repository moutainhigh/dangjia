package com.dangjia.acg.dto.member;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author: Ronalcheng
 * Date: 2018/12/18 0018
 * Time: 18:41
 */
@Data
public class WalletDTO {
    private Double workerPrice;//总赚到(所有订单工钱整体完工相加)

    //private BigDecimal haveMoney;//已获钱,可取余额加上押金
    private BigDecimal surplusMoney;//可取余额,已获钱减押金
    private BigDecimal retentionMoney;//实际滞留金
    private Integer houseOrder;//所有接单量

    private Double outAll;//支出总钱
    private Double income;//收入总钱
}
