package com.dangjia.acg.dto.basics;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
@ApiModel
public class BudgetResult {
	private String budgetDec;//预算说明
	private Double cost;//总费用
	private Double workerBudget;//人工费用
	private Double materialBudget;//材料费用
	private List<BudgetListResult> bigList;//人工费List
	private List<BudgetListResult> caiList;//材料费List
    private String mianji;//面积
    private String danjia;//单价

}
