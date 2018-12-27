package com.dangjia.acg.export.actuary;

import com.dangjia.acg.common.annotation.ExcelField;
import lombok.Data;

/**
 * 导出精算材料表
 * @author ysl
 */
@Data
public class TActuaryGoods {
    @ExcelField(titile = "工序名称",offset = 1)//
    private String name;

    @ExcelField(titile = "商品类型",offset = 2)//材料，服务，人工
    private String goodsType;

    @ExcelField(titile = "商品名称",offset = 3)
    private String productName;

    @ExcelField(titile = "商品数量",offset = 4)
    private String productNum;

    @ExcelField(titile = "商品单位",offset = 5)
    private String unit;

    @ExcelField(titile = "单价",offset = 6)
    private String price;

    @ExcelField(titile = "总价格",offset = 7)

    private String priceTotal;
}