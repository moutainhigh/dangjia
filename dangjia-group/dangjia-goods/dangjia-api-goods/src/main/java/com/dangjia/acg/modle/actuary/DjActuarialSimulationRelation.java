package com.dangjia.acg.modle.actuary;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
@Entity
@Table(name = "dj_actuarial_simulation_relation")
@ApiModel(description = "花费和精算模板关系表")
@FieldNameConstants(prefix = "")
public class DjActuarialSimulationRelation extends GoodsBaseEntity {

    @Column(name = "actuarialTemplateId")
    @Desc(value = "阶段模板ID(施工阶段的模板）")
    @ApiModelProperty("阶段模板ID(施工阶段的模板）")
    private String actuarialTemplateId;

    @Column(name = "simulation_code_group")
    @Desc(value = "花费模板编码组合")
    @ApiModelProperty("花费模板编码组合")
    private String simulationCodeGroup;

    @Column(name = "simulation_name_group")
    @Desc(value = "花费模板编码组合")
    @ApiModelProperty("花费模板编码组合")
    private String simulationNameGroup;


    @Column(name = "create_by")
    @Desc(value = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    @Column(name = "update_by")
    @Desc(value = "修改人")
    @ApiModelProperty("修改人")
    private String updateBy;


}
