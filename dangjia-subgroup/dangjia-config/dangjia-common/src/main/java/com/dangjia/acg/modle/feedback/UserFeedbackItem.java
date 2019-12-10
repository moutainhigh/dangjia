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

/**
 * author: ljl
 */
@Data
@Entity
@Table(name = "dj_user_feedback_item")
@ApiModel(description = "用户反馈详情")
@FieldNameConstants(prefix = "")
public class UserFeedbackItem extends BaseEntity {
    @Column(name = "feedback_id")
    @Desc(value = "反馈id")
    @ApiModelProperty("反馈id")
    private String feedbackId;

    @Column(name = "user_id")
    @Desc(value = "用户id")
    @ApiModelProperty("用户id")
    private String userId;

    @Column(name = "remark")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private Integer remark;

    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private Integer image;


}
