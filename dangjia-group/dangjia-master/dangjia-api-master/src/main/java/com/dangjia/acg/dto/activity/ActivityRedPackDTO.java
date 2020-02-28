package com.dangjia.acg.dto.activity;


import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.modle.activity.ActivityRedPackRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 实体类 - 优惠券
 */
@Data
public class ActivityRedPackDTO{


	protected String id;

	@ApiModelProperty("创建时间")
	protected Date createDate;// 创建日期

	@ApiModelProperty("修改时间")
	protected Date modifyDate;// 修改日期

	@ApiModelProperty("优惠券名")
	private String name;

	@ApiModelProperty("来源数据ID")
	private String fromObject;//workertypeid
	@ApiModelProperty("来源数据name")
	private String fromObjectName;//workertypeid

	@ApiModelProperty("来源数据类型 0为人工 1为材料 2为单品,3类别，4货品，5商品，6城市，7店铺")
	private int fromObjectType;//


	@ApiModelProperty("有效开始时间")
	private Date startDate;//

	@ApiModelProperty("有效结束时间")
	private Date endDate;//

	@ApiModelProperty("可用城市ID")
	private String cityId;//

	@ApiModelProperty("发行总数数量")
	private int num;//

	@ApiModelProperty("优惠券剩余总数量")
	private int surplusNums ;//

	@ApiModelProperty("优惠卷类型：1城市券，2店铺券")
	private Integer sourceType;//

	@ApiModelProperty("单人领取次数 默认1")
	private int receiveCount;

	@ApiModelProperty("是否可以与其他优惠券共用，0代表可以共用，1代表不可以共用")
	private int isShare;//isshare

	@ApiModelProperty("优惠券类型 0为减免金额券 1 为折扣券 2代金券")
	private int type; //

	@ApiModelProperty("备注说明适用范围")
	private String remake;//

	private BigDecimal money;//优惠卷面值

	@ApiModelProperty("满足使用条件的金额数")
	private BigDecimal satisfyMoney;//

	@ApiModelProperty("状态，0正常，1停用")
	private int deleteState;//deletestate

	private String status;//优惠卷状态：1发行中，2暂停发放，3已过期，4发送完毕

	private String statusNme;//状态名称

	@ApiModelProperty("优惠券拆分包")
	private List<ActivityRedPackRule> redPackRule;
	@ApiModelProperty("优惠券拆分包(文字说明)")
	private List<ActivityRedPackRuleDTO> redPackRuleDTO;
}