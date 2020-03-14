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
@Table(name = "dj_store_activity_support")
@ApiModel(description = "店铺活动集福用户助力表")
public class DjStoreActivitySupport extends BaseEntity {


  @Column(name = "participate_id")
  @Desc(value = "参与用户ID")
  @ApiModelProperty("参与用户ID")
  private String participateId;

  @Column(name = "probability")
  @Desc(value = "助力人第三方认证ID")
  @ApiModelProperty("助力人第三方认证ID")
  private String openid;

  @Column(name = "store_activity_id")
  @Desc(value = "活动ID")
  @ApiModelProperty("活动ID")
  private String storeActivityId;

  @Column(name = "user_name")
  @Desc(value = "助力人昵称")
  @ApiModelProperty("助力人昵称")
  private String userName;

  @Column(name = "user_mobile")
  @Desc(value = "助力人用户名")
  @ApiModelProperty("助力人用户名")
  private String userMobile;

  @Column(name = "user_head")
  @Desc(value = "助力人头像")
  @ApiModelProperty("助力人头像")
  private String userHead;

  @Column(name = "time")
  @Desc(value = "助力时间")
  @ApiModelProperty("助力时间")
  private java.util.Date time;

  @Column(name = "score")
  @Desc(value = "助力分数")
  @ApiModelProperty("助力分数")
  private Integer score;


}
