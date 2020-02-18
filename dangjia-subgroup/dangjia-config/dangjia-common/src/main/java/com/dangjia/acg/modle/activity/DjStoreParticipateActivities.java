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
 * Date: 2020/2/18
 * Time: 15:39
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_store_participate_activities")
@ApiModel(description = "店铺参加活动表")
public class DjStoreParticipateActivities extends BaseEntity {

    @Column(name = "activity_type")
    @Desc(value = "数据类型 1:限时购，2:拼团购")
    @ApiModelProperty("数据类型 1:限时购，2:拼团购")
    private Integer activityType;

    @Column(name = "store_activity_id")
    @Desc(value = "店铺活动配置id")
    @ApiModelProperty("店铺活动配置id")
    private String storeActivityId;

    @Column(name = "activity_session_id")
    @Desc(value = "活动场次id")
    @ApiModelProperty("活动场次id")
    private String activitySessionId;

    @Column(name = "storefront_id")
    @Desc(value = "店铺id")
    @ApiModelProperty("店铺id")
    private String storefrontId;
}
