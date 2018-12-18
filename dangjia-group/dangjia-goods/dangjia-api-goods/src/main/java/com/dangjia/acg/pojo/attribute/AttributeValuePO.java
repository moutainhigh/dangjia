package com.dangjia.acg.pojo.attribute;

import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.attribute.Attribute;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;

/**
 * 基础 属性值
 */
@Data
@Entity
@Table(name = "dj_basics_attribute_value")
@ApiModel(description = "属性选项")
public class AttributeValuePO extends BaseEntity {

    @Column(name = "attribute_id")
    private String attributeId;//属性id

    @Column(name = "name")
    private String name;//名称

    @Column(name = "image")
    private String image;//图片

    @Column(name = "introduction")
    private String introduction;//说明


    private Attribute attribute;
    /*
     * 多对一关联关系
     * 延迟加载：fetch = FetchType.LAZY
     * 引用外键：attribute_id
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "attribute_id")
    public Attribute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

}