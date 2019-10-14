package com.dangjia.acg.dto.deliver;

import com.dangjia.acg.common.annotation.ExcelField;
import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/22 0022
 * Time: 14:12
 */
@Data
public class SplitDeliverItemDTO {


    @ExcelField(titile = "供应商", offset = 1)
    private String supplierName;//
    @ExcelField(titile = "工地名称", offset = 2)
    private String houseName;//工地名称
    private String image;//图片

    @ExcelField(titile = "商品名称", offset = 3)
    private String productName;//名字
    private String productSn;//编号
    private Double shopCount;//购买总数
    private Double num;//本次发货数量

    @ExcelField(titile = "单位", offset = 7)
    private String unitName;//单位
    private String brandSeriesName;//品牌系列
    private Double cost;//平均成本价
    private Double price;//销售价
    private Double totalPrice; //销售价*发货数量
    private String id;

    @ExcelField(titile = "收货数量", offset = 4)
    private Double receive;//收货数量

    @ExcelField(titile = "成本单价", offset = 5)
    private Double supCost;//选择的供应商提供的单价

    @ExcelField(titile = "成本总价", offset = 6)
    private Double supCostTotal;//成本总价


    private Double askCount;//要货数量
}
