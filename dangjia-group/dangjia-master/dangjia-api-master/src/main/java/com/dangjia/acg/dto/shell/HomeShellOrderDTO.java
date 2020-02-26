package com.dangjia.acg.dto.shell;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 2020-02-25
 * Time: 下午 5:18
 */
@Data
public class HomeShellOrderDTO extends GoodsBaseEntity {

    @ApiModelProperty("兑换单ID")
    private String shellOrderId;

    @ApiModelProperty("兑换单号")
    private String number;

    @ApiModelProperty("商品Id")
    private String prouctId;

    @ApiModelProperty("兑换时间")
    private Date exchangeTime;

    @ApiModelProperty("兑换客户端 1业主端，2工匠端")
    private Integer exchangeClient;

    @ApiModelProperty("兑换人ID")
    private String memberId;

    @ApiModelProperty("兑换人姓名")
    private String memberName;

    @ApiModelProperty("兑换人电话")
    private String memberMobile;

    @ApiModelProperty("订单状态（0待付款，1待发货，2待收货，3已收货，4待退款，5已退款）")
    private Integer status;

    @ApiModelProperty("收货地址ID")
    private String addressId;

    @ApiModelProperty("收货地址")
    private String address;

    @ApiModelProperty("收货人姓名")
    private String reveiveMemberName;

    @ApiModelProperty("收货人电话")
    private String reveiveMemberMobile;

    @ApiModelProperty("兑换数量")
    private Double exchangeNum;

    @ApiModelProperty("所需积分（贝币）")
    private Double integral;

    @ApiModelProperty("所需金额")
    private Double money;

    @ApiModelProperty("相关凭证图片")
    private String image;

    private String imageUrl;


    private String productName;//商品名称

    private String productType;//所属分类，1实物商品，2虚拟商品

    private String productSn;//商品编码

    private String productSpecName;//规格名称

    private String  productSpecId;//商品规格ID

}
