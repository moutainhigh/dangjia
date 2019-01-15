package com.dangjia.acg.modle.worker;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - 提现申请
 * @author ronalcheng
 */
@Data
@Entity
@Table(name = "dj_worker_withdraw_deposit")
@ApiModel(description = "提现申请")
public class WithdrawDeposit extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "工匠姓名")
	@ApiModelProperty("工匠姓名")
	private String name;

	@Column(name = "worker_id")
	@Desc(value = "工人id")
	@ApiModelProperty("工人id")
	private String workerId;//workerid

	/*@Column(name = "worker_type_id")
	@Desc(value = "工种类型id")
	@ApiModelProperty("工种类型id")
	private String workerTypeId;//workertypeid*/

	@Column(name = "state")
	@Desc(value = "0未处理,1已处理")
	@ApiModelProperty("0未处理,1已处理")
	private Integer state;//

	@Column(name = "money")
	@Desc(value = "本次提现金额")
	@ApiModelProperty("本次提现金额")
	private BigDecimal money;//

	@Column(name = "bank_name")
	@Desc(value = "银行名字")
	@ApiModelProperty("银行名字")
    private String bankName;//

	@Column(name = "card_number")
	@Desc(value = "卡号")
	@ApiModelProperty("卡号")
    private String cardNumber;//

	@Column(name = "processing_date")
	@Desc(value = "处理时间")
	@ApiModelProperty("处理时间")
    private Date processingDate;//

}














