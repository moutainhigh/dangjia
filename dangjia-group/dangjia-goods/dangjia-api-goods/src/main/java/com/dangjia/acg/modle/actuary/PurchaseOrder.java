package com.dangjia.acg.modle.actuary;

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
 * @author Ruking.Cheng
 * @descrilbe 购买单
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/29 10:57 AM
 */
@Data
@Entity
@Table(name = "dj_purchase_order")
@ApiModel(description = "购买单")
@FieldNameConstants(prefix = "")
public class PurchaseOrder extends BaseEntity {

    @Column(name = "price")
    @Desc(value = "价格")
    @ApiModelProperty("价格")
    private Double price;//价格

    @Column(name = "budget_ids")
    @Desc(value = "选中的商品ID“,”分割")
    @ApiModelProperty("选中的商品ID“,”分割")
    private String budgetIds;

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "type")
    @Desc(value = "支付状态 0=未支付，1=已支付")
    @ApiModelProperty("支付状态 0=未支付，1=已支付")
    private Integer type;

}
