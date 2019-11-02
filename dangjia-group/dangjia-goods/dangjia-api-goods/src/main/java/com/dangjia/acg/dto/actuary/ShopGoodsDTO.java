package com.dangjia.acg.dto.actuary;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author: qiyuxiang
 * Date: 2019-09-21
 */
@Data
public class ShopGoodsDTO {

    private String shopId;//店铺ID
    private String shopName;//店铺名称
    private BigDecimal totalMaterialPrice;//实物总价
    private List<BudgetLabelDTO> labelDTOS;//分类标签下对应的商品集
}
