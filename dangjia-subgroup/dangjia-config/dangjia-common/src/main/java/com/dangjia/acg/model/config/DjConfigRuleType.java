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
@Table(name = "dj_config_rule_type")
@ApiModel(description = "规则配置类型表")
@FieldNameConstants(prefix = "")
public class DjConfigRuleType extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "种类名称")
    @ApiModelProperty("种类名称")
    private String name;

    @Column(name = "source")
    @Desc(value = "类型对象：1=模块类型    2=参数类型  3=施工类型  4=施工排期类型")
    @ApiModelProperty("类型对象：1=模块类型    2=参数类型  3=施工类型 4=施工排期类型")
    private Double source;

}
