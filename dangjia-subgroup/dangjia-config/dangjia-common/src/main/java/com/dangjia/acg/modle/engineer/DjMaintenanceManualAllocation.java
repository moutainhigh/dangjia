package com.dangjia.acg.modle.engineer;

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
 * Date: 13/12/2019
 * Time: 上午 9:39
 */
@Data
@Entity
@Table(name = "dj_maintenance_manual_allocation")
@FieldNameConstants(prefix = "")
@ApiModel(description = "维保申诉成功人工定责")
public class DjMaintenanceManualAllocation extends BaseEntity {

    @Column(name = "complain_id")
    @Desc(value = "申诉单ID")
    @ApiModelProperty("申诉单ID")
    private String complainId;

    @Column(name = "maintenance_record_id")
    @Desc(value = "维保记录表id")
    @ApiModelProperty("维保记录表id")
    private String maintenanceRecordId;

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "operator_id")
    @Desc(value = "处理人ID")
    @ApiModelProperty("处理人ID")
    private String operatorId;

    @Column(name = "money")
    @Desc(value = "涉及金额")
    @ApiModelProperty("涉及金额")
    private Double money;

    @Column(name = "operator_name")
    @Desc(value = "处理人名称")
    @ApiModelProperty("处理人名称")
    private Double operatorName;

    @Column(name = "operator_time")
    @Desc(value = "运费")
    @ApiModelProperty("运费")
    private Date operatorTime;

    @Column(name = "status")
    @Desc(value = "处理状态（1待处理，2已处理）")
    @ApiModelProperty("处理状态（1待处理，2已处理）")
    private Integer status;

}
