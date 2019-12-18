package com.dangjia.acg.dto.supervisor;

import lombok.Data;

/**
 * 大管家实体(工种名称、姓名、工期、巡查、验收)
 */
@Data
public class HouseKeeperDTO {

    private String workerTypeName;//工种名称
    private String name;//姓名
    private String projectTime;//工期
    private String patrol;//巡查
    private String checkTimes; //验收
}
