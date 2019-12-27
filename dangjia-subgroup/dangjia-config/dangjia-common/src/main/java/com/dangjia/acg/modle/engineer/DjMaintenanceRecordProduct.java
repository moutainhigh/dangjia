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

}
