package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: chenyufeng
 * 店铺商品维度子列表
 * Date: 11/11/2019
 * Time: 上 3:00
 */
@Data
public class StoreSupplyDimensionDetailDTO {

    private  String  number;//发货单号
    private  String  shipAddress;//房子地址
    private  String  submitTime;//下单时间
    private String supId;//供应商id
    private  String  supplyQuantity;//本次供货数
    private  String  applyMoney;//成本价
    private  String  totalAmount;//售价
    private  String profit ;//利润

}
