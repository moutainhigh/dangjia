package com.dangjia.acg.modle.safe;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 实体类 - 保险类型
 */
@Data
@Entity
@Table(name = "dj_safe_worker_type_safe")
@ApiModel(description = "补材料表")
public class WorkerTypeSafe extends BaseEntity {

	@Column(name = "name")
	@Desc(value = "保险名称")
	@ApiModelProperty("保险名称")
	private String name;//

	@Column(name = "worker_type_id")
	@Desc(value = "工种类型的id")
	@ApiModelProperty("工种类型的id")
	private String workerTypeId;//workertyid

	@Column(name = "worker_type")
	@Desc(value = "工种类型1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆")
	@ApiModelProperty("工种类型1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆")
	private int workerType;//workertype

	@Column(name = "price")
	@Desc(value = "价格")
	@ApiModelProperty("价格")
	private BigDecimal price;

	@Column(name = "safe_default")
	@Desc(value = "1为默认,价格免费")
	@ApiModelProperty("1为默认,价格免费")
	private Integer safeDefault;//safedefault

	@Column(name = "order_list")
	@Desc(value = "排序")
	@ApiModelProperty("排序")
	private Integer orderList;

	@Column(name = "month")
	@Desc(value = "保险时长,按月计算")
	@ApiModelProperty("保险时长,按月计算")
	private Integer month;
	
}