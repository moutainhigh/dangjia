package com.dangjia.acg.dto.activity;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/18
 * Time: 16:20
 */
@Data
public class DjStoreActivityDTO extends BaseEntity {

    @Desc(value = "数据类型 1:限时购，2:拼团购")
    @ApiModelProperty("数据类型 1:限时购，2:拼团购")
    private Integer activityType;

    @Desc(value = "状态 1：开启 2：停用")
    @ApiModelProperty("状态 1：开启 2：停用")
    private Integer state;

    @Desc(value = "是否参加 0:未参加 1：已参加")
    @ApiModelProperty("是否参加 0:未参加 1：已参加")
    private Integer attended;

    @Desc(value = "场次")
    @ApiModelProperty("场次")
    private Integer session;

    @Desc(value = "报名开始时间")
    @ApiModelProperty("报名开始时间")
    private Date registrationStartTime;

    @Desc(value = "报名结束时间")
    @ApiModelProperty("报名结束时间")
    private Date endTimeRegistration;

    @Desc(value = "活动开始时间")
    @ApiModelProperty("活动开始时间")
    private Date activityStartTime;

    @Desc(value = "活动结束时间")
    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @Desc(value = "场次开始时间")
    @ApiModelProperty("场次开始时间")
    private Date sessionStartTime;

    @Desc(value = "场次结束时间")
    @ApiModelProperty("场次结束时间")
    private Date endSession;

    @Desc(value = "活动地区")
    @ApiModelProperty("活动地区")
    private String city;

    @Desc(value = "报名状态")
    @ApiModelProperty("报名状态")
    private Integer registrationStatus;

    @Desc(value = "活动说明")
    @ApiModelProperty("活动说明")
    private String activityDescription;


    private String storeActivityId;//活动id

    private String activitySessionId;//场次id

    private String storeParticipateActivitiesId;//店铺参与活动表id
}
