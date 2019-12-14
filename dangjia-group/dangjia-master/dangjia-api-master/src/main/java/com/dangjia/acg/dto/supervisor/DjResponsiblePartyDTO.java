package com.dangjia.acg.dto.supervisor;

import lombok.Data;

import java.util.List;

@Data
public class DjResponsiblePartyDTO {
    private  String responsiblePartyId;//店鋪id或者用戶id
    private  String responsiblePartyType;//维保责任方类型
    private  String responsiblePartyTypeName;//维保责任方类型名称
    List<StoreMaintenanceDTO> listStoreMaintenance;
    List<MemberMaintenanceDTO> listMemberMaintenance;

}
