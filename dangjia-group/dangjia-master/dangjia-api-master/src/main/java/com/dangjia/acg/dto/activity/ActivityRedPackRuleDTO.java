package com.dangjia.acg.dto.activity;


import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - 优惠券拆分规则
 */
@Data
public class ActivityRedPackRuleDTO {

	private ActivityRedPack redPack;

	protected String id;

	@ApiModelProperty("创建时间")
	protected Date createDate;// 创建日期

	@ApiModelProperty("修改时间")
	protected Date modifyDate;// 修改日期

	@ApiModelProperty("数据状态 0=正常，1=删除")
	protected int dataStatus;

	@ApiModelProperty("优惠券ID")
	private String activityRedPackId;


	@ApiModelProperty("数量")
	private Integer num;//


	@ApiModelProperty("面值金额")
	private  BigDecimal money;//

	@ApiModelProperty("满足使用条件的金额数")
	private BigDecimal satisfyMoney;//
	
	private String name; //优惠名
	private String nameType; //优惠类型名
	private String validTime;//格式:  有效期
	private String share;//说明
	private String redPackRuleInfo;//满减说明

	public void toConvert(){
		if (this.redPack.getType() == 0) {
			this.setNameType("满减券");
			this.setRedPackRuleInfo("满"+this.satisfyMoney.setScale(2, BigDecimal.ROUND_HALF_UP)+"可用");
		}else if(this.redPack.getType() == 1){
			this.setNameType("折扣券");
		}else{
			this.setNameType("代金券");
		}
		this.setValidTime(
				"有效期:"
						+ DateUtil.getDateString2(this.redPack.getStartDate().getTime())
						+"至"
						+ DateUtil.getDateString2(this.redPack.getEndDate().getTime())
		);
		redPack.setIsShare(1);
		if (this.redPack.getFromObjectType() == 0) {
			this.setShare("限人工:此优惠券仅限某些工种使用");
		}else if (this.redPack.getFromObjectType() == 1) {
			this.setShare("限材料:仅可购买材料类指定商品");
		}else{
			this.setShare("限商品:仅可购买指定商品");
		}
		this.setName(this.redPack.getName());
	}

}