package com.dangjia.acg.modle.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 28/12/2019
 * Time: 下午 1:59
 */
@Data
@Entity
@Table(name = "dj_acceptance_task")
@ApiModel(description = "验收任务表")
@FieldNameConstants(prefix = "")
public class DjAcceptanceTask extends BaseEntity {

    @Column(name = "apply_dec")
    @Desc(value = "每日描述 审核停工的原因")
    @ApiModelProperty("每日描述 审核停工的原因")
    private String applyDec;//applydec

    @Column(name = "house_id")
    @Desc(value = "房子/项目ID")
    @ApiModelProperty("房子/项目ID")
    private String houseId;//houseid


    @Column(name = "acceptance_number")
    @Desc(value = "业主验收次数")
    @ApiModelProperty("业主验收次数")
    private Integer acceptanceNumber;//acceptance_number

    @Column(name = "type")
    @Desc(value = "类型 1：主动验收 2：被动验收")
    @ApiModelProperty("类型 1：主动验收 2：被动验收")
    private Integer type;//type
}
