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
public class DOrderArrInfoDTO {
    @ApiModelProperty("未付款数量")
    private Integer noPaymentNumber;

    @ApiModelProperty("已付款数量")
    private Integer yesPaymentNumber;

    @ApiModelProperty("已取消数量")
    private Integer yesCancel;

    @ApiModelProperty("订单信息list")
    private PageInfo list;
}
