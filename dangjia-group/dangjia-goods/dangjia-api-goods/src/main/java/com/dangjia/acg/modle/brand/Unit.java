package com.dangjia.acg.modle.brand;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 单位实体
 * @author Ronalcheng
 */
@Data
@Entity
@Table(name = "dj_basics_unit")
@ApiModel(description = "单位")
public class Unit extends BaseEntity {

    @Column(name = "name")
    private String name;//名称

    @Column(name = "link_unit_id_arr")
    private String linkUnitIdArr;// 关联多个换算单位Id集合

    @Column(name = "type")
    @Desc(value = "单位数值类型 1=整数单位，2=小数单位")
    @ApiModelProperty("单位数值类型 1=整数单位，2=小数单位")
    private int type;
}