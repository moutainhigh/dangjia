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
@Table(name = "dj_lattice_coding")
@ApiModel(description = "方格编号表")
@FieldNameConstants(prefix = "")
public class LatticeCoding extends BaseEntity {

    /** '编码名称 */
    @Column(name = "coding_name")
    @Desc(value = "'编码名称")
    @ApiModelProperty("'编码名称")
    private String codingName;

    /** '编码值 */
    @Column(name = "coding_value")
    @Desc(value = "'编码值")
    @ApiModelProperty("'编码值")
    private Integer codingValue;

    /** '行号 */
    @Column(name = "row_no")
    @Desc(value = "'行号")
    @ApiModelProperty("'行号")
    private Integer rowNo;

    /** '列号 */
    @Column(name = "col_no")
    @Desc(value = "'列号")
    @ApiModelProperty("'列号")
    private Integer colNo;
}
