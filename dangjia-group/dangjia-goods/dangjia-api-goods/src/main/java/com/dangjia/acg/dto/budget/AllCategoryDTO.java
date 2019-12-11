package com.dangjia.acg.dto.budget;

import lombok.Data;

@Data
public class AllCategoryDTO {
    private String id;//标签id
    private String name;//标签名称
    private Double priceArr;//全部金额 1代表我们购,2代表自购
    private Double sfpriceArr;//自购金额
}
