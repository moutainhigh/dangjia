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
@Table(name = "dj_config_rule_rank")
@ApiModel(description = "等级配置表")
@FieldNameConstants(prefix = "")
public class DjConfigRuleRank extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "score_start")
    @Desc(value = "开始分")
    @ApiModelProperty("开始分")
    private Double scoreStart;

    @Column(name = "score_end")
    @Desc(value = "结束分")
    @ApiModelProperty("结束分")
    private Double scoreEnd;

}
