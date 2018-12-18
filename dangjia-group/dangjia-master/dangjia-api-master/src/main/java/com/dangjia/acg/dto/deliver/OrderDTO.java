package com.dangjia.acg.dto.deliver;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/4 0004
 * Time: 11:42
 */
@Data
public class OrderDTO {

    @ApiModelProperty("订单ID")
    private String id;
    @ApiModelProperty("订单名字")
    private String name;
}
