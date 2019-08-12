package com.dangjia.acg.dto.clue;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Data
public class ClueTalkDTO {

    protected String id;

    @ApiModelProperty("创建时间")
    protected Date createDate;// 创建日期

    @ApiModelProperty("修改时间")
    protected Date modifyDate;// 修改日期

    @ApiModelProperty("数据状态 0=正常，1=删除")
    protected Integer dataStatus;

    @ApiModelProperty("线索ID")
    private String clueId;

    @ApiModelProperty("操作人ID（客服）")
    private String userId;

    @ApiModelProperty("谈话内容")
    private String talkContent;

    @ApiModelProperty("提醒时间")
    private Date remindTime;

    @ApiModelProperty("memberId")
    private String memberId;

    @ApiModelProperty("阶段 0:线索阶段 1:客户阶段")
    private Integer phaseStatus;

    @ApiModelProperty("阶段 0:新线索 1：继续跟进 2:已放弃 3:黑名单 4:已下单 5:待确认下单")
    private Integer stage;

    @ApiModelProperty("电话号码")
    private String phone;

}
