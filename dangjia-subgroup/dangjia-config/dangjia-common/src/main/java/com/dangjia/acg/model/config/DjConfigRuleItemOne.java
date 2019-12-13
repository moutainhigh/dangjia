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
import javax.persistence.Transient;

@Data
@Entity
@Table(name = "dj_config_rule_item_one")
@ApiModel(description = "规则明细配置表4")
@FieldNameConstants(prefix = "")
public class DjConfigRuleItemOne extends BaseEntity {

    @Column(name = "module_id")
    @Desc(value = "模块配置配置表的ID")
    @ApiModelProperty("模块配置配置表的ID")
    private String moduleId;

    @Column(name = "batch_code")
    @Desc(value = "批次号")
    @ApiModelProperty("用于每次配置做历史记录和使用，每次编辑一个批次号")
    private String batchCode;

    @Column(name = "type_id")
    @Desc(value = "工种种类别ID，或规则配置类型表的ID，中的施工类别ID(周计划、巡查、验收、竣工、每日完工、被评价)")
    @ApiModelProperty("工种种类别ID，或规则配置类型表的ID，中的施工类别ID(周计划、巡查、验收、竣工、每日完工、被评价)")
    private String typeId;

    @Column(name = "rank_id")
    @Desc(value = "等级配置表的ID")
    @ApiModelProperty("等级配置表的ID")
    private String rankId;

    @Column(name = "rule_field_name")
    @Desc(value = "规则字段名称")
    @ApiModelProperty("规则字段名称")
    private String ruleFieldName;

    @Column(name = "rule_field_value")
    @Desc(value = "规则字段名称值")
    @ApiModelProperty("规则字段名称值")
    private String ruleFieldValue;

    @Column(name = "rule_field_code")
    @Desc(value = "规则字段编号")
    @ApiModelProperty("规则字段编号")
    private String ruleFieldCode;

    @Transient
    private String rankName;

}
