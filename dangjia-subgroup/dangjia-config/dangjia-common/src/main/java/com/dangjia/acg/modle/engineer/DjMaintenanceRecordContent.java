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
 * Date: 26/12/2019
 * Time: 上午 11:38
 */
@Data
@Entity
@Table(name = "dj_maintenance_record_content")
@FieldNameConstants(prefix = "")
@ApiModel(description = "维保记录内容")
public class DjMaintenanceRecordContent extends BaseEntity {

    @Column(name = "maintenance_record_id")
    @Desc(value = "维保记录表id")
    @ApiModelProperty("维保记录表id")
    private String maintenanceRecordId;

    @Column(name = "remark")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remark;

    @Column(name = "member_id")
    @Desc(value = "用户id")
    @ApiModelProperty("用户id")
    private String memberId;

    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String image;

    @Column(name = "type")
    @Desc(value = "类型 1:工匠 2:大管家 3：业主")
    @ApiModelProperty("类型 1:工匠 2:大管家 3：业主")
    private Integer type;


    @Column(name = "worker_type_id")
    @Desc(value = "工种id")
    @ApiModelProperty("工种id")
    private String workerTypeId;

}
