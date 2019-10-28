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
 * Date: 10/10/2019
 * Time: 下午 3:20
 */
@Data
@Entity
@Table(name = "dj_sup_application")
@ApiModel(description = "供应店铺关系单表")
@FieldNameConstants(prefix = "")
public class DjSupApplication extends BaseEntity {

    @Column(name = "sup_id")
    @Desc(value = "供应商id")
    @ApiModelProperty("供应商id")
    private String supId;

    @Column(name = "shop_id")
    @Desc(value = "店铺id")
    @ApiModelProperty("店铺id")
    private String shopId;

    @Column(name = "application_status")
    @Desc(value = "申请状态 0:审核中 1:通过 2:不通过")
    @ApiModelProperty("申请状态 0:审核中 1:通过 2:不通过")
    private String applicationStatus;

    @Column(name = "fail_reason")
    @Desc(value = "失败原因")
    @ApiModelProperty("失败原因")
    private String failReason;

    @Column(name = "contract")
    @Desc(value = "合同地址")
    @ApiModelProperty("合同地址")
    private String contract;

    @Column(name = "city_id")
    @Desc(value = "城市ID")
    @ApiModelProperty("城市ID")
    private String cityId;//城市ID


}
