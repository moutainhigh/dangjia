package com.dangjia.acg.service.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.supervisor.*;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SupAuthorityService {
    @Autowired
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper;


    /**
     * 查看责任划分
     *
     * @param request
     * @return
     */
    public ServerResponse queryDvResponsibility(HttpServletRequest request, String houseId, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjResponsiblePartyDTO> list = djMaintenanceRecordMapper.queryDvResponsibility(houseId);
            list.forEach(djResponsiblePartyDTO -> {
                String responsiblePartyType = djResponsiblePartyDTO.getResponsiblePartyType();
                String responsiblePartyId = djResponsiblePartyDTO.getResponsiblePartyId();
                //维保责任方类型 1:店铺  3：工匠
                if (responsiblePartyType.equals("1")) {
                    List<StoreMaintenanceDTO> listStoreMaintenance = djMaintenanceRecordMapper.queryStoreMaintenance(responsiblePartyType, responsiblePartyId);
                    djResponsiblePartyDTO.setListStoreMaintenance(listStoreMaintenance);
                }
                if (responsiblePartyType.equals("3")) {
                    List<MemberMaintenanceDTO> listMemberMaintenance = djMaintenanceRecordMapper.queryMemberMaintenance(responsiblePartyType, responsiblePartyId);
                    djResponsiblePartyDTO.setListMemberMaintenance(listMemberMaintenance);
                }
            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("增加已选异常");
        }
    }

}
