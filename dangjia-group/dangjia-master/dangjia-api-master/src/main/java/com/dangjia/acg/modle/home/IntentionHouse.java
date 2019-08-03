package com.dangjia.acg.modle.home;

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
@FieldNameConstants(prefix = "")
@Table(name = "dj_intention_house")
@ApiModel(description = "意向房子")
public class IntentionHouse extends BaseEntity {

    @Column(name = "residential_name")
    @Desc(value = "小区名称")
    @ApiModelProperty("小区名称")
    private String residentialName;

    @Column(name = "building_name")
    @Desc(value = "楼栋名称")
    @ApiModelProperty("楼栋名称")
    private String buildingName;

    @Column(name = "number_name")
    @Desc(value = "小区名称")
    @ApiModelProperty("小区名称")
    private String numberName;

    @Column(name = "clue_id")
    @Desc(value = "线索id")
    @ApiModelProperty("线索id")
    private String clueId;


}
