package com.dangjia.acg.dto.actuary;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

/**
 * 实体类 - 精算阶段花费统计
 */
@Data
@FieldNameConstants(prefix = "")
public class BudgetStageCostDTO extends BaseEntity {

    @Desc(value = "房子ID")
    private String houseId;


    @Desc(value = "订单总额")
    private BigDecimal totalAmount;

    @ApiModelProperty("工种id")
    private String workerTypeId;

    @ApiModelProperty("0：材料；1：包工包料 2：人工 ")
    private Integer type;


}