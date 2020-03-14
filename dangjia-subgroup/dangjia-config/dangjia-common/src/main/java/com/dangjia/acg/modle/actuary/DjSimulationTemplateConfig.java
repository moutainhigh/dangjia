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
@Table(name = "dj_simulation_template_config")
@ApiModel(description = "精算模拟模板表")
@FieldNameConstants(prefix = "")
public class DjSimulationTemplateConfig extends GoodsBaseEntity {

    @Column(name = "config_name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String configName;

    @Column(name = "config_type")
    @Desc(value = "模板类型：A图片和文字，B仅图片，C仅文字")
    @ApiModelProperty("模板类型：A图片和文字，B仅图片，C仅文字")
    private String configType;

    @Column(name = "config_type_index")
    @Desc(value = "模板序号")
    @ApiModelProperty("模板序号")
    private Integer configTypeIndex;

    @Column(name = "service_type_id")
    @Desc(value = "服务类型ID")
    @ApiModelProperty("服务类型ID")
    private String serviceTypeId;

    @Column(name = "create_by")
    @Desc(value = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    @Column(name = "update_by")
    @Desc(value = "修改人")
    @ApiModelProperty("修改人")
    private String updateBy;


}
