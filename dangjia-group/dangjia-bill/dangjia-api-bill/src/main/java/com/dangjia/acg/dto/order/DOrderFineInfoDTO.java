package com.dangjia.acg.dto.order;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 15/11/2019
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
public class DOrderFineInfoDTO {

    @ApiModelProperty("商品图片")
    private String image;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品编码")
    private String productSn;

    @ApiModelProperty("销售价")
    private Double price;

    @ApiModelProperty("购买总数")
    private Double shopCount;

    @ApiModelProperty("要货数")
    private Double askCount;

    @ApiModelProperty("退货数")
    private Double returnCount;

    @ApiModelProperty("剩余数量")
    private Double remnantCount;

    private Double stevedorageCost;//搬运费

    private Double transportationCost;//运费

    @ApiModelProperty("小计")
    private Double remember;


}
