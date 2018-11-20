package com.dangjia.acg.modle.house;


import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 实体类 - 回访记录关联到人
 */
@Data
@Entity
@Table(name = "dj_house_house_visit")
@ApiModel(description = "回访记录")
public class HouseVisit extends BaseEntity {


	@Column(name = "member_id")
	@Desc(value = "业主id")
	@ApiModelProperty("业主id")
	private String memberId;//改动为回访记录关联到人

	@Column(name = "visit_state")
	@Desc(value = "0未回访，1有需求立刻开工，2有意向继续跟进，3无装修需求，4恶意操作")
	@ApiModelProperty("0未回访，1有需求立刻开工，2有意向继续跟进，3无装修需求，4恶意操作")
	private Integer visitState;//visitstate

	@Column(name = "visit_result")
	@Desc(value = "补充说明")
	@ApiModelProperty("补充说明")
	private String visitResult;//visitresult

	@Column(name = "visit_person")
	@Desc(value = "回访人")
	@ApiModelProperty("回访人")
	private String visitPerson;//visitperson

	@Column(name = "next_date")
	@Desc(value = "下次回访时间")
	@ApiModelProperty("下次回访时间")
	private Date nextDate;
}