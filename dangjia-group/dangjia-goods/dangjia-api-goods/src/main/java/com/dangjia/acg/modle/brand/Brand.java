package com.dangjia.acg.modle.brand;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @类 名： Brand.java
 */
@Data
@Entity
@Table(name = "dj_basics_brand")
@ApiModel(description = "品牌")
public class Brand extends BaseEntity{

    @Column(name = "name")
    private String name;//名称

}