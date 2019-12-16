package com.dangjia.acg.dto.supervisor;

import lombok.Data;

@Data
public class WorkerSiteDetailsDTO {
    private String address;//房子地址
    private String latitude;//坐标
    private String longitude;//坐标
    private String houseId;//房子id
}
