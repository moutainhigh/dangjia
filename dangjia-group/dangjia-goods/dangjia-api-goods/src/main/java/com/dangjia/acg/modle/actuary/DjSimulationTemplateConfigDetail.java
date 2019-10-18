package com.dangjia.acg.modle.actuary;

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
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
@Entity
@Table(name = "dj_simulation_template_config_detail")
@ApiModel(description = "施工模拟花费模板详情配置表")
@FieldNameConstants(prefix = "")
public class DjSimulationTemplateConfigDetail extends BaseEntity {

    @Column(name = "simulation_template_id")
    @Desc(value = "花费模板ID")
    @ApiModelProperty("花费模板ID")
    private String simulationTemplateId;

    @Column(name = "code")
    @Desc(value = "编码")
    @ApiModelProperty("编码")
    private String code;

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String image;

    @Column(name = "label_name")
    @Desc(value = "标签名称，多个用逗号分隔")
    @ApiModelProperty("标签名称，多个用逗号分隔")
    private String labelName;

    @Column(name = "config_status")
    @Desc(value = "配置状态(1显示，0不显示）")
    @ApiModelProperty("配置状态(1显示，0不显示）")
    private Integer configStatus;

    @Column(name = "template_detail_index")
    @Desc(value = "模板序号，按每个模板类加的")
    @ApiModelProperty("模板序号，按每个模板类加的")
    private Integer templateDetailIndex;

    @Column(name = "create_by")
    @Desc(value = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    @Column(name = "update_by")
    @Desc(value = "修改人")
    @ApiModelProperty("修改人")
    private String updateBy;


}
