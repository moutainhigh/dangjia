package com.dangjia.acg.pojo.attribute;

import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.CategoryAttribute;
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
public class AttributePO extends Attribute {
    /*
     * 多对一关联关系
     * 延迟加载：fetch = FetchType.LAZY
     * 引用外键：category_id
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private CategoryAttribute categoryAttribute;//分类对象

    /*
     * 一对多关联关系
     * 级联关系：cascade=CascadeType.ALL
     * 延迟加载：fetch = FetchType.LAZY
     * 映射：mappedBy = "dj_basics_attribute"
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "AttributePO")
    private List<AttributeValuePO> attributeValueLists;//属性选项集合

}