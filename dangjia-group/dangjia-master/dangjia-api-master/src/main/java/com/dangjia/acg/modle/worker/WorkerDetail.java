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

/**
 * 实体类 - 工人流水明细
 */
@Data
@Entity
@Table(name = "dj_worker_worker_detail")
@ApiModel(description = "工人流水明细")
@FieldNameConstants(prefix = "")
public class WorkerDetail extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "说明")
	@ApiModelProperty("说明")
	private String name;//

	@Column(name = "worker_id")
	@Desc(value = "工人id")
	@ApiModelProperty("工人id")
	private String workerId;//workerid

	@Column(name = "worker_name")
	@Desc(value = "工人姓名")
	@ApiModelProperty("工人姓名")
	private String workerName;//workername

	@Column(name = "house_id")
	@Desc(value = "房子id")
	@ApiModelProperty("房子id")
	private String houseId;//houseid

	@Column(name = "money")
	@Desc(value = "money")
	@ApiModelProperty("money")
	private BigDecimal money;

	@Column(name = "state")
	@Desc(value = "0为进账 1为出账 自定义流水状态  2 增加金额 3减少金额")
	@ApiModelProperty("0为进账 1为出账 自定义流水状态  2 增加金额 3减少金额")
	private Integer state;//

	@Column(name = "defined_worker_id")
	@Desc(value = "自定义工人流水id")
	@ApiModelProperty("自定义工人流水id")
	private String definedWorkerId; //

}
