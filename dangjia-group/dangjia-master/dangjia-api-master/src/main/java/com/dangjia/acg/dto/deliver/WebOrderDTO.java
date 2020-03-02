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
    private String id;
    private String orderId;//订单号
    private String houseName;//房屋信息
    private String memberId;//用户id
    private String mobile;//电话
    private String payOrderNumber;//支付单号(业务订单号)
    private BigDecimal totalAmount;//订单总价
    private BigDecimal redPackAmount;//优惠金额
    private BigDecimal actualPayment;//实付 （精算价格）
    private String redPackName;//优惠券名字
    private String redPackNumber;//优惠券号
    private String redPackType;//优惠券类型
    private String image;//回执图片
    private Date createDate;//支付日期
    private Integer type;// 1工序支付任务,2补货补人工 ,4待付款进来只付材料, 5验房分销
    private String typeText; // 1工序支付任务,2补货补人工 ,4待付款进来只付材料, 5验房分销
    private Integer state;//处理状态  1刚生成(可编辑),2去支付(不修改),3已支付
    private Integer payType;//支付类型1微信，2支付宝
    private String taskId;//工序支付   补货补人工  提前付
    private String storeName;// 归属分店
    private String storefrontId;//店铺ID
    private String redPackId;//优惠券ID
}
