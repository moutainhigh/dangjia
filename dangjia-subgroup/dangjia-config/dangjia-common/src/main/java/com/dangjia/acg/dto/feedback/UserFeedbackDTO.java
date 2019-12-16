package com.dangjia.acg.dto.feedback;

import com.dangjia.acg.common.annotation.Desc;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * author: ljl
 */
@Data
public class UserFeedbackDTO{

    private String id;


    @ApiModelProperty("创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createDate;// 创建日期

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("用户手机号码")
    private String mobile;

    @ApiModelProperty("反馈状态：0-未查看 1-已查看")
    private Integer feedbackType;

    @ApiModelProperty("来源应用（1:业主端，2:工匠端）")
    private Integer appType;


    @ApiModelProperty("图片")
    List<String> image;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("反馈id")
    private String feedbackId;
}
