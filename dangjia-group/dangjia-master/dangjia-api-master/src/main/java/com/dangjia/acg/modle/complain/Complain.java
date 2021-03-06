package com.dangjia.acg.modle.complain;

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
@Table(name = "dj_complain")
@FieldNameConstants(prefix = "")
@ApiModel(description = "当家申诉处理")
public class Complain extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "对象ID")
    @ApiModelProperty("对象ID")
    private String memberId;

    @Column(name = "complain_type")
    @Desc(value = "申诉类型 1:工匠被处罚后不服.2：业主要求整改.3：大管家（开工后）要求换人.4:部分收货申诉.5提前结束装修")
    @ApiModelProperty("申诉类型 1:工匠被处罚后不服.2：业主要求整改.3：大管家（开工后）要求换人.4:部分收货申诉.5提前结束装修")
    private Integer complainType;

    @Column(name = "user_id")
    @Desc(value = "发起人ID")
    @ApiModelProperty("发起人ID")
    private String userId;

    @Column(name = "status")
    @Desc(value = "处理状态.0:待处理。1.驳回。2.接受。")
    @ApiModelProperty("处理状态.0:待处理。1.驳回。2.接受。")
    private Integer status;

    @Column(name = "description")
    @Desc(value = "处理描述")
    @ApiModelProperty("处理描述")
    private String description;

    @Column(name = "business_id")
    @Desc(value = "对应业务ID  " +
            "complain_type==1:对应处罚的rewardPunishRecordId, " +
            "complain_type==2:对应工匠memberId," +
            "complain_type==3:对应工匠memberId," +
            "complain_type==4:发货单splitDeliverId,")
    @ApiModelProperty("对应业务ID")
    private String businessId;

    @Column(name = "house_id")
    @Desc(value = "对应房子ID")
    @ApiModelProperty("对应房子ID")
    private String houseId;


    @Column(name = "files")
    @Desc(value = "附件")
    @ApiModelProperty("附件")
    private String files;

    @Column(name = "content")
    @Desc(value = "对象名称")
    @ApiModelProperty("对象名称")
    private String content;

    @Column(name = "user_name")
    @Desc(value = "发起人名称")
    @ApiModelProperty("发起人名称")
    private String userName;

    @Column(name = "operate_id")
    @Desc(value = "操作人ID")
    @ApiModelProperty("操作人ID")
    private String operateId;

    @Column(name = "operate_name")
    @Desc(value = "操作人姓名")
    @ApiModelProperty("操作人姓名")
    private String operateName;


    @Column(name = "user_nick_name")
    @Desc(value = "发起人昵称")
    @ApiModelProperty("发起人昵称")
    private String userNickName;


    @Column(name = "user_mobile")
    @Desc(value = "发起人电话")
    @ApiModelProperty("发起人电话")
    private String userMobile;

}