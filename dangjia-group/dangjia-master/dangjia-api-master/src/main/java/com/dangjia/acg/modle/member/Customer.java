package com.dangjia.acg.modle.member;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.dto.sale.client.CustomerIndexDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
}