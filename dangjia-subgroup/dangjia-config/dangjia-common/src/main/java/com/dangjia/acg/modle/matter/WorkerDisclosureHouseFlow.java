package com.dangjia.acg.modle.matter;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 交底事项关联工序
 * 原表名 WorkerDisclosureHouseflow
 */
@Data
@Entity
@Table(name = "dj_matter_worker_disclosure_house_flow")
@ApiModel(description = "交底事项关联工序")
@FieldNameConstants(prefix = "")
public class WorkerDisclosureHouseFlow extends BaseEntity {

	@Column(name = "state")
	@Desc(value = "状态1选择,2扫码成功,3删除")
	@ApiModelProperty("状态1选择,2扫码成功,3删除")
	private Integer state;//

	@Column(name = "worker_disclo_id")
	@Desc(value = "交底事项")
	@ApiModelProperty("交底事项")
	private String workerDiscloId;

	@Column(name = "house_flow_id")
	@Desc(value = "工序id")
	@ApiModelProperty("工序id")
	private String houseFlowId;//houseflowId
}
