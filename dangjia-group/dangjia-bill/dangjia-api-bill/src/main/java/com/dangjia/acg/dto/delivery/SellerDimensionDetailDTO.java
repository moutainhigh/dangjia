package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * 买家维度详情DTO
 */
@Data
public class SellerDimensionDetailDTO {

    private String orderSplitId;//订单号
    private Date createDate;//下单时间
    private Double price;//成本价(供应商提供的价格)
    private Double sellPrice;//销售价
    private Double profit ;//利润

}
