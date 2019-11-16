package com.dangjia.acg.dto.order;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 15/11/2019
 */

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class DOrderArrFineInfoDTO {

    @ApiModelProperty("业务订单id")
    private String pboId;
    @ApiModelProperty("房子名称")
    private String houseName;
    @ApiModelProperty("实付总价")
    private BigDecimal actualPaymentPrice;
    @ApiModelProperty("订单号")
    private String orderNumber;
    @ApiModelProperty("订单支付时间")
    private Date orderPayTime;

    @ApiModelProperty("支付状态，0- 未支付 1-已支付")
    private Integer state;

    @ApiModelProperty("回执图片")
    private List<String> imageList;


    @ApiModelProperty("订单详情")
    PageInfo list;

}
