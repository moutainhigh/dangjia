package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * author: ysl
 * Date: 2018/1/24 0004
 * Time: 16:32
 * 订单流水
 */
@Data
public class WebOrderDTO {
    private String orderId;//订单号
    private String houseName;//房屋信息
    private String redPackName;//优惠券名字
    private String payOrderNumber;//支付单号(业务订单号)
    private BigDecimal totalAmount;//订单总价
    private BigDecimal redPackAmount;//优惠
    private BigDecimal actualPayment;//实付 （精算价格）
    private String state;//状态
    private Date createDate;//创建日期
    private Date modifyDate;//修改日期
}
