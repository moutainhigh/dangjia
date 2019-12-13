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
@Table(name = "dj_config_rule_item_ladder")
@ApiModel(description = "规则明细配置表4")
@FieldNameConstants(prefix = "")
public class DjConfigRuleItemLadder  extends BaseEntity {

    @Column(name = "item_three_id")
    @Desc(value = "规则明细配置表3(dj_config_rule_item_three)的ID")
    @ApiModelProperty("规则明细配置表3(dj_config_rule_item_three)的ID")
    private String itemThreeId;

    @Column(name = "phase_start")
    @Desc(value = "阶段开始数")
    @ApiModelProperty("阶段开始数")
    private Double phaseStart;

    @Column(name = "phase_end")
    @Desc(value = "阶段结束数")
    @ApiModelProperty("阶段结束数")
    private Double phaseEnd;

    @Column(name = "fraction")
    @Desc(value = "参考分")
    @ApiModelProperty("参考分")
    private Double fraction;
}
