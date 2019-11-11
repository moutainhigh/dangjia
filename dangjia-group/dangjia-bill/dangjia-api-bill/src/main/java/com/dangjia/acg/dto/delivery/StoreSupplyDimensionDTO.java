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
public class StoreSupplyDimensionDTO {
    private String image;// 商品图片
    private String productName;//商品名称
    private String productSn;//商品编号
    private Double price; //价格
    private Integer stock; //库存
    private Double  income;//收入
    private String supId;//供应商id
    private String productId;//商品id
    List<StoreSupplyDimensionDetailDTO> toreSupplyDimensionDetailDTO;
    //商品图片 商品名称  商品编号  总购买数 供货数 成本总价 销售总价 利润

}
