package com.dangjia.acg.modle.house;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 实体类 - 项目(房子)流水明细
 */
@Data
@Entity
@Table(name = "dj_house_house_accounts")
@ApiModel(description = "房子流水明细")
public class HouseAccounts extends BaseEntity {

	@Column(name = "reason")
	@Desc(value = "说明")
	@ApiModelProperty("说明")
	private String reason;//

	@Column(name = "money")
	@Desc(value = "项目总钱")
	@ApiModelProperty("项目总钱")
	private BigDecimal money;//

	@Column(name = "state")
	@Desc(value = "0为进账1为出账")
	@ApiModelProperty("0为进账1为出账")
	private int state;//

	@Column(name = "pay_money")
	@Desc(value = "每次数额")
	@ApiModelProperty("每次数额")
	private BigDecimal payMoney;//paymoney

	@Column(name = "house_id")
	@Desc(value = "房子id")
	@ApiModelProperty("房子id")
	private String houseId;//houseid

	@Column(name = "member_id")
	@Desc(value = "用户ID")
	@ApiModelProperty("用户ID")
	private String memberId;//memberid

	@Column(name = "house_name")
	@Desc(value = "housename")
	@ApiModelProperty("housename")
	private String houseName;//housename

	@Column(name = "name")
	@Desc(value = "工种,工人名字等")
	@ApiModelProperty("工种,工人名字等")
	private String name;//

	@Column(name = "payment")
	@Desc(value = "支付方式1微信, 2支付宝,3后台回调")
	@ApiModelProperty("支付方式1微信, 2支付宝,3后台回调")
	private String payment;//

	@Column(name = "istest")
	@Desc(value = "0不是，1 是测试")
	@ApiModelProperty("0不是，1 是测试")
	private int istest;//
}
