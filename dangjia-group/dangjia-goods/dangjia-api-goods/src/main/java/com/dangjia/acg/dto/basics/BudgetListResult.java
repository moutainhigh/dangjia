package com.dangjia.acg.dto.basics;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
@Data
@ApiModel
public class BudgetListResult { 
	private String listName;// 名称
	private String listCost;// 预算
	private String url;// 详情listUrl

}
