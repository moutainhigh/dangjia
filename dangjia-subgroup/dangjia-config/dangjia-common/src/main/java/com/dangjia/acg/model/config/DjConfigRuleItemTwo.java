package com.dangjia.acg.model.config;

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
@Table(name = "dj_config_rule_item_two")
@ApiModel(description = "规则明细配置表2")
@FieldNameConstants(prefix = "")
public class DjConfigRuleItemTwo extends BaseEntity {

    @Column(name = "batch_code")
    @Desc(value = "批次号")
    @ApiModelProperty("用于每次配置做历史记录和使用，每次编辑一个批次号")
    private String batchCode;

    @Column(name = "field_name")
    @Desc(value = "字段名称")
    @ApiModelProperty("字段名称")
    private String fieldName;

    @Column(name = "field_value")
    @Desc(value = "字段值,多个逗号分隔")
    @ApiModelProperty("字段值,多个逗号分隔")
    private String fieldValue;

    @Column(name = "field_code")
    @Desc(value = "字段编号")
    @ApiModelProperty("字段编号")
    private String fieldCode;

    @Column(name = "module_id")
    @Desc(value = "模块配置配置表的ID")
    @ApiModelProperty("模块配置配置表的ID")
    private String moduleId;

}
