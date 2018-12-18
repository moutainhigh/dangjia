package com.dangjia.acg.modle.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 -订单锁
 */
@Data
@Entity
@Table(name = "dj_core_house_worker_lock")
@ApiModel(description = "工序")
public class HouseWorkerLock extends BaseEntity {

	@Column(name = "house_id")
	@Desc(value = "房子ID")
	@ApiModelProperty("房子ID")
	private String houseId;//houseid

	@Column(name = "worker_id")
	@Desc(value = "工人ID")
	@ApiModelProperty("工人ID")
	private String workerId;//workerid

	@Column(name = "worker_type")
	@Desc(value = "工种类型1设计师，2精算师，3大管家,4拆除，5打孔，6水电工，7防水，8泥工,9木工，10油漆工，11安装")
	@ApiModelProperty("工种类型1设计师，2精算师，3大管家,4拆除，5打孔，6水电工，7防水，8泥工,9木工，10油漆工，11安装")
	private Integer workerType;//workertype

	@Column(name = "house_flow_id")
	@Desc(value = "工序id")
	@ApiModelProperty("工序id")
	private String houseFlowId;//houseflowid
	
}
