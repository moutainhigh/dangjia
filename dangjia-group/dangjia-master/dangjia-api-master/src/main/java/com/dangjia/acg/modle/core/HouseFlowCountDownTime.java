package com.dangjia.acg.modle.core;

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
 * 记录工人抢单倒计时
 * @author Administrator
 *
 */
@Data
@Entity
@Table(name = "dj_core_house_flow_count_down_time")
@ApiModel(description = "工序")
public class HouseFlowCountDownTime extends BaseEntity {

	@Column(name = "worker_id")
	@Desc(value = "工匠id")
	@ApiModelProperty("工匠id")
	private String workerId;//

	@Column(name = "house_flow_id")
	@Desc(value = "工序id")
	@ApiModelProperty("工序id")
	private String houseFlowId;//

	@Column(name = "count_down_time")
	@Desc(value = "可抢单时间")
	@ApiModelProperty("可抢单时间")
	private Date countDownTime;//

}
