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

@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_store_activity_participate")
@ApiModel(description = "店铺活动集福用户参与表")
public class DjStoreActivityParticipate extends BaseEntity {


  @Column(name = "member_id")
  @Desc(value = "参与人ID")
  @ApiModelProperty("参与人ID")
  private String memberId;

  @Column(name = "openid")
  @Desc(value = "参与人第三方认证ID")
  @ApiModelProperty("参与人第三方认证ID")
  private String openid;

  @Column(name = "store_activity_id")
  @Desc(value = "活动ID")
  @ApiModelProperty("活动ID")
  private String storeActivityId;

  @Column(name = "user_name")
  @Desc(value = "参与人昵称")
  @ApiModelProperty("参与人昵称")
  private String userName;

  @Column(name = "user_mobile")
  @Desc(value = "参与人用户名")
  @ApiModelProperty("参与人用户名")
  private String userMobile;

  @Column(name = "city_id")
  @Desc(value = "城市id")
  @ApiModelProperty("城市id")
  private String cityId;

  @Column(name = "city_name")
  @Desc(value = "城市名")
  @ApiModelProperty("城市名")
  private String cityName;

  @Column(name = "address")
  @Desc(value = "详细地址")
  @ApiModelProperty("详细地址")
  private String address;

  @Column(name = "start_time")
  @Desc(value = "开始时间")
  @ApiModelProperty("开始时间")
  private java.util.Date startTime;

  @Column(name = "end_time")
  @Desc(value = "截止时间")
  @ApiModelProperty("截止时间")
  private java.util.Date endTime;

  @Column(name = "score")
  @Desc(value = "当前分数")
  @ApiModelProperty("当前分数")
  private Integer score;

  @Column(name = "target_score")
  @Desc(value = "目标分数")
  @ApiModelProperty("目标分数")
  private Integer targetScore;

  @Column(name = "is_success")
  @Desc(value = "是否集福成功 0=集福中，1=集福成功，2=集福失败")
  @ApiModelProperty("是否集福成功 0=集福中，1=集福成功，2=集福失败")
  private String isSuccess;

  @Column(name = "is_give")
  @Desc(value = "是否领取奖励 0=否，1=是")
  @ApiModelProperty("是否领取奖励 0=否，1=是")
  private String isGive;


}
