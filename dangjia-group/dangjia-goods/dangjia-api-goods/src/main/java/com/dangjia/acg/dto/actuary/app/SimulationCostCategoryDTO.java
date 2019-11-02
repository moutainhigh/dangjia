package com.dangjia.acg.dto.actuary.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
public class SimulationCostCategoryDTO {



    @ApiModelProperty("大类价格")
    private String categoryId;

    @ApiModelProperty("大类名称")
    private String categoryName;

    @ApiModelProperty("总花费")
    private BigDecimal totalPrice;

    private List productList;//商品列有

}
