package com.dangjia.acg.modle.actuary;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 精算模版model
 * @类 名： ActuarialTemplate
 * @功能描述：
 * @作者信息： lxl
 * @创建时间： 2018-9-20上午13:35:10
 */
@Data
@Entity
@Table(name = "dj_actuary_actuarial_template")
@ApiModel(description = "精算模板表")
public class ActuarialTemplate extends BaseEntity {

    @Column(name = "user_id")
    @Desc(value = "用户ID")
    @ApiModelProperty("用户ID")
    private String userId;

    @Column(name = "name")
    @Desc(value = "精算模板名称")
    @ApiModelProperty("精算模板名称")
    private String name;

    @Column(name = "worker_type_name")
    @Desc(value = "工序")
    @ApiModelProperty("工序")
    private String workerTypeName;

    @Column(name = "worker_type_id")
    @Desc(value = "工序类型ID")
    @ApiModelProperty("工序类型ID")
    private Integer workerTypeId;

    @Column(name = "style_type")
    @Desc(value = "风格")
    @ApiModelProperty("风格")
    private String styleType;

    @Column(name = "applicable_area")
    @Desc(value = "适用面积")
    @ApiModelProperty("适用面积")
    private String applicableArea;

    @Column(name = "number_of_use")
    @Desc(value = "使用次数")
    @ApiModelProperty("使用次数")
    private Integer numberOfUse;

    @Column(name = "state_type")
    @Desc(value = "状态0为停用1为启用")
    @ApiModelProperty("状态0为停用1为启用")
    private Integer stateType;

}

