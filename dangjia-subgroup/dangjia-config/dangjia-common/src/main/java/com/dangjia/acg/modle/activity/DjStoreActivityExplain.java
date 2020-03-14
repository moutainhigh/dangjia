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
@Table(name = "dj_store_activity_explain")
@ApiModel(description = "店铺活动集福活动提示表")
public class DjStoreActivityExplain extends BaseEntity {

  @Column(name = "store_activity_id")
  @Desc(value = "活动ID")
  @ApiModelProperty("活动ID")
  private String storeActivityId;

  @Column(name = "hour_max")
  @Desc(value = "大于")
  @ApiModelProperty("大于")
  private BigDecimal hourMax;

  @Column(name = "hour_min")
  @Desc(value = "小于")
  @ApiModelProperty("小于")
  private BigDecimal hourMin;

  @Column(name = "reminder")
  @Desc(value = "提示语")
  @ApiModelProperty("提示语")
  private String reminder;


}
