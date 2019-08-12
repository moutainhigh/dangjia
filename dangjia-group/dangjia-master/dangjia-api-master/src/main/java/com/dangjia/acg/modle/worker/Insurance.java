package com.dangjia.acg.modle.worker;

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
import java.util.Date;

/**
 * 实体类 - 工人保险单
 * 
 */
@Data
@Entity
@Table(name = "dj_worker_insurance")
@ApiModel(description = "工人保险单")
@FieldNameConstants(prefix = "")
public class Insurance extends BaseEntity {

	@Column(name = "worker_id")
	@Desc(value = "工人ID")
	@ApiModelProperty("工人ID")
	private String workerId;//

	@Column(name = "worker_name")
	@Desc(value = "工人名称")
	@ApiModelProperty("工人名称")
	private String workerName;

	@Column(name = "worker_mobile")
	@Desc(value = "工人电话")
	@ApiModelProperty("工人电话")
	private String workerMobile;

	@Column(name = "type")
	@Desc(value = "保险类型 0=首保 1=续保")
	@ApiModelProperty("保险类型 0=首保 1=续保")
	private String type;


	@Column(name = "start_date")
	@Desc(value = "有效开始时间")
	@ApiModelProperty("有效开始时间")
	protected Date startDate;


	@Column(name = "end_date")
	@Desc(value = "有效结束时间")
	@ApiModelProperty("有效结束时间")
	protected Date endDate;

	@Column(name = "money")
	@Desc(value = "支付金额")
	@ApiModelProperty("支付金额")
	private BigDecimal money;
}