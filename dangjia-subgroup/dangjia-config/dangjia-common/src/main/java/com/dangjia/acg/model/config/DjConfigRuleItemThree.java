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

@Data
@Entity
@Table(name = "dj_config_rule_item_three")
@ApiModel(description = "规则明细配置表3")
@FieldNameConstants(prefix = "")
public class DjConfigRuleItemThree extends BaseEntity {

    @Column(name = "batch_code")
    @Desc(value = "批次号")
    @ApiModelProperty("用于每次配置做历史记录和使用，每次编辑一个批次号")
    private String batchCode;

    @Column(name = "param_weight")
    @Desc(value = "参数权重比例/%")
    @ApiModelProperty("参数权重比例/%")
    private Double paramWeight;

    @Column(name = "param_type")
    @Desc(value = "规则配置类型表的ID\\r\\n            参数类型:1=店铺总销量  2=店铺上货数  3=商品点击率  4=商品收藏数  5=店铺的订单量  6=地域  7=评分  8=持单上线\\r\\n")
    @ApiModelProperty("规则配置类型表的ID\\r\\n            参数类型:1=店铺总销量  2=店铺上货数  3=商品点击率  4=商品收藏数  5=店铺的订单量  6=地域  7=评分  8=持单上线\\r\\n")
    private String paramType;

    @Column(name = "module_id")
    @Desc(value = "模块配置配置表的ID")
    @ApiModelProperty("模块配置配置表的ID")
    private String moduleId;


    @Transient
    private String paramName;

    @Transient
    private List<DjConfigRuleItemLadder> configRuleItemLadders;
}
