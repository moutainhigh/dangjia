package com.dangjia.acg.dto.order;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 4/11/2019
 * Time: 上午 15:48
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DecorationCostDTO {


    @ApiModelProperty("类别ID")
    private String categoryId;


    @ApiModelProperty("类别名称")
    private String categoryName;

    @ApiModelProperty("工种ID")
    private String workerTypeId;


    @ApiModelProperty("工种名称")
    private String workerTypeName;

    @ApiModelProperty("类别标签ID")
    private String labelValId;


    @ApiModelProperty("类别标签名称")
    private String labelValName;


    @ApiModelProperty("精算总价")
    private Double totalPrice;


    @ApiModelProperty("当前装修花费")
    private Double actualPaymentPrice;


    @ApiModelProperty("自购商品花费")
    private Double purchaseTotalPrice;

    List<DecorationCostItemDTO> decorationCostItemList;//当前花费详情商品信息

}
