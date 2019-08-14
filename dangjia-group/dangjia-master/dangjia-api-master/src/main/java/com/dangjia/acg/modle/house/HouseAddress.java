package com.dangjia.acg.modle.house;

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
 * 实体类 - 房间
 */
@Data
@Entity
@Table(name = "dj_house_address")
@ApiModel(description = "用户选择房子位置保存")
@FieldNameConstants(prefix = "")
public class HouseAddress extends BaseEntity {

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "latitude")
    @Desc(value = "纬度")
    @ApiModelProperty("纬度")
    private String latitude;

    @Column(name = "longitude")
    @Desc(value = "经度")
    @ApiModelProperty("经度")
    private String longitude;

    @Column(name = "address")
    @Desc(value = "地址")
    @ApiModelProperty("地址")
    private String address;

    @Column(name = "name")
    @Desc(value = "地址名称")
    @ApiModelProperty("地址名称")
    private String name;

}