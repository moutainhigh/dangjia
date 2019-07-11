package com.dangjia.acg.common.model;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by QiYuXiang
 */

@Data
@MappedSuperclass
@FieldNameConstants(prefix = "")
public class PolygonBase implements Serializable {

    @Column(name = "locationx")
    @Desc(value = "经度")
    @ApiModelProperty("经度")
    private double locationx;//

    @Column(name = "locationy")
    @Desc(value = "纬度")
    @ApiModelProperty("纬度")
    private double locationy;//

}
