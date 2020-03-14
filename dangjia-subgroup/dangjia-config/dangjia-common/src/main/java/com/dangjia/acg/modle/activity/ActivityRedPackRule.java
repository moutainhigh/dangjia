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

/**
 * 实体类 - 优惠券拆分规则
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_activity_red_pack_rule")
@ApiModel(description = "优惠券拆分规则")
public class ActivityRedPackRule extends BaseEntity {



	@Column(name = "activity_red_pack_id")
	@Desc(value = "优惠券ID")
	@ApiModelProperty("优惠券ID")
	private String activityRedPackId;


	@Column(name = "num")
	@Desc(value = "数量")
	@ApiModelProperty("数量")
	private Integer num;//


	@Column(name = "money")
	@Desc(value = "面值金额")
	@ApiModelProperty("面值金额")
	private  BigDecimal money;//


	@Column(name = "satisfy_money")
	@Desc(value = "满足使用条件的金额数")
	@ApiModelProperty("满足使用条件的金额数")
	private BigDecimal satisfyMoney;//


}