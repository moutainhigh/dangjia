package com.dangjia.acg.dto.actuary;

import lombok.Data;

import java.util.List;

/**
 * author: qiyuxiang
 * Date: 2019-09-21
 */
@Data
public class BudgetLabelDTO {

    private String labelId;//标签ID
    private String labelName;//标签名称
    private String categoryIds;//分类ID组，逗号分隔
    private List<BudgetLabelGoodsDTO> goods;//分类标签下对应的商品集
}
