package com.dangjia.acg.dto.design;

import lombok.Data;

/**
 * @author Ruking.Cheng
 * @descrilbe 设计精算支付返回体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/5/28 6:06 PM
 */
@Data
public class DesignPayDTO {
    private String message;//头部提示信息
    private String butName;//协议名称
    private String butUrl;//协议地址
    private String moneyMessage;//金额描叙
    private String businessOrderNumber;//订单ID
    private int type;
}

