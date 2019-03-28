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

    @Column(name = "talk_content")
    @Desc(value = "谈话内容")
    @ApiModelProperty("谈话内容")
    private String talkContent;

}
