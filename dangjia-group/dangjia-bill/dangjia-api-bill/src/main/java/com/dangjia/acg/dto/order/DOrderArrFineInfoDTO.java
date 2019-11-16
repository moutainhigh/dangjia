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

    @ApiModelProperty("房子名称")
    private String houseName;
    @ApiModelProperty("实付总价")
    private BigDecimal actualPaymentPrice;
    @ApiModelProperty("订单号")
    private String orderNumber;
    @ApiModelProperty("订单支付时间")
    private Date orderPayTime;

    @ApiModelProperty("订单详情")
    PageInfo list;

}
