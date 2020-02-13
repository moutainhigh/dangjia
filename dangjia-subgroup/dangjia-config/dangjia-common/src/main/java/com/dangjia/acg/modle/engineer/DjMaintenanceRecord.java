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
 * Time: 上午 9:20
 */
@Data
@Entity
@Table(name = "dj_maintenance_record")
@FieldNameConstants(prefix = "")
@ApiModel(description = "维保记录")
public class DjMaintenanceRecord extends BaseEntity {

    @Column(name = "steward_processing_time")
    @Desc(value = "管家处理时间")
    @ApiModelProperty("管家处理时间")
    private Date stewardProcessingTime;

    @Column(name = "steward_order_time")
    @Desc(value = "管家接单时间")
    @ApiModelProperty("管家接单时间")
    private Date stewardOrderTime;

    @Column(name = "member_id")
    @Desc(value = "业主id")
    @ApiModelProperty("业主id")
    private String memberId;

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "owner_name")
    @Desc(value = "业主名称")
    @ApiModelProperty("业主名称")
    private String ownerName;

    @Column(name = "owner_mobile")
    @Desc(value = "业主电话号码")
    @ApiModelProperty("业主电话号码")
    private String ownerMobile;

    @Column(name = "steward_id")
    @Desc(value = "接单管家id")
    @ApiModelProperty("接单管家id")
    private String stewardId;

    @Column(name = "state")
    @Desc(value = "状态 1:业主待验收 2:业主已验收 3:业主拒绝验收通过 4结束质保  5 已开工")
    @ApiModelProperty("状态 1:业主待验收 2:业主已验收 3:业主拒绝验收通过 4结束质保  5 已开工")
    private Integer state;

    @Column(name = "worker_type_id")
    @Desc(value = "工种id")
    @ApiModelProperty("工种id")
    private String workerTypeId;

    @Column(name = "worker_type_safe_order_id")
    @Desc(value = "保险订单ID")
    @ApiModelProperty("保险订单ID")
    private String workerTypeSafeOrderId;


    @Column(name = "worker_member_id")
    @Desc(value = "工匠id")
    @ApiModelProperty("工匠id")
    private String workerMemberId;

    @Column(name = "worker_create_date")
    @Desc(value = "工匠接单时间")
    @ApiModelProperty("工匠接单时间")
    private Date workerCreateDate;

    @Column(name = "apply_collect_time")
    @Desc(value = "工匠申请验收时间")
    @ApiModelProperty("工匠申请验收时间")
    private Date applyCollectTime;

    @Column(name = "over_protection")
    @Desc(value = "是否过保（1是，0否）")
    @ApiModelProperty("是否过保（1是，0否）")
    private Integer overProtection;

    @Column(name = "end_maintenance_type")
    @Desc(value = "结束维保人员类型（1:工匠 2:大管家 3：业主）")
    @ApiModelProperty("结束维保人员类型（1:工匠 2:大管家 3：业主）")
    private Integer endMaintenanceType;

}
