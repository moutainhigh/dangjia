package com.dangjia.acg.modle.member;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 客服基础类 - 这个类只用于接收客服的申请，如有其它业务，请以其它表补充。
 */
@Data
@Entity
@Table(name = "dj_member_feedback")
@ApiModel(description = "客服反馈表")
public class Feedback extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "申请会员ID")
    @ApiModelProperty("申请会员ID")
    private String memberId;//

    @Column(name = "mobile")
    @Desc(value = "申请会员手机号")
    @ApiModelProperty("申请会员手机号")
    private String mobile;//

    @Column(name = "worker_type_id")
    @Desc(value = "审核工种ID")
    @ApiModelProperty("审核工种")
    private String workerTypeId;//workertypeid

    @Column(name = "worker_type_name")
    @Desc(value = "审核工种名称")
    @ApiModelProperty("审核工种名称")
    private String workerTypeName;//workertype

    @Column(name = "customerdes")
    @Desc(value = "描述")
    @ApiModelProperty("描述")
    private String customerdes;//

    @Column(name = "imageurl")
    @Desc(value = "反馈图片（多张）")
    @ApiModelProperty("反馈图片（多张）")
    private String imageurl;

    @Column(name = "state")
    @Desc(value = "审核状态,0已经提交等审核，1用户自己撤回，2，已受理，3、受理完成，告知业主，4，业主审核通过，全部完成。")
    @ApiModelProperty("审核状态,0已经提交等审核，1用户自己撤回，2，已受理，3、受理完成，告知业主，4，业主审核通过，全部完成。")
    private Integer state;//

    @Column(name = "state_result")
    @Desc(value = "审核结果")
    @ApiModelProperty("审核结果")
    private String stateResult;

    @Column(name = "state_name")
    @Desc(value = "审核人")
    @ApiModelProperty("审核人")
    private String stateName;
}