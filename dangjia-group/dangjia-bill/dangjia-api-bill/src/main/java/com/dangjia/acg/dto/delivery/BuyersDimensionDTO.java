package com.dangjia.acg.dto.delivery;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * 买家维度DTO
 * Date: 16/10/2019
 * Time: 下午 4:34
 */
@Data
public class BuyersDimensionDTO {

    private String supId;
    private String shipAddress;
    private String name;
    private String mobile;
    private Integer supplyQuantity;//数量
    private Double income;//收入
    private String houseId;

}
