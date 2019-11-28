package com.dangjia.acg.modle.delivery;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 10:00
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
@Table(name = "dj_delivery_return_slip_details")
@ApiModel(description = "发货退货单详情")
@FieldNameConstants(prefix = "")
public class DjDeliveryReturnSlipDetails extends BaseEntity {

    @Column(name = "delivery_return_slip_id")
    @Desc(value = "发货单ID")
    @ApiModelProperty("发货单ID")
    private String deliveryReturnSlipId;

    @Column(name = "storefront_product_id")
    @Desc(value = "店铺商品id")
    @ApiModelProperty("店铺商品id")
    private String storefrontProductId;

    @Column(name = "unit_price")
    @Desc(value = "商品单价")
    @ApiModelProperty("商品单价")
    private Double unitPrice;

    @Column(name = "quantity")
    @Desc(value = "数量")
    @ApiModelProperty("数量")
    private Integer quantity;

    @Column(name = "total_prices")
    @Desc(value = "发货商品总价")
    @ApiModelProperty("发货商品总价")
    private Double totalPrices;

    @Column(name = "invoice_status")
    @Desc(value = "货单状态：0待发货；1待收货；2已收货；3待退货；4已退货；5:拒绝退货")
    @ApiModelProperty("货单状态：0待发货；1待收货；2已收货；3待退货；4已退货；5:拒绝退货")
    private Double invoiceStatus;
}
