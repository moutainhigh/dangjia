package com.dangjia.acg.dto.supervisor;

import lombok.Data;

@Data
public class AcceptanceTrendDetailDTO {
    private String workerType;//工种
    private String name;//工人姓名
    private String stewardImage;//图片
    private String[] stewardImageDetail;//图片
    private String stewardRemark;//内容
    private String star; //业主评分
    private String content;//业主评价
    private String workerTypeName ;//工种名称
}
