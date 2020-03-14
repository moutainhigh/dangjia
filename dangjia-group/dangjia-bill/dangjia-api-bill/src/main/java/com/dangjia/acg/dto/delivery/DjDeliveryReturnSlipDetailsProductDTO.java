package com.dangjia.acg.dto.delivery;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 下午 4:07
 */
@Data
public class DjDeliveryReturnSlipDetailsProductDTO {
    private String productName;
    private String productSn;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrices;
    private String image;
}
