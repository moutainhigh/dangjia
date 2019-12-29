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

@Data
@Entity
@Table(name = "dj_basics_site_memo_reminder")
@ApiModel(description = "工地备忘录提醒关联表")
@FieldNameConstants(prefix = "")
public class DjBaicsSiteMemoReminder extends BaseEntity {

    @Column(name = "dj_basics_site_memo_id")
    @Desc(value = "工地备忘录表id")
    @ApiModelProperty("工地备忘录表id")
    private String djBasicsSiteMemoId;

    @Column(name = "specify_reminder")
    @Desc(value = "指定提醒人member_id")
    @ApiModelProperty("指定提醒人member_id")
    private String specifyReminder;

    @Column(name = "state")
    @Desc(value = "备忘录状态(0:未看1：已看)")
    @ApiModelProperty("备忘录状态(0:未看1：已看)")
    private String state;

    @Column(name = "send_state")
    @Desc(value = "备忘录提醒状态(0:未提醒1：已提醒)")
    @ApiModelProperty("备忘录提醒状态(0:未提醒1：已提醒)")
    private String sendState;

}
