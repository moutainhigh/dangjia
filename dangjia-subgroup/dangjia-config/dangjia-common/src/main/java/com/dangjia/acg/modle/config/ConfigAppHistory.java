package com.dangjia.acg.modle.config;

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
 * 实体类 - APP版本更新配置表
 */
@Data
@Entity
@Table(name = "dj_config_app_history")
@ApiModel(description = "APP版本更新配置表")
@FieldNameConstants(prefix = "")
public class ConfigAppHistory extends BaseEntity {

    @Column(name = "app_id")
    @Desc(value = "外键ID")
    @ApiModelProperty("外键ID")
    String appId;

    @Column(name = "is_forced")
    @Desc(value = "是否强制（0：是；1：否）")
    @ApiModelProperty("是否强制（0：是；1：否）")
    Boolean isForced;

    @Column(name = "history_id")
    @Desc(value = "历史应用版本ID")
    @ApiModelProperty("历史应用版本ID")
    String historyId;

    @Column(name = "version_code")
    @Desc(value = "历史应用版本号Code")
    @ApiModelProperty("历史应用版本号Code")
    String versionCode;


}