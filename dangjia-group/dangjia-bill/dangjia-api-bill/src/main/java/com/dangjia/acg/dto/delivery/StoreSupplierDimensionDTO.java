package com.dangjia.acg.dto.delivery;

import lombok.Data;

/**
 * 店铺利润统计--供应商维度
 */
@Data
public class StoreSupplierDimensionDTO {
    //供应商名称
    private String name;
    //联系人
    private String checkPeople;
    //联系号码
    private String telephone;
    //收入
    private Double income;
    //支出
    private Double expenditure;
    //利润
    private Double profit;

}
