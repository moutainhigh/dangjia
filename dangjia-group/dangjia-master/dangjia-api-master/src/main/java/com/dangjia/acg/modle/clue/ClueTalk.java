package com.dangjia.acg.modle.clue;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "dj_clue_talk")
@ApiModel(description = "线索谈话记录")
@FieldNameConstants(prefix = "")
public class ClueTalk extends BaseEntity {

    @Column(name = "clue_id")
    @Desc(value = "线索ID")
    @ApiModelProperty("线索ID")
    private String clueId;

    @Column(name = "user_id")
    @Desc(value = "操作人ID（客服）")
    @ApiModelProperty("操作人ID（客服）")
    private String userId;

    @Column(name = "talk_content")
    @Desc(value = "谈话内容")
    @ApiModelProperty("谈话内容")
    private String talkContent;

    @Column(name = "remind_time")
    @Desc(value = "提醒时间")
    @ApiModelProperty("提醒时间")
    private Date remindTime;


}
