package com.dangjia.acg.dto.supervisor;

import lombok.Data;

/**
 * 维修工地列表
 */
@Data
public class RepairHouseListDTO {
    private String area;//地址
    private String sincePurchaseAmount;//维保金额
    private String address;//房子地址
    private  String  houseId;// 房子id
    private String todayConstruction;//今日施工
}
