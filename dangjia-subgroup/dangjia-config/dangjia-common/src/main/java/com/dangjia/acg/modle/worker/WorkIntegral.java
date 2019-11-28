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
 * 实体类 - 工人积分明细
 */
@Data
@Entity
@Table(name = "dj_worker_work_integral")
@ApiModel(description = "工人积分明细")
@FieldNameConstants(prefix = "")
public class WorkIntegral extends BaseEntity {

	@Column(name = "worker_id")
	@Desc(value = "工人ID")
	@ApiModelProperty("工人ID")
	private String workerId;//workerid

	@Column(name = "member_id")
	@Desc(value = "评价的业主ID")
	@ApiModelProperty("评价的业主ID")
	private String memberId;//memberid

	@Column(name = "butler_id")
	@Desc(value = "评价的大管家ID与worker中大管家id对应")
	@ApiModelProperty("评价的大管家ID与worker中大管家id对应")
	private String butlerId;//butlerid

	@Column(name = "star")
	@Desc(value = "业主或大管家所给星数")
	@ApiModelProperty("业主或大管家所给星数")
	private Integer star;//

	@Column(name = "status")
	@Desc(value = "得分类型每日积分0，业主积分1，大管家积分2")
	@ApiModelProperty("得分类型每日积分0，业主积分1，大管家积分2")
	private Integer status;//

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "briefed")
	@Desc(value = "评分描述")
	@ApiModelProperty("评分描述")
	private String briefed;

	@Column(name = "integral")
	@Desc(value = "积分增减分数")
	@ApiModelProperty("积分增减分数")
	private BigDecimal integral;
}