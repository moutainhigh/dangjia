package com.dangjia.acg.dto.refund;

/**
 * Created with IntelliJ IDEA.
 * author: Fzh
 * Date: 28/11/2019
 * Time: 上午 17:00
 */

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeliverOrderAddedProductDTO extends BaseEntity {

    @ApiModelProperty("流水ID")
    private String id;

    @ApiModelProperty("任意订单号")
    private String anyOrderId;


    @ApiModelProperty("增值商品ID")
    private String addedProductId;


    @ApiModelProperty("增值商品名称")
    private String productName;

    @ApiModelProperty("商品单价")
    private Double price;


    @ApiModelProperty("数据来源（1订单详情ID，2发货单详情ID，3退货单详情ID,4购物车ID，5其它）")
    private String source;

}
