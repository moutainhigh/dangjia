package com.dangjia.acg.modle.supervisor;

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

@Data
@Entity
@Table(name = "dj_basics_patrol_record")
@ApiModel(description = "巡查记录表")
@FieldNameConstants(prefix = "")
public class PatrolRecord extends BaseEntity {


    @Column(name = "member_id")
    @Desc(value = "用户ID(记录人或者督导人)")
    @ApiModelProperty("用户ID(记录人或者督导人)")
    private String memberId;


    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;


    @Column(name = "images")
    @Desc(value = "巡查图片")
    @ApiModelProperty("巡查图片")
    private String images;


    @Column(name = "content")
    @Desc(value = "巡查工作及处置情况")
    @ApiModelProperty("巡查工作及处置情况")
    private String content;


    @Column(name = "type")
    @Desc(value = "0:奖励;1:处罚,2:巡查")
    @ApiModelProperty("0:奖励;1:处罚,2:巡查")
    private Integer type;


    @Column(name = "reward_punish_id")
    @Desc(value = "奖罚ID")
    @ApiModelProperty("奖罚ID")
    private String rewardPunishId;

}
