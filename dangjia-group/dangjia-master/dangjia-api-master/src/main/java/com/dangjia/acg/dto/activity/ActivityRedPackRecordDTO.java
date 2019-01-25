package com.dangjia.acg.dto.activity;


import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - 优惠券记录
 */
@Data
public class ActivityRedPackRecordDTO {
	protected String id;

	@ApiModelProperty("创建时间")
	protected Date createDate;// 创建日期

	@ApiModelProperty("修改时间")
	protected Date modifyDate;// 修改日期
	@ApiModelProperty("可用城市ID")
	private String cityId;//
	@ApiModelProperty("会员ID")
	private String memberId; //memberid

	@ApiModelProperty("存放业主电话")
	private String phone;//


	@ApiModelProperty("对应房子")
	private String houseId;//houseid


	@ApiModelProperty("优惠券状态，0未使用，1已使用，2已过期,3已失效")
	private int haveReceive;//havereceive


	@ApiModelProperty("优惠券规则ID")
	private String redPackRuleId;//

	@ApiModelProperty("优惠券Id")
	private String redPackId;//

	@ApiModelProperty("订单编号")
	private String businessOrderNumber;

	private ActivityRedPack redPack;

	private ActivityRedPackRule redPackRule;

	private String name; //优惠名
	private String nameType; //优惠类型名
	private String validTime;//格式:  有效期
	private String share;//是否公用说明
	private String selected;//是否选中


	@ApiModelProperty("有效开始时间")
	private Date startDate;//

	@ApiModelProperty("有效结束时间")
	private Date endDate;//

	public void toConvert(){
		if (this.getRedPack().getType() == 0) {
			this.setNameType("满减券");
			this.setRedPackRuleId("满"+this.getRedPackRule().getSatisfyMoney().setScale(2, BigDecimal.ROUND_HALF_UP)+"可用");
		}else if(this.getRedPack().getType() == 1){
			this.setNameType("折扣券");
		}else{
			this.setNameType("代金券");
		}
		this.setValidTime(
				"有效期:"
						+ DateUtil.getDateString2(this.getRedPack().getStartDate().getTime())
						+"至"
						+ DateUtil.getDateString2(this.getRedPack().getEndDate().getTime())
		);
		getRedPack().setIsShare(1);
		if (this.getRedPack().getFromObjectType() == 0) {
			this.setShare("限人工:此优惠券仅限某些工种使用");
		}else if (this.getRedPack().getFromObjectType() == 1) {
			this.setShare("限材料:仅可购买材料类指定商品");
		}else{
			this.setShare("限商品:仅可购买指定商品");
		}
		this.setName(this.getRedPack().getName());

		//上一次选中优惠券标记
		if(!CommonUtil.isEmpty(this.getBusinessOrderNumber())){
			this.setSelected("1");
		}else{
			this.setSelected("0");
		}
	}
}