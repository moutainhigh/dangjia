package com.dangjia.acg.dto.order;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 15/11/2019
 */
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
@Data
public class DOrderInfoDTO {

    @ApiModelProperty("订单id")
    private String id;
    @ApiModelProperty("房子名称")
    private String houseName;
    @ApiModelProperty("手机号码")
    private String mobile;
    @ApiModelProperty("门店名称")
    private String storefrontName;
    @ApiModelProperty("订单号")
    private String orderNumber;
    @ApiModelProperty("订单总额")
    private BigDecimal totalAmount;
    @ApiModelProperty("实付总价")
    private BigDecimal actualPaymentPrice;
    @ApiModelProperty("创建时间")
    private Date createDate;
    @ApiModelProperty("支付方式1微信, 2支付宝,3后台回调")
    private String payment;
    @ApiModelProperty("订单生成时间")
    private Date orderGenerationTime;
    @ApiModelProperty("订单支付时间")
    private Date orderPayTime;
    @ApiModelProperty("处理状态  1刚生成(可编辑),2去支付(不修改),3已支付,4已取消")
    private Integer state;//
    @ApiModelProperty("支付订单号")
    private String payOrderNumber;//
    @ApiModelProperty("支付类型  1工序支付任务,2补货补人工 , 3,充值，4待付款进来只付材料, 5验房分销, 6换货单,7:设计精算补单,9:工人保险")
    private Integer type; // 1工序支付任务,2补货补人工 ,4待付款进来只付材料, 5验房分销, 6换货单


}
