package com.dangjia.acg.modle.supplier;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/11/2019
 * Time: 下午 4:04
 */
@Data
@Entity
@Table(name = "dj_supplier_pay_order")
@ApiModel(description = "供应商支付流水表")
@FieldNameConstants(prefix = "")
public class DjSupplierPayOrder extends BaseEntity {


    @Column(name = "business_order_type")
    @Desc(value = "订单类型：1充值 2交纳滞留金")
    @ApiModelProperty("订单类型：1充值 2交纳滞留金")
    private String businessOrderType;

    @Column(name = "supplier_id")
    @Desc(value = "供应商Id/店铺id")
    @ApiModelProperty("供应商Id/店铺id")
    private String supplierId;

    @Column(name = "pay_state")
    @Desc(value = "1微信，2支付宝")
    @ApiModelProperty("1微信，2支付宝")
    private String payState;

    @Column(name = "price")
    @Desc(value = "该笔支付钱")
    @ApiModelProperty("该笔支付钱")
    private Double price;

    @Column(name = "state")
    @Desc(value = "支付状态：0申请中,1已支付,2支付失败")
    @ApiModelProperty("支付状态：0申请中,1已支付,2支付失败")
    private Integer state;

    @Column(name = "user_id")
    @Desc(value = "申请人id")
    @ApiModelProperty("申请人id")
    private String userId;
}
