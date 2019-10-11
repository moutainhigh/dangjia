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
 * Date: 11/10/2019
 * Time: 下午 4:14
 */
@Data
@Entity
@Table(name = "dj_adjust_record")
@ApiModel(description = "调价记录表")
@FieldNameConstants(prefix = "")
public class DjAdjustRecord extends BaseEntity {

    @Column(name = "application_product_id")
    @Desc(value = "供应商申请商品表id")
    @ApiModelProperty("供应商申请商品表id")
    private String applicationProductId;

    @Column(name = "user_id")
    @Desc(value = "操作人")
    @ApiModelProperty("操作人")
    private String userId;

    @Column(name = "adjust_time")
    @Desc(value = "申请状态 0:审核中 1:通过 2:不通过")
    @ApiModelProperty("申请状态 0:审核中 1:通过 2:不通过")
    private String adjustTime;

    @Column(name = "adjust_price")
    @Desc(value = "调后价")
    @ApiModelProperty("调后价")
    private String adjustPrice;

}
