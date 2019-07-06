package com.dangjia.acg.modle.store;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/14
 * Time: 16:05
 */
@Data
@Entity
@Table(name = "dj_store")
@ApiModel(description = "门店表")
@FieldNameConstants(prefix = "")
public class Store extends BaseEntity {

    @Column(name = "store_name")
    @Desc(value = "门店名称")
    @ApiModelProperty("门店名称")
    private String storeName;

    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;

    @Column(name = "city_name")
    @Desc(value = "门店所在区域/城市名称")
    @ApiModelProperty("门店所在区域/城市名称")
    private String cityName;

    @Column(name = "store_address")
    @Desc(value = "门店地址")
    @ApiModelProperty("门店地址")
    private String storeAddress;

    @Column(name = "reservation_number")
    @Desc(value = "门店预约号码")
    @ApiModelProperty("门店预约号码")
    private String reservationNumber;

    @Column(name = "department_id")
    @Desc(value = "部门ID")
    @ApiModelProperty("部门ID")
    private String departmentId;

    @Column(name = "latitude")
    @Desc(value = "门店纬度")
    @ApiModelProperty("门店纬度")
    private String latitude;

    @Column(name = "longitude")
    @Desc(value = "门店经度")
    @ApiModelProperty("门店经度")
    private String longitude;


    @Column(name = "scope_itude")
    @Desc(value = "门店范围")
    @ApiModelProperty("门店范围")
    private String scopeItude;

    @Transient
    private Integer juli;//门店距离


}
