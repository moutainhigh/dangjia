package com.dangjia.acg.dto.feedback;

import com.dangjia.acg.common.annotation.ExcelField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * author: ljl
 */
@Data
public class ExpotUserFeedbackDTO {

    @ApiModelProperty("创建时间")
    @ExcelField(titile = "创建时间", offset =1)
    protected Date createDate;// 创建日期

    @ApiModelProperty("来源应用（1:业主端，2:工匠端）")
    @ExcelField(titile = "客户端", offset =2)
    private String appTypeName;

    @ApiModelProperty("用户名称")
    @ExcelField(titile = "用户名称", offset =3)
    private String userName;

    @ApiModelProperty("用户手机号码")
    @ExcelField(titile = "用户号码", offset =4)
    private String mobile;

    @ApiModelProperty("反馈状态：0-未查看 1-已查看")
    @ExcelField(titile = "状态", offset =5)
    private String feedbackTypeName;


}
