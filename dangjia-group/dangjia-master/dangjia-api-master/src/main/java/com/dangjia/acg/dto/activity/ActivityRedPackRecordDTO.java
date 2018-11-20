package com.dangjia.acg.dto.activity;


import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 优惠券记录
 */
@Data
public class ActivityRedPackRecordDTO {

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
}