package com.dangjia.acg.modle.other;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 银行卡类型
 */
@Data
@Entity
@Table(name = "dj_other_bank_card")
@ApiModel(description = "银行卡类型")
public class BankCard extends BaseEntity {

	@Column(name = "bank_name")
	@Desc(value = "银行名称")
	@ApiModelProperty("银行名称")
	private String bankName;//

	@Column(name = "bk_max_amt")
	@Desc(value = "最大取现金额")
	@ApiModelProperty("最大取现金额")
	private String bkMaxAmt;//

	@Column(name = "bk_min_amt")
	@Desc(value = "最小取现金额")
	@ApiModelProperty("最小取现金额")
	private String bkMinAmt;//

	@Column(name = "bank_card_image")
	@Desc(value = "保险名称")
	@ApiModelProperty("图片路径")
	private String bankCardImage;//

	@Column(name = "sign")
	@Desc(value = "标识")
	@ApiModelProperty("标识")
	private String sign;//
}
