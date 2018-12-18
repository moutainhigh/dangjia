package com.dangjia.acg.dto.basics;

import io.swagger.annotations.ApiModel;
import lombok.Data;
@Data
@ApiModel
public class BudgetListResult { 
	private String listName;// 名称
	private String listCost;// 预算

}
