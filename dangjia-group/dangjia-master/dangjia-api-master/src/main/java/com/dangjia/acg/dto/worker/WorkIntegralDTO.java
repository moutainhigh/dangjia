package com.dangjia.acg.dto.worker;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - 工人积分明细
 */
@Data
public class WorkIntegralDTO {
	protected String id;

	@ApiModelProperty("创建时间")
	protected Date createDate;// 创建日期

	@ApiModelProperty("修改时间")
	protected Date modifyDate;// 修改日期

	@ApiModelProperty("数据状态 0=正常，1=删除")
	protected int dataStatus;

	@ApiModelProperty("工人ID")
	private String workerId;

	@ApiModelProperty("评价的业主ID")
	private String memberId;

	@ApiModelProperty("评价的大管家ID与worker中大管家id对应")
	private String butlerId;

	@ApiModelProperty("业主或大管家所给星数")
	private Integer star;

	@ApiModelProperty("得分类型每日积分0，业主积分1，大管家积分2")
	private int status;//

	@ApiModelProperty("房子ID")
	private String houseId;

	@ApiModelProperty("评分描述")
	private String briefed;

	@ApiModelProperty("积分增减分数")
	private BigDecimal integral;

	private String houseName;//房子名称
}