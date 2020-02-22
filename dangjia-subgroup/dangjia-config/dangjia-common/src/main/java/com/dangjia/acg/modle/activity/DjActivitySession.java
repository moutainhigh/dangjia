package com.dangjia.acg.modle.activity;

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

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/15
 * Time: 14:11
 */

@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_activity_session")
@ApiModel(description = "活动场次表")
public class DjActivitySession extends BaseEntity {

    @Column(name = "session")
    @Desc(value = "场次")
    @ApiModelProperty("场次")
    private Integer session;

    @Column(name = "session_start_time")
    @Desc(value = "场次开始时间")
    @ApiModelProperty("场次开始时间")
    private Date sessionStartTime;

    @Column(name = "end_session")
    @Desc(value = "场次结束时间")
    @ApiModelProperty("场次结束时间")
    private Date endSession;

    @Column(name = "store_activity_id")
    @Desc(value = "店铺活动配置id")
    @ApiModelProperty("店铺活动配置id")
    private String storeActivityId;


}
