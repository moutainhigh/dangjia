package com.dangjia.acg.modle.matter;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 装修指南阶段配置
 */
@Data
@Entity
@Table(name = "dj_matter_renovation_stage")
@ApiModel(description = "装修指南阶段配置")
public class RenovationStage extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "阶段名称")
    @ApiModelProperty("阶段名称")
    private String name;

    @Column(name = "image")
    @Desc(value = "图标")
    @ApiModelProperty("图标")
    private String image;

}