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

    @Column(name = " payment_date")
    @Desc(value = "支付时间")
    @ApiModelProperty("支付时间")
    private Date paymentDate;

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

    @Column(name = "supervisor_id")
    @Desc(value = "督导id")
    @ApiModelProperty("督导id")
    private String supervisorId;

    @Column(name = "steward_subsidy")
    @Desc(value = "是否需要补贴管家 1:是 2:否")
    @ApiModelProperty("是否需要补贴管家 1:是 2:否")
    private Integer stewardSubsidy;

    @Column(name = "service_remark")
    @Desc(value = "客服备注")
    @ApiModelProperty("客服备注")
    private String serviceRemark;

    @Column(name = "steward_id")
    @Desc(value = "接单管家id")
    @ApiModelProperty("接单管家id")
    private String stewardId;

    @Column(name = "steward_state")
    @Desc(value = "管家处理状态 1：待处理 2：已处理")
    @ApiModelProperty("管家处理状态 1：待处理 2：已处理")
    private Integer stewardState;

    @Column(name = "since_purchase_amount")
    @Desc(value = "维保金额")
    @ApiModelProperty("维保金额")
    private Double sincePurchaseAmount;

    @Column(name = "enough_amount")
    @Desc(value = "自购金额")
    @ApiModelProperty("自购金额")
    private Double enoughAmount;

    @Column(name = "remark")
    @Desc(value = "自购商品备注")
    @ApiModelProperty("自购商品备注")
    private String remark;

    @Column(name = "owner_state")
    @Desc(value = "状态 1:待业主确认 2:业主已确认 3:业主已拒绝")
    @ApiModelProperty("状态 1:待业主确认 2:业主已确认 3:业主已拒绝")
    private Integer ownerState;

    @Column(name = "state")
    @Desc(value = "状态 1:待审核 2:已通过 3:已拒绝")
    @ApiModelProperty("状态 1:待审核 2:已通过 3:已拒绝")
    private Integer state;

    @Column(name = "complain_type")
    @Desc(value = "申诉类型：9-业主申请质保")
    @ApiModelProperty("申诉类型：9-业主申请质保")
    private Integer complainType;

    @Column(name = "handle_type")
    @Desc(value = "客服处理状态0-待处理 1-驳回 2-接受 3-已处理 4-已结束")
    @ApiModelProperty("客服处理状态0-待处理 1-驳回 2-接受 3-已处理 4-已结束")
    private Integer handleType;

    @Column(name = "user_id")
    @Desc(value = "处理人id")
    @ApiModelProperty("处理人id")
    private String userId;

    @Column(name = "worker_member_id")
    @Desc(value = "工匠id")
    @ApiModelProperty("工匠id")
    private String workerMemberId;

    @Column(name = "worker_create_date")
    @Desc(value = "工匠接单时间")
    @ApiModelProperty("工匠接单时间")
    private Date workerCreateDate;


    @Column(name = "worker_type_id")
    @Desc(value = "工种id")
    @ApiModelProperty("工种id")
    private String workerTypeId;

}
