package com.dangjia.acg.modle.attribute;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *   分类与属性的关联 中间表
 */
@Data
@Entity
@Table(name = "dj_basics_category_attribute")
@ApiModel(description = "属性分类")
public class CategoryAttribute extends BaseEntity {

	@Column(name = "category_id")
    private String categoryId;//分类id

	@Column(name = "attribute_id")
    private String attributeId;//属性id

}