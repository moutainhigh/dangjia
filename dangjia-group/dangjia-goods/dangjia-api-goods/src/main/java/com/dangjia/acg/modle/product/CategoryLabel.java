package com.dangjia.acg.modle.product;

import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 标签实体
 * @author ysl
 */
@Data
@Entity
@Table(name = "dj_basics_category_label")
@ApiModel(description = "类别标签")
public class CategoryLabel extends GoodsBaseEntity {

    @Column(name = "name")
    private String name;//标签名称

    @Column(name = "sort")
    private int sort;//排序

}