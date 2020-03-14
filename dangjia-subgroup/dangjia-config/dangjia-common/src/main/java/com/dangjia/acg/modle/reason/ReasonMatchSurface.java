package com.dangjia.acg.modle.reason;

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
 * author: ljl
 */
@Data
@Entity
@Table(name = "dj_reason_match_surface")
@ApiModel(description = "工匠更换原因")
@FieldNameConstants(prefix = "")
public class ReasonMatchSurface extends BaseEntity {

    @Column(name = "remark")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remark;

}
