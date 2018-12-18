package com.dangjia.acg.dto.basics;

import io.swagger.annotations.ApiModel;
import lombok.Data;
@Data
@ApiModel
public class RlistResult {
	private String rId;
	private String workerId;
	private String rName;
	private Double rCost;//单价
	private Double sumRcost;//总价
	private Double number;//数量
	
}
