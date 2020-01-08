package com.dangjia.acg.modle.engineer;

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
 * Date: 13/12/2019
 * Time: 上午 9:32
 */
@Data
@Entity
@Table(name = "dj_maintenance_record_product")
@FieldNameConstants(prefix = "")
@ApiModel(description = "维保商品记录")
public class DjMaintenanceRecordProduct extends BaseEntity {

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "product_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String productId;

    @Column(name = "maintenance_record_id")
    @Desc(value = "维保记录表id")
    @ApiModelProperty("维保记录表id")
    private String maintenanceRecordId;

    @Column(name = "shop_count")
    @Desc(value = "数量")
    @ApiModelProperty("数量")
    private Double shopCount;

    @Column(name = "price")
    @Desc(value = "单价")
    @ApiModelProperty("单价")
    private Double price;

    @Column(name = "worker_type_id")
    @Desc(value = "工种ID")
    @ApiModelProperty("工种ID")
    private String workerTypeId;

    @Column(name = "maintenance_member_id")
    @Desc(value = "维护人ID")
    @ApiModelProperty("维护人ID")
    private String maintenanceMemberId;

    @Column(name = "maintenance_member_type")
    @Desc(value = "维护人类型：1业主；2大管家；3工匠")
    @ApiModelProperty("维护人类型：1业主；2大管家；3工匠；4系统（勘查费用商品）")
    private Integer  maintenanceMemberType;

    @Column(name = "total_price")
    @Desc(value = "商品总价")
    @ApiModelProperty("商品总价")
    private Double totalPrice;

    @Column(name = "pay_price")
    @Desc(value = "需支付价钱")
    @ApiModelProperty("需支付价钱")
    private Double payPrice;

    @Column(name = "over_protection")
    @Desc(value = "是否过保商品（1是，0否）")
    @ApiModelProperty("是否过保商品（1是，0否）")
    private Integer overProtection;

    @Column(name = "pay_state")
    @Desc(value = "是否已付费（1未支付，2已支付）")
    @ApiModelProperty("是否已付费（1未支付，2已支付）")
    private Integer payState;

    @Column(name = "business_order_number")
    @Desc(value = "业务支付单号")
    @ApiModelProperty("业务支付单号")
    private String businessOrderNumber;
}
