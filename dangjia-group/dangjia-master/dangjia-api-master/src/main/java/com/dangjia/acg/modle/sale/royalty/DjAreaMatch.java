package com.dangjia.acg.modle.sale.royalty;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "dj_area_match")
@ApiModel(description = "提成小区楼栋详情表")
@FieldNameConstants(prefix = "")
public class DjAreaMatch extends BaseEntity {

    @Column(name = "village_name")
    @Desc(value = "小区名称")
    @ApiModelProperty("小区名称")
    private String villageName;

    @Column(name = "village_id")
    @Desc(value = "小区id")
    @ApiModelProperty("小区id")
    private String villageId;

    @Column(name = "building_name")
    @Desc(value = "楼栋名称")
    @ApiModelProperty("楼栋名称")
    private String buildingName;

    @Column(name = "building_id")
    @Desc(value = "楼栋id")
    @ApiModelProperty("楼栋id")
    private String buildingId;

    @Column(name = "vbName")
    @Desc(value = "小区楼栋名称")
    @ApiModelProperty("小区楼栋名称")
    private String vbName;

}
