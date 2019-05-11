package com.dangjia.acg.dto.budget;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/2/27 0027
 * Time: 11:21
 */
@Data
public class BudgetDTO {
    private Double workerPrice;//人工
    private Double caiPrice;//材料服务
    private BigDecimal totalPrice;//总计

    private List<BudgetItemDTO> budgetItemDTOList;

}
