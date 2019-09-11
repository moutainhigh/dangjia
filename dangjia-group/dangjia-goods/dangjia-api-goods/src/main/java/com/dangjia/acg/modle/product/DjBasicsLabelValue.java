package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.annotation.Desc;
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
 * Date: 2019/7/25
 * Time: 13:56
 */
@Data
@Entity
@Table(name = "dj_basics_label_value")
@ApiModel(description = "商品标签值表")
@FieldNameConstants(prefix = "")
public class DjBasicsLabelValue {
    @Column(name = "label_id")
    @Desc(value = "标签id")
    @ApiModelProperty("标签id")
    private String labelId;

    @Column(name = "name")
    @Desc(value = "标签值名称")
    @ApiModelProperty("标签值名称")
    private String name;

    @Column(name = "sort")
    @Desc(value = "排序")
    @ApiModelProperty("排序")
    private String sort;
}
