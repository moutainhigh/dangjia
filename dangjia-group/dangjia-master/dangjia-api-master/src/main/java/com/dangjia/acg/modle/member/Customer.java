package com.dangjia.acg.modle.member;

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
 * 客服跟进表（记录每个业主的跟进阶段）
 * at ysl 2019-1-5
 */
@Data
@Entity
@Table(name = "dj_member_customer")
@ApiModel(description = "客服基础类")
@FieldNameConstants(prefix = "")
public class Customer extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "业主id")
    @ApiModelProperty("业主id")
    private String memberId;

    @Column(name = "user_id")
    @Desc(value = "销售id")
    @ApiModelProperty("销售id")
    private String userId;

    @Column(name = "label_id_arr")
    @Desc(value = "标签id数组")
    @ApiModelProperty("标签id数组")
    private String labelIdArr;

    @Column(name = "stage")
    @Desc(value = "阶段: 0未跟进,1继续跟进,2放弃跟进,3黑名单,4已下单")
    @ApiModelProperty("阶段")
    private Integer stage;

    @Column(name = "curr_record_id")
    @Desc(value = "最新沟通记录id")
    @ApiModelProperty("最新沟通记录id")
    private String currRecordId;

    @Column(name = "remind_record_id")
    @Desc(value = "最近的提醒沟通记录id")
    @ApiModelProperty("最近的提醒沟通记录id")
    private String remindRecordId;


    @Column(name = "store_id")
    @Desc(value = "门店id")
    @ApiModelProperty("门店id")
    private String storeId;

    @Column(name = "phase_status")
    @Desc(value = "阶段 0:线索阶段 1:客户阶段")
    @ApiModelProperty("阶段 0:线索阶段 1:客户阶段")
    private Integer phaseStatus;



    @Column(name = "clue_type")
    @Desc(value = "线索类型 1：跨域下单  0：正常")
    @ApiModelProperty("线索类型 1：跨域下单  0：正常")
    private Integer clueType;


    @Column(name = "turn_status")
    @Desc(value = "阶段 0:未转出阶段 1:转出阶段")
    @ApiModelProperty("阶段 0:未转出阶段 1:转出阶段")
    private Integer turnStatus;


    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;
}