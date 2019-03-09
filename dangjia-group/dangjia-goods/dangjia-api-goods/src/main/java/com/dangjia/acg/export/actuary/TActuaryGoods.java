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

    @ExcelField(titile = "用户删除状态",offset = 4)//用户删除状态·,0表示未支付，1表示已删除,2表示业主取消,3表示已经支付,4再次购买
    private String deleteState;

    @ExcelField(titile = "商品数量",offset = 5)
    private Double productNum;

    @ExcelField(titile = "商品单位",offset = 6)
    private String unit;

    @ExcelField(titile = "单价",offset = 7)
    private Double price;

    @ExcelField(titile = "总价格",offset = 8)
    private Double priceTotal;
}