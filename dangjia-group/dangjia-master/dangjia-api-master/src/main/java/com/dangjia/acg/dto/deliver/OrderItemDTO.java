package com.dangjia.acg.dto.deliver;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/20 0020
 * Time: 16:38
 */
@Data
public class OrderItemDTO {
    private String orderId;//流水号
    private BigDecimal totalAmount;//该订单总价
    private List<ItemDTO> itemDTOList;


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

    @ApiModelProperty("小计(单价*数量）")
    private Double totalPrice;

    private Double stevedorageCost;//搬运费

    private Double transportationCost;//运费

    @ApiModelProperty("小计")
    private Double remember;
}
