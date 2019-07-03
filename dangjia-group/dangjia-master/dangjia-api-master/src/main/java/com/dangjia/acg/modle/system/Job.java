package com.dangjia.acg.modle.system;

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
 * author: qyx
 * Date: 2019/7/1
 * Time: 16:05
 */
@Data
@Entity
@Table(name = "dj_system_job")
@ApiModel(description = "岗位表")
@FieldNameConstants(prefix = "")
public class Job extends BaseEntity {

    @Column(name = "department_id")
    @Desc(value = "部门ID")
    @ApiModelProperty("部门ID")
    private String departmentId;
    @Column(name = "department_name")
    @Desc(value = "部门ID")
    @ApiModelProperty("部门ID")
    private String departmentName;
    @Column(name = "operate_id")
    @Desc(value = "操作id")
    @ApiModelProperty("操作id")
    private String operateId;

    @Column(name = "name")
    @Desc(value = "岗位名称")
    @ApiModelProperty("岗位名称")
    private String name;

    @Column(name = "code")
    @Desc(value = "岗位编号")
    @ApiModelProperty("岗位编号")
    private String code;

    @Column(name = "info")
    @Desc(value = "岗位描述")
    @ApiModelProperty("岗位描述")
    private String info;


    @Column(name = "city_name")
    @Desc(value = "城市名称")
    @ApiModelProperty("城市名称")
    private String cityName;


    @Column(name = "city_id")
    @Desc(value = "城市ID")
    @ApiModelProperty("城市ID")
    private String cityId;


}
