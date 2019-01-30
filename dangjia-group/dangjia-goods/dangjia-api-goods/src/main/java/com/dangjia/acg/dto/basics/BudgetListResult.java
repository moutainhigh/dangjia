package com.dangjia.acg.dto.basics;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class BudgetListResult { 
	private String listName;// 名称
	private BigDecimal listCost;// 预算

}
