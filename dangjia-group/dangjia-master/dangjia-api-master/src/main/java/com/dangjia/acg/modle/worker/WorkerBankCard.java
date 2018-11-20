package com.dangjia.acg.modle.worker;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 工人关联银行卡
 */
@Data
@Entity
@Table(name = "dj_worker_worker_bank_card")
@ApiModel(description = "银行卡类型")
public class WorkerBankCard extends BaseEntity {

	@Column(name = "worker_id")
	@Desc(value = "工人ID")
	@ApiModelProperty("工人ID")
	private String workerId; //workerid

	@Column(name = "card_holder")
	@Desc(value = "持卡人姓名")
	@ApiModelProperty("持卡人姓名")
	private String cardHolder;//cardholder

	@Column(name = "bank_card_id")
	@Desc(value = "银行卡类型Id")
	@ApiModelProperty("银行卡类型Id")
	private String bankCardId; //bankcardid

	@Column(name = "bank_card_number")
	@Desc(value = "银行卡卡号")
	@ApiModelProperty("银行卡卡号")
	private String bankCardNumber; //bankcardnumber
	
}
