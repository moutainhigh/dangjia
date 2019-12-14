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
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "dj_config_rule_module")
@ApiModel(description = "模块配置配置表")
@FieldNameConstants(prefix = "")
public class DjConfigRuleModule extends BaseEntity {

    @Column(name = "type")
    @Desc(value = "规则模块类型： 1=积分规则 2=拿钱规则  3=抢单规则 4=其他规则 5=排期配置")
    @ApiModelProperty("规则模块类型：1=积分规则 2=拿钱规则   3=抢单规则 4=其他规则 5=排期配置")
    private Integer type;

    @Column(name = "type_id")
    @Desc(value = "种类类型ID(dj_config_rule_type)")
    @ApiModelProperty("种类类型ID")
    private String typeId;

    @Column(name = "type_name")
    @Desc(value = "类型名称")
    @ApiModelProperty("类型名称")
    private String typeName;

    @Column(name = "item_type")
    @Desc(value = "模块类型使用字段")
    @ApiModelProperty("模块类型使用字段：1=dj_config_rule_item_one  2=dj_config_rule_item_two   3=dj_config_rule_item_three")
    private Integer itemType;


    @Transient
    private List<Map> types;

}
