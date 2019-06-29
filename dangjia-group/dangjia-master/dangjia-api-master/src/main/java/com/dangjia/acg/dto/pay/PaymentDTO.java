package com.dangjia.acg.dto.pay;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 17:28
 */
@Data
public class PaymentDTO<T> {
    private WorkerDTO workerDTO;//工匠信息
    private UpgradeDesignDTO upgradeDesignDTO;//升级设计
    private List<ActuaryDTO> actuaryDTOList;//商品
    private List<T> datas;//数据集合
    private UpgradeSafeDTO upgradeSafeDTO;//升级保险

    private BigDecimal totalPrice;//总价
    private BigDecimal discountsPrice;//优惠总价
    private BigDecimal payPrice;//应付
    private int discounts;//1有优惠  0没有
    private String businessOrderNumber;//订单号
    private String houseId;
    private String taskId;
    private String info;//支付温馨提示用于
    private int type;
}
