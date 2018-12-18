package com.dangjia.acg.modle.attribute;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 属性  中间层
 */
@Data
@Entity
@Table(name = "dj_basics_attribute")
@ApiModel(description = "属性")
public class Attribute extends BaseEntity {

    @Column(name = "category_id")
    private String categoryId;//分类id

    @Column(name = "name")
    private String name;//名称

    @Column(name = "type")
    private Integer type;//1价格,2规格
}