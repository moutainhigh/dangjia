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
@Table(name = "dj_area_match_setup")
@ApiModel(description = "提成楼栋详情表")
@FieldNameConstants(prefix = "")
public class DjAreaMatchSetup extends BaseEntity {

    @Column(name = "resource_id")
    @Desc(value = "关联提成总表id")
    @ApiModelProperty("关联提成总表id")
    private String resourceId;

    @Column(name = "start_single")
    @Desc(value = "开始单")
    @ApiModelProperty("开始单")
    private Integer startSingle;

    @Column(name = "over_single")
    @Desc(value = "结束单")
    @ApiModelProperty("结束单")
    private Integer overSingle;

    @Column(name = "royalty")
    @Desc(value = "每单提成")
    @ApiModelProperty("每单提成")
    private Integer royalty;

    @Column(name = "village_id")
    @Desc(value = "小区id")
    @ApiModelProperty("小区id")
    private String villageId;

    @Column(name = "building_id")
    @Desc(value = "楼栋id")
    @ApiModelProperty("楼栋id")
    private String buildingId;

}
