package com.dangjia.acg.modle.attribute;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *  属性  中间层
 */
@Data
@Entity
@Table(name = "dj_basics_goods_attribute")
@ApiModel(description = "属性")
public class GoodsAttribute extends BaseEntity {

    @Column(name = "name")
    private String name;//名称

    @Column(name = "type")
    private Integer type;//1价格,2规格

}