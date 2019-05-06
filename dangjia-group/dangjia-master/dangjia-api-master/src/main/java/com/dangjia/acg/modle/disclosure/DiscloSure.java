package com.dangjia.acg.modle.disclosure;

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
@Table(name = "dj_disclosure")
@ApiModel(description = "交底事项表")
@FieldNameConstants(prefix = "")
public class DiscloSure extends BaseEntity {

    @Column(name = "sure_name")
    @Desc(value = "交底事项/帮助名字")
    @ApiModelProperty("交底事项/帮助名字")
    private String sureName;

    @Column(name = "sure_desc")
    @Desc(value = "交底事项/帮助描述")
    @ApiModelProperty("交底事项/帮助描述")
    private String sureDesc;

    @Column(name = "sure_img")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String sureImg;

    @Column(name = "type")
    @Desc(value = "类型")
    @ApiModelProperty("类型 0:交底 1帮助")
    private int type;

}
