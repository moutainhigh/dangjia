package com.dangjia.acg.dto.repair;

import com.dangjia.acg.modle.repair.MendMateriel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReturnMendMaterielDTO {
    private List<MendMateriel> mendMaterielList;
    List<ReturnOrderProgressDTO> MendMaterielProgressList;
    private Double  totalPrice;
    private String imageArr; //相关凭证
    private String returnReason;
    private String state;

}
