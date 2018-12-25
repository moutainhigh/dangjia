package com.dangjia.acg.modle.attribute;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 基础 属性值
 */
@Data
@Entity
@Table(name = "dj_basics_attribute_value")
@ApiModel(description = "属性选项")
public class AttributeValue extends BaseEntity {

    @Column(name = "attribute_id")
    private String attributeId;//属性id

    @Column(name = "name")
    private String name;//名称

    @Column(name = "image")
    private String image;//图片

    @Column(name = "introduction")
    private String introduction;//说明
}