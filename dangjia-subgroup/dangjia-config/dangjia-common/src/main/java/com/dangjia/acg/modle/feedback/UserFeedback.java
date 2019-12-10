package com.dangjia.acg.modle.feedback;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * author: ljl
 */
@Data
@Entity
@Table(name = "dj_user_feedback")
@ApiModel(description = "用户反馈")
@FieldNameConstants(prefix = "")
public class UserFeedback extends BaseEntity {

    @Column(name = "user_id")
    @Desc(value = "用户id")
    @ApiModelProperty("用户id")
    private String userId;

    @Column(name = "feedback_type")
    @Desc(value = "反馈状态：0-未查看 1-已查看")
    @ApiModelProperty("反馈状态：0-未查看 1-已查看")
    private Integer feedbackType;

    @Column(name = "app_type")
    @Desc(value = "来源应用（1:业主端，2:工匠端）")
    @ApiModelProperty("来源应用（1:业主端，2:工匠端）")
    private Integer appType;


}
