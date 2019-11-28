package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "dj_repair_sale_order")
@ApiModel(description = "购物车-销售订单表")
@FieldNameConstants(prefix = "")
public class SaleOrder extends BaseEntity {
    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "business_order_number")
    @Desc(value = "业务订单号")
    @ApiModelProperty("业务订单号")
    private String businessOrderNumber;

    @Column(name = "total_amount")
    @Desc(value = "订单总额")
    @ApiModelProperty("订单总额")
    private BigDecimal totalAmount;

    @Column(name = "payment")
    @Desc(value = "支付方式1微信, 2支付宝,3后台回调")
    @ApiModelProperty("支付方式1微信, 2支付宝,3后台回调")
    private String payment;

    @Column(name = "type")
    @Desc(value = "1:人工订单 2:材料订单 10:购物车购物")
    @ApiModelProperty("1:人工订单 2:材料订单 10:购物车购物")
    private String type;
}
