package com.dangjia.acg.modle.recommend;

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
@Table(name = "dj_recommend_config")
@ApiModel(description = "推荐配置表")
@FieldNameConstants(prefix = "")
public class RecommendConfig extends BaseEntity {

    /** '配置项code */
    @Column(name = "config_code")
    @Desc(value = "'配置项code")
    @ApiModelProperty("'配置项code")
    private String configCode;

    /** 配置项名称 */
    @Column(name = "config_name")
    @Desc(value = "配置项名称")
    @ApiModelProperty("配置项名称")
    private String configName;

    /** 配置值 */
    @Column(name = "config_value")
    @Desc(value = "配置值")
    @ApiModelProperty("配置值")
    private String configValue;
}
