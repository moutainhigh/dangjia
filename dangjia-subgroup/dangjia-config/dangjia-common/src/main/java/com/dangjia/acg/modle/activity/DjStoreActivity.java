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
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/15
 * Time: 14:00
 */

@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_store_activity")
@ApiModel(description = "活动主表")
public class DjStoreActivity extends BaseEntity {

    @Column(name = "activity_type")
    @Desc(value = "数据类型 1:限时购，2:拼团购，3:集福")
    @ApiModelProperty("数据类型 1:限时购，3:集福")
    private Integer activityType;

    @Column(name = "city_id")
    @Desc(value = "适应地区id")
    @ApiModelProperty("适应地区id")
    private String cityId;

    @Column(name = "registration_start_time")
    @Desc(value = "报名开始时间")
    @ApiModelProperty("报名开始时间")
    private Date registrationStartTime;

    @Column(name = "registration_end_time")
    @Desc(value = "报名结束时间")
    @ApiModelProperty("报名结束时间")
    private Date registrationEndTime;

    @Column(name = "spell_group")
    @Desc(value = "拼团人数")
    @ApiModelProperty("拼团人数")
    private Integer spellGroup;

    @Column(name = "activity_start_time")
    @Desc(value = "活动开始时间")
    @ApiModelProperty("活动开始时间")
    private Date activityStartTime;

    @Column(name = "activity_end_time")
    @Desc(value = "活动结束时间")
    @ApiModelProperty("活动结束时间")
    private Date activityEndTime;

    @Column(name = "activity_description")
    @Desc(value = "活动说明")
    @ApiModelProperty("活动说明")
    private String activityDescription;

    @Column(name = "cycle_purchasing")
    @Desc(value = "限购周期")
    @ApiModelProperty("限购周期")
    private Integer cyclePurchasing;

    @Column(name = "duration_goods")
    @Desc(value = "商品持续时间")
    @ApiModelProperty("商品持续时间")
    private Integer durationGoods;

    @Column(name = "whether_show")
    @Desc(value = "是否展示 1:是 2:否")
    @ApiModelProperty("是否展示 1:是 2:否")
    private Integer whetherShow;

    @Column(name = "state")
    @Desc(value = "状态 1：开启 2：停用")
    @ApiModelProperty("状态 1：开启 2：停用")
    private Integer state;

    @Column(name = "red_pack_id")
    @Desc(value = "优惠券Id，多个逗号分割")
    @ApiModelProperty("优惠券Id，多个逗号分割")
    private Integer redPackId;

    @Column(name = "red_pack_explain")
    @Desc(value = "优惠券使用说明")
    @ApiModelProperty("优惠券使用说明")
    private Integer redPackExplain;

    @Column(name = "userId")
    @Desc(value = "活动创建人")
    @ApiModelProperty("活动创建人")
    private String userId;

    @Transient
    @Desc(value = "适应地区")
    private String city;

    @Transient
    private List<String> list;
}
