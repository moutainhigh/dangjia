package com.dangjia.acg.modle.brand;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
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

}