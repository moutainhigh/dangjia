package com.dangjia.acg.pojo.attribute;

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
public class AttributePO extends BaseEntity {

    @Column(name = "category_id")
    private String categoryId;//分类id

    @Column(name = "name")
    private String name;//名称

    @Column(name = "type")
    private Integer type;//1价格,2规格

    private List<AttributeValuePO> attributeValueLists;
    /*
     * 一对多关联关系
     * 级联关系：cascade=CascadeType.ALL
     * 延迟加载：fetch = FetchType.LAZY
     * 映射：mappedBy = "dj_basics_attribute"
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "AttributePO")
    public List<AttributeValuePO> getAttributeValueLists() {
        return this.attributeValueLists;
    }

    public void setAttributeValueLists(List<AttributeValuePO> attributeValueLists) {
        this.attributeValueLists = attributeValueLists;
    }

}