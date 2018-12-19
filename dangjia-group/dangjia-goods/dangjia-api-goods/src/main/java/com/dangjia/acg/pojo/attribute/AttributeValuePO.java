package com.dangjia.acg.pojo.attribute;

import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.AttributeValue;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;

/**
 * 基础 属性值
 *  @作者信息： ysl
 * * @创建时间： 2018-12-18下午14:55:35
 */
@Data
@Entity
@Table(name = "dj_basics_attribute_value")
@ApiModel(description = "属性选项")
public class AttributeValuePO extends AttributeValue {

    /*
     * 多对一关联关系
     * 延迟加载：fetch = FetchType.LAZY
     * 引用外键：attribute_id
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "attribute_id")//属性id
    private Attribute attribute;

}