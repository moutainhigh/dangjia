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
	@Desc(value = "本次金额")
	@ApiModelProperty("本次金额")
	private BigDecimal money;//实拿

	@Column(name = "state")
	@Desc(value = "0工钱收入,1提现,2自定义增加金额,3自定义减少金额,4退材料退款,5剩余材料退款,6退人工退款,7运费,8提现驳回到余额，9:提前结束装修退款")
	@ApiModelProperty("0工钱收入,1提现,2自定义增加金额,3自定义减少金额,4退材料退款,5剩余材料退款,6退人工退款,7运费,8提现驳回到余额，9:提前结束装修退款")
	private Integer state;

	@Column(name = "defined_worker_id")
	@Desc(value = "自定义工人流水id")
	@ApiModelProperty("自定义工人流水id")
	private String definedWorkerId;

	/*新增字段*/
	@Column(name = "defined_name")
	@Desc(value = "自定义流水说明")
	@ApiModelProperty("自定义流水说明")
	private String definedName;

	@Column(name = "have_money")
	@Desc(value = "工匠订单当时拿到的钱")
	@ApiModelProperty("工匠订单当时拿到的钱")
	private BigDecimal haveMoney;

	@Column(name = "house_worker_order_id")
	@Desc(value = "工匠订单ID")
	@ApiModelProperty("工匠订单ID")
	private String houseWorkerOrderId;

	@Column(name = "apply_money")
	@Desc(value = "申请的钱")
	@ApiModelProperty("申请的钱")
	private BigDecimal applyMoney;//应拿

	@Column(name = "wallet_money")
	@Desc(value = "钱包余额")
	@ApiModelProperty("钱包当时钱")
	private BigDecimal walletMoney;//对应member表 haveMoney

}
