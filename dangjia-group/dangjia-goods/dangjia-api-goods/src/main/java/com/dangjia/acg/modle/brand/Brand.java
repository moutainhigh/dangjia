package com.dangjia.acg.modle.brand;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class Brand extends GoodsBaseEntity {

    @Column(name = "name")
    private String name;//名称

    @Column(name = "image")
    private String image;//图片

}