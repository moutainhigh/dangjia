package com.dangjia.acg.dto.worker;

import lombok.Data;

@Data
public class CraftsmenListDTO {
    private String workerId;//工匠id
    private String head;//头像
    private String workerTypeId;//工种id
    private String workerTypeName;//工种id
    private String workerTypeColor;//颜色
    private String name;//姓名
    private String mobile;//电话
}
