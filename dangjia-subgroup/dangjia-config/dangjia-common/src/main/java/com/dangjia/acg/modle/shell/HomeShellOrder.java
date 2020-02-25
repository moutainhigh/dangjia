package com.dangjia.acg.modle.shell;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 2020/2/25
 * Time: 13:56
 */
@Data
@Entity
@Table(name = "dj_home_shell_order")
@ApiModel(description = "兑换记录订单")
@FieldNameConstants(prefix = "")
public class HomeShellOrder extends GoodsBaseEntity {

    @Column(name = "number")
    @Desc(value = "兑换单号")
    @ApiModelProperty("兑换单号")
    private String number;

    @Column(name = "product_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品Id")
    private String prouctId;

    @Column(name = "product_spec_id")
    @Desc(value = "商品规格ID")
    @ApiModelProperty("商品规格ID")
    private String productSpecId;


    @Column(name = "exchange_time")
    @Desc(value = "兑换时间")
    @ApiModelProperty("兑换时间")
    private Date exchangeTime;

    @Column(name = "exchange_client")
    @Desc(value = "兑换客户端 1业主端，2工匠端")
    @ApiModelProperty("兑换客户端 1业主端，2工匠端")
    private Integer exchangeClient;

    @Column(name = "member_id")
    @Desc(value = "兑换人ID")
    @ApiModelProperty("兑换人ID")
    private String memberId;

    @Column(name = "member_name")
    @Desc(value = "兑换人姓名")
    @ApiModelProperty("兑换人姓名")
    private String memberName;

    @Column(name = "member_mobile")
    @Desc(value = "兑换人电话")
    @ApiModelProperty("兑换人电话")
    private String memberMobile;


    @Column(name = "status")
    @Desc(value = "订单状态（0待付款，1待发货，2待收货，3已收货，4待退款，5已退款）")
    @ApiModelProperty("订单状态（0待付款，1待发货，2待收货，3已收货，4待退款，5已退款）")
    private Integer status;

    @Column(name = "address_id")
    @Desc(value = "收货地址ID")
    @ApiModelProperty("收货地址ID")
    private String addressId;

    @Column(name = "address")
    @Desc(value = "收货地址")
    @ApiModelProperty("收货地址")
    private String address;

    @Column(name = "exchange_num")
    @Desc(value = "兑换数量")
    @ApiModelProperty("兑换数量")
    private Double exchangeNum;

    @Column(name = "integral")
    @Desc(value = "所需积分（贝币）")
    @ApiModelProperty("所需积分（贝币）")
    private Double integral;

    @Column(name = "money")
    @Desc(value = "所需金额")
    @ApiModelProperty("所需金额")
    private Double money;

    @Column(name = "image")
    @Desc(value = "相关凭证图片")
    @ApiModelProperty("相关凭证图片")
    private String image;

    @Column(name = "deliver_time")
    @Desc(value = "发货时间")
    @ApiModelProperty("发货时间")
    private Date deliverTime;//

    @Column(name = "reveive_time")
    @Desc(value = "收货时间")
    @ApiModelProperty("收货时间")
    private Date reveiveTime;//

    @Column(name = "return_application_time")
    @Desc(value = "退货申请时间")
    @ApiModelProperty("退货申请时间")
    private Date returnApplicationTime;//

    @Column(name = "cancel_application_time")
    @Desc(value = "撤销申请时间")
    @ApiModelProperty("撤销申请时间")
    private Date cancelApplicationTime;//

    @Column(name = "refund_time")
    @Desc(value = "退款时间")
    @ApiModelProperty("退款时间")
    private Date refundTime;//


}
