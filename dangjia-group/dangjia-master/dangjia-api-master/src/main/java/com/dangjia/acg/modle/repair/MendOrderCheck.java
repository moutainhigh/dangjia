package com.dangjia.acg.modle.repair;

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
 * author: Ronalcheng
 * Date: 2019/1/16 0016
 * Time: 14:39
 * 补退单关联审核
 */
@Data
@Entity
@Table(name = "dj_repair_mend_order_check")
@ApiModel(description = "补退单关联审核")
@FieldNameConstants(prefix = "")
public class MendOrderCheck extends BaseEntity {

    @Column(name = "mend_order_id")
    @Desc(value = "补退单id")
    @ApiModelProperty("补退单id")
    private String mendOrderId;

    @Column(name = "role_type")
    @Desc(value = "角色代号")
    @ApiModelProperty("角色代号")
    private String roleType;  //1业主,2管家,3工匠,4材料员,5供应商

    @Column(name = "state")
    @Desc(value = "审核状态")
    @ApiModelProperty("审核状态")
    private Integer state; //0处理中,1未通过,2已通过

    @Column(name = "auditor_id")
    @Desc(value = "审核人id")
    @ApiModelProperty("审核人id")
    private String auditorId;

    @Column(name = "sort")
    @Desc(value = "审核顺序")
    @ApiModelProperty("审核顺序")
    private Integer sort;
}
