package com.dangjia.acg.dto.delivery;

import lombok.Data;

@Data
public class CostDetailDTO {
    private String image;//商品图片
    private String stevedorageCost;//搬运费
    private String storefrontName;//店铺名称
    private String transportationCost ;//运费

}
