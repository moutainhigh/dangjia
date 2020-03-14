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
import java.math.BigDecimal;

@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_store_activity_fraction_rate")
@ApiModel(description = "店铺活动集福分率配置表")
public class DjStoreActivityFractionRate extends BaseEntity {

  @Column(name = "store_activity_id")
  @Desc(value = "活动ID")
  @ApiModelProperty("活动ID")
  private String storeActivityId;

  @Column(name = "probability")
  @Desc(value = "概率")
  @ApiModelProperty("概率")
  private BigDecimal probability;

  @Column(name = "score")
  @Desc(value = "分数")
  @ApiModelProperty("分数")
  private Integer score;


}
