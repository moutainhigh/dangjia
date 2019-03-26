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

@Data
@Entity
@Table(name = "dj_member_info")
@FieldNameConstants(prefix = "")
@ApiModel(description = "当家用户身份表")
public class MemberInfo extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "会员编号")
    @ApiModelProperty("会员编号")
    private String memberId;// 会员编号

    @Column(name = "password")
    @Desc(value = "密码")
    @ApiModelProperty("密码,MD5加密")
    private String password;// 密码

    @Column(name = "policy_id")
    @Desc(value = "会员策略 1=业主  2=工匠  3=供应商  4=其他")
    @ApiModelProperty("会员策略 1=业主  2=工匠  3=供应商  4=其他")
    private String policyId;//会员策略

    @Column(name = "check_status")
    @Desc(value = "审核状态 预留")
    @ApiModelProperty("审核状态 预留")
    private String checkStatus;// 审核状态

}
