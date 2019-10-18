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
@Table(name = "dj_actuarial_template_config")
@ApiModel(description = "设计阶段模板配置")
@FieldNameConstants(prefix = "")
public class DjActuarialTemplateConfig extends BaseEntity {

    @Column(name = "config_name")
    @Desc(value = "阶段名称")
    @ApiModelProperty("阶段名称")
    private String configName;

    @Column(name = "config_type")
    @Desc(value = "配置类型1：设计阶段 2：精算阶段 3：施工阶段")
    @ApiModelProperty("配置类型1：设计阶段 2：精算阶段 3：施工阶段")
    private String configType;

    @Column(name = "excel_address")
    @Desc(value = "精算Excel地址")
    @ApiModelProperty("精算Excel地址")
    private String excelAddress;

    @Column(name = "excel_file_name")
    @Desc(value = "精算Excel上传后的文件名")
    @ApiModelProperty("精算Excel上传后的文件名")
    private String excelFileName;

    @Column(name = "create_by")
    @Desc(value = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    @Column(name = "update_by")
    @Desc(value = "修改人")
    @ApiModelProperty("修改人")
    private String updateBy;


}
