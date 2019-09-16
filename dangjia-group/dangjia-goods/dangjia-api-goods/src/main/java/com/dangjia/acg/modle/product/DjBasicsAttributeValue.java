package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/15
 * Time: 14:12
 */
@Data
@Entity
@Table(name = "dj_basics_attribute_value")
@ApiModel(description = "属性选项")
@FieldNameConstants(prefix = "")
public class DjBasicsAttributeValue extends BaseEntity {

    @Column(name = "attribute_id")
    @Desc(value = "属性id")
    @ApiModelProperty("属性id")
    private String attributeId;//属性id

    @Column(name = "name")
    @Desc(value = "说明")
    @ApiModelProperty("说明")
    private String name;//说明

    @Column(name = "introduction")
    @Desc(value = "说明")
    @ApiModelProperty("说明")
    private String introduction;//说明
}
