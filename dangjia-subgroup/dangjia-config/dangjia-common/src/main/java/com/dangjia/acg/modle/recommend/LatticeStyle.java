package com.dangjia.acg.modle.recommend;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dj_lattice_style")
@ApiModel(description = "方格样式表")
@FieldNameConstants(prefix = "")
public class LatticeStyle extends BaseEntity {

    /** '样式名称 */
    @Column(name = "style_name")
    @Desc(value = "'样式名称")
    @ApiModelProperty("'样式名称")
    private String styleName;

    /** '行-数量 */
    @Column(name = "row_number")
    @Desc(value = "'行-数量")
    @ApiModelProperty("'行-数量")
    private Integer rowNumber;

    /** '列-数量 */
    @Column(name = "col_number")
    @Desc(value = "'列-数量")
    @ApiModelProperty("'列-数量")
    private Integer colNumber;

    /** '允许的内容类型值 */
    @Column(name = "type_values")
    @Desc(value = "'允许的内容类型值")
    @ApiModelProperty("'允许的内容类型值")
    private Integer typeValues;
}
