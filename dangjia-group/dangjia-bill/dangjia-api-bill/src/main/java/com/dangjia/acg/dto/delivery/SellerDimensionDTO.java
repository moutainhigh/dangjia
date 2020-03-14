package com.dangjia.acg.dto.delivery;

import lombok.Data;

/**
 * 买家维度DTO
 */
@Data
public class SellerDimensionDTO {

    private String storefrontId;//卖家ID
    private String shipAddress;//收货地址
    private String name;//业主名称
    private Double income;//收入
    private Double expenditure;//支出
    private Double profit ;//利润
    private String houseId;//房子ID
}
