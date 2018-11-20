package com.dangjia.acg.modle.config;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - APP版本表
 */
@Data
@Entity
@Table(name = "dj_config_app_logo")
@ApiModel(description = "APP版本表")
public class ConfigAppLogo extends BaseEntity {

    @Column(name = "app_type")
    @Desc(value = "来源应用（1:业主端，2:工匠端）")
    @ApiModelProperty("来源应用（1:业主端，2:工匠端）")
    private String appType;

    @Column(name = "type")
    @Desc(value = "动作类型（0:默认，1:周年庆，2: 双十一，3:双十二）")
    @ApiModelProperty("动作类型（0:默认，1:周年庆，2: 双十一，3:双十二）")
    private Integer type;

    @Column(name = "is_switch")
    @Desc(value = "开关（0:开启，1:关闭）")
    @ApiModelProperty("开关（0:开启，1:关闭）")
    private boolean isSwitch;

    @Column(name = "data")
    @Desc(value = "动作内容（暂时保留，冗余字段）")
    @ApiModelProperty("动作内容（暂时保留，冗余字段）")
    private String data;


}