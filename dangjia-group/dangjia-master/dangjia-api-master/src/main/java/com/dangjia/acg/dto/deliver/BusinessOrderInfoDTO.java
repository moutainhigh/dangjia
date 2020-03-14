package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/27 0027
 * Time: 14:06
 */
@Data
public class BusinessOrderInfoDTO {
    private String businessOrderId;//业务订单id
    private String businessNumber;//业务订单id
    private String address;//地址
    private String memberName;//业主
    private String memberMobile;//联系方式
    private Integer elevator;//是否为电梯房 1是，0否
    private String floor;//楼层数
    private Date orderGenerationTime;//	下单时间
    private Double totalStevedorageSost;//总搬运费
    private Double totalTransportationSost;//总运费
    private Double totalPrice;//商品小计
    private Double totalAmount;//订单总价
    private Double actualPaymentPrice;//实付总价(应付总额)
    private String discountType;//优惠卷类型（1店铺，2平台）
    private String discountNumber;//优惠卷编号
    private String discountName;//优惠卷名称(方式)
    private String discountPrice;//优惠卷金额
    private Double totalDiscountPrice;//优惠金额
    private Integer state;//处理状态  1刚生成(可编辑),2去支付(不修改),3已支付,4已取
    private String payment;//支付方式1微信, 2支付宝,3后台回调
    private String houseId;//房子ID
    private String redPackId;//优惠券ID
    private Double redPackAmount;//优惠券ID

    List<OrderDTO> orderInfoList;//订单信息（按店铺划分)
}
