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
 * Time: 14:09
 */
@Data
@Entity
@Table(name = "dj_basics_attribute")
@ApiModel(description = "属性选项")
@FieldNameConstants(prefix = "")
public class DjBasicsAttribute extends BaseEntity {

    @Column(name = "category_id")
    @Desc(value = "分类id")
    @ApiModelProperty("分类id")
    private String categoryId;

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "type")
    @Desc(value = "类型")
    @ApiModelProperty("类型")
    private Integer type;

    @Column(name = "is_screen_conditions")
    @Desc(value = "是否作为筛选条件（1是，0否）")
    @ApiModelProperty("是否作为筛选条件（1是，0否）")
    private Integer isScreenConditions;
}
