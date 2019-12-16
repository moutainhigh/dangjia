package com.dangjia.acg.dto.supervisor;

import lombok.Data;

/**
 *人工详情
 */
@Data
public class MemberMaintenanceDTO {

    private String head;//头像
    private String name;//姓名
    private String workerTypeId;//工种
    private String workerTypeName;//工种名称
    private String proportion;//占比
}
