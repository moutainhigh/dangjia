package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 17/10/2019
 * Time: 下午 3:00
 */
@Data
public class SupplyDimensionDTO {
    private String image;
    private String productName;
    private String productSn;
    //价格
    private Double price;
    //库存
    private Integer stock;
    private Double  income;
    private String supId;
    private String productId;

    List<BuyersDimensionDTO> buyersDimensionDTOS;
}
