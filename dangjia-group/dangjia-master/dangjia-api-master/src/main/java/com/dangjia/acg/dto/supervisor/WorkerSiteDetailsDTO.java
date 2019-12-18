package com.dangjia.acg.dto.supervisor;

import lombok.Data;

import java.util.List;

@Data
public class WorkerSiteDetailsDTO {
    private String address;//房子地址
    private String latitude;//坐标
    private String longitude;//坐标
    private String houseId;//房子id
    HouseKeeperDTO houseKeeperDTO; //大管家实体(工种名称、姓名、工期、巡查、验收)
    List<CraftsManDTO> craftsManDTOList;//普通工匠实体(工种名称、姓名、完工状态、工期、节点)
}
