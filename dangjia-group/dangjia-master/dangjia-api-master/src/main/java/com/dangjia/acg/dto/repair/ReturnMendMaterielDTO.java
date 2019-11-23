package com.dangjia.acg.dto.repair;

import com.dangjia.acg.modle.repair.MendMateriel;
import lombok.Data;

import java.util.List;

@Data
public class ReturnMendMaterielDTO {
    private List<MendMateriel> mendMaterielList;
    List<ReturnOrderProgressDTO> MendMaterielProgressList;
    private Double  totalPrice;
}
