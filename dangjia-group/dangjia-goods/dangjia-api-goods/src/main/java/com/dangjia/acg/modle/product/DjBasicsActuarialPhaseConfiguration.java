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
 * Date: 2019/9/20
 * Time: 16:11
 */
@Data
@Entity
@Table(name = "dj_basics_actuarial_phase_configuration")
@ApiModel(description = "属性选项")
@FieldNameConstants(prefix = "")
public class DjBasicsActuarialPhaseConfiguration extends BaseEntity {

    @Column(name = "phase")
    @Desc(value = "阶段")
    @ApiModelProperty("阶段")
    private String phase;


}
