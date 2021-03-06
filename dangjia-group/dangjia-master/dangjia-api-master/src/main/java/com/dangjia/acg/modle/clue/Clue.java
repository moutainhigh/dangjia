package com.dangjia.acg.modle.clue;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.annotation.ExcelField;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


@Data
@Entity
@Table(name = "dj_clue")
@ApiModel(description = "线索表")
@FieldNameConstants(prefix = "")
public class Clue extends BaseEntity {


    @Column(name = "owername")
    @Desc(value = "业主名")
    @ApiModelProperty("业主名")
    @ExcelField(titile = "业主名", offset = 1)
    private String owername;

    @Column(name = "phone")
    @Desc(value = "手机号码")
    @ApiModelProperty("手机号码")
    @ExcelField(titile = "手机号码", offset = 2)
    private String phone;

    @Column(name = "wechat")
    @Desc(value = "微信")
    @ApiModelProperty("微信")
    @ExcelField(titile = "微信", offset = 3)
    private String wechat;

    @Column(name = "address")
    @Desc(value = "地址")
    @ApiModelProperty("地址")
    @ExcelField(titile = "地址", offset = 4)
    private String address;

    @Column(name = "building")
    @Desc(value = "楼栋")
    @ApiModelProperty("楼栋")
    private String building;//

    @Column(name = "number")
    @Desc(value = "房间号")
    @ApiModelProperty("房间号")
    private String number;

    @Column(name = "stage")
    @Desc(value = "阶段 0:新线索 1：继续跟进 2:已放弃 3:黑名单 4:已下单 5:待确认下单")
    @ApiModelProperty("阶段 0:新线索 1：继续跟进 2:已放弃 3:黑名单 4:已下单 5:待确认下单")
    private Integer stage;

    @Column(name = "label_id")
    @Desc(value = "标签Id")
    @ApiModelProperty("标签Id")
    private String labelId;

    @Column(name = "remark")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remark;

    @Column(name = "report_date")
    @Desc(value = "报备时间")
    @ApiModelProperty("报备时间")
    private Date reportDate;

    @Column(name = "member_id")
    @Desc(value = "用户Id")
    @ApiModelProperty("用户Id")
    private String memberId;

    @Column(name = "cus_service")
    @Desc(value = "客服ID")
    @ApiModelProperty("客服ID")
    private String cusService;

    @Column(name = "store_id")
    @Desc(value = "门店Id")
    @ApiModelProperty("门店Id")
    private String storeId;

    @Column(name = "clue_type")
    @Desc(value = "线索类型 1：跨域下单  0：正常")
    @ApiModelProperty("线索类型 1：跨域下单  0：正常")
    private Integer clueType;

    @Column(name = "phase_status")
    @Desc(value = "阶段 0:线索阶段 1:客户阶段")
    @ApiModelProperty("阶段 0:线索阶段 1:客户阶段")
    private Integer phaseStatus;


    @Column(name = "turn_status")
    @Desc(value = "阶段 0:未转出阶段 1:转出阶段")
    @ApiModelProperty("阶段 0:未转出阶段 1:转出阶段")
    private Integer turnStatus;


    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;

    @Column(name = "tips")
    @Desc(value = "提示 0：无提示 1：提示")
    @ApiModelProperty("提示 0：无提示 1：提示")
    private String tips;

    @Column(name = "cross_domain_user_id")
    @Desc(value = "跨域下单的销售id ：不是跨域为空")
    @ApiModelProperty("跨域下单的销售id ：不是跨域为空")
    private String crossDomainUserId;

    @Column(name = "time_sequencing")
    @Desc(value = "列表时间排序字段")
    @ApiModelProperty("列表时间排序字段")
    private Date timeSequencing;

    @Column(name = "branch_user")
    @Desc(value = "分配销售 1：已分配  0：未分配")
    @ApiModelProperty("分配销售 1：已分配  0：未分配")
    private Integer branchUser;

}
