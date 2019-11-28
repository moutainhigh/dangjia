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
@Table(name = "dj_system_department")
@ApiModel(description = "组织结构表")
@FieldNameConstants(prefix = "")
public class Department extends BaseEntity {

    @Column(name = "operate_id")
    @Desc(value = "操作id")
    @ApiModelProperty("操作id")
    private String operateId;

    @Column(name = "name")
    @Desc(value = "组织名称")
    @ApiModelProperty("组织名称")
    private String name;

    @Column(name = "parent_id")
    @Desc(value = "上级id")
    @ApiModelProperty("上级id")
    private String parentId;

    @Column(name = "parent_top")
    @Desc(value = "顶级")
    @ApiModelProperty("顶级")
    private String parentTop;

    @Column(name = "city_name")
    @Desc(value = "城市名称")
    @ApiModelProperty("城市名称")
    private String cityName;


    @Column(name = "city_id")
    @Desc(value = "城市ID")
    @ApiModelProperty("城市ID")
    private String cityId;


}
