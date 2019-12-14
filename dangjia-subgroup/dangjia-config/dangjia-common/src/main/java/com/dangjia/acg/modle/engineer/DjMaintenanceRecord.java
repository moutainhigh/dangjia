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

    @Column(name = "owner_image")
    @Desc(value = "业主上传图片(逗号分隔)")
    @ApiModelProperty("业主上传图片(逗号分隔)")
    private String ownerImage;

    @Column(name = "owner_remark")
    @Desc(value = "业主备注")
    @ApiModelProperty("业主备注")
    private String ownerRemark;

    @Column(name = "supervisor_id")
    @Desc(value = "督导id")
    @ApiModelProperty("督导id")
    private String supervisor_id;

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
    @Desc(value = "自购金额")
    @ApiModelProperty("自购金额")
    private Double sincePurchaseAmount;

    @Column(name = "remark")
    @Desc(value = "自购商品备注")
    @ApiModelProperty("自购商品备注")
    private String remark;

    @Column(name = "owner_state")
    @Desc(value = "状态 1:待业主确认 2:业主已确认 3:业主已拒绝")
    @ApiModelProperty("状态 1:待业主确认 2:业主已确认 3:业主已拒绝")
    private Integer ownerState;

    @Column(name = "steward_image")
    @Desc(value = "管家上传图片(逗号分隔)")
    @ApiModelProperty("管家上传图片(逗号分隔)")
    private String stewardImage;

    @Column(name = "steward_remark")
    @Desc(value = "管家备注")
    @ApiModelProperty("管家备注")
    private String stewardRemark;

    @Column(name = "state")
    @Desc(value = "状态 1:待审核 2:已审核")
    @ApiModelProperty("状态 1:待审核 2:已审核")
    private Integer state;





}
