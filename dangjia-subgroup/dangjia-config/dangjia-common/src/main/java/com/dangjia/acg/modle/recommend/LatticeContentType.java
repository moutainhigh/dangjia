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
@Table(name = "dj_lattice_content_type")
@ApiModel(description = "方格内容类型表")
@FieldNameConstants(prefix = "")
public class LatticeContentType extends BaseEntity {

    /** '类型名称 */
    @Column(name = "type_name")
    @Desc(value = "'类型名称")
    @ApiModelProperty("'类型名称")
    private String typeName;

    /** '类型值 */
    @Column(name = "type_value")
    @Desc(value = "'类型值")
    @ApiModelProperty("'类型值")
    private Integer typeValue;
}
