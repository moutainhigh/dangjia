package com.dangjia.acg.modle.order;

/**
 * Created with IntelliJ IDEA.
 * author: Fzh
 * Date: 28/11/2019
 * Time: 上午 17:00
 */

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dj_deliver_order_added_product")
@ApiModel(description = "订单增值商品信息")
@FieldNameConstants(prefix = "")
public class DeliverOrderAddedProduct extends BaseEntity {

    @Column(name = "any_order_id")
    @Desc(value = "任意订单号")
    @ApiModelProperty("任意订单号")
    private String anyOrderId;

    @Column(name = "added_product_id")
    @Desc(value = "增值商品ID")
    @ApiModelProperty("增值商品ID")
    private String addedProductId;

    @Column(name = "product_name")
    @Desc(value = "增值商品名称")
    @ApiModelProperty("增值商品名称")
    private String productName;

    @Column(name = "price")
    @Desc(value = "商品单价")
    @ApiModelProperty("商品单价")
    private Double price;

    @Column(name = "source")
    @Desc(value = "数据来源（1订单详情ID，2发货单详情ID，3退货单详情ID,4购物车ID，5其它）")
    @ApiModelProperty("数据来源（1订单详情ID，2发货单详情ID，3退货单详情ID,4购物车ID，5其它）")
    private String source;

}
