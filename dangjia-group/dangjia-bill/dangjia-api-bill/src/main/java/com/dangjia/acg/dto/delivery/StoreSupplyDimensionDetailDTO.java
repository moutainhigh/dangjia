package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: chenyufeng
 * Date: 11/11/2019
 * Time: 上 3:00
 */
@Data
public class StoreSupplyDimensionDetailDTO {
    private String image;// 商品图片
    private String productName;//商品名称
    private String productSn;//商品编号
    private Double price; //价格
    private Integer stock; //库存
    private Double  income;//收入
    private String supId;//供应商id
    private String productId;//商品id

    //发货单号 房子地址 下单时间 本次供货数 成本价 售价  利润
}
