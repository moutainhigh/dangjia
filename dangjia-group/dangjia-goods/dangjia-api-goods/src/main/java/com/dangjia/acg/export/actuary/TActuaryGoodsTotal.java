package com.dangjia.acg.export.actuary;

import com.dangjia.acg.common.annotation.ExcelField;
import lombok.Data;

/**
 * 导出精算材料汇总表
 * @author ysl
 */
@Data
public class TActuaryGoodsTotal {
    @ExcelField(titile = "工序",offset = 1)//工序名称
    private String name;

    @ExcelField(titile = "性质",offset = 2) //材料，服务，人工
    private String goodsType;

    @ExcelField(titile = "参考总花费",offset = 3)
    private Double priceTotal;

}