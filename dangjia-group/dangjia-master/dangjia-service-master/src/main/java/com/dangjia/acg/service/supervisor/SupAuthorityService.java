package com.dangjia.acg.service.supervisor;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.supervisor.*;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class SupAuthorityService {
    @Autowired
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;

    /**
     * 查看申请信息
     *
     * @param request
     * @return
     */
    public ServerResponse queryApplicationInfo(HttpServletRequest request, String houseId, PageDTO pageDTO) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<MaintenanceRecordDTO> list = djMaintenanceRecordMapper.queryApplicationInfo(houseId);
            list.forEach(maintenanceRecordDTO -> {
                maintenanceRecordDTO.setOwnerImage(StringTool.getImage(maintenanceRecordDTO.getOwnerImage(), address));
                maintenanceRecordDTO.setOwnerImageDetail(StringTool.getImage(maintenanceRecordDTO.getOwnerImage(), address).split(","));
            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("增加已选异常");
        }
    }

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

    /**
     * （维修)工地列表
     *
     * @param request
     * @param pageDTO
     * @param userToken
     * @param keyWord
     * @return
     */
    public ServerResponse queryMaintenanceHostList(HttpServletRequest request, PageDTO pageDTO, String userToken, String keyWord) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            List<RepairHouseListDTO> list = djMaintenanceRecordMapper.queryMaintenanceHostList(worker.getId(), keyWord);
            list.forEach(repairHouseListDTO -> {
                String houseId = repairHouseListDTO.getHouseId();
                List<DjMaintenanceRecord> maintenanceRecordList = djMaintenanceRecordMapper.queryMaintenanceRecord(worker.getId(), houseId);
                StringBuffer sb = new StringBuffer();
                maintenanceRecordList.forEach(djMaintenanceRecord -> {
                    String workerMemberId = djMaintenanceRecord.getWorkerMemberId();
                    if (workerMemberId != null) {
                        Member member = memberMapper.selectByPrimaryKey(workerMemberId);//工匠信息
                        String workerTypeName = "";
                        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());//查询工种
                        workerTypeName = workerType != null ? workerType.getName() : "";
                        if (StringUtil.isNotEmpty(workerTypeName)) {
                            sb.append(workerTypeName).append("、");//工种名称
                        }
                    }
                });
                repairHouseListDTO.setTodayConstruction(sb != null && sb.length() > 0 ? sb.toString().substring(0, sb.toString().length() - 1) : "");
            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("（维修)工地列表异常");
        }
    }

    /**
     * （维修）工地详情
     *
     * @param request
     * @param houseId
     * @return
     */
    public ServerResponse queryMtHostListDetail(HttpServletRequest request, String houseId, String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            MtHostListDetailDTO mtHostListDetailDTO = djMaintenanceRecordMapper.queryMtHostListDetail(houseId);
            return ServerResponse.createBySuccess("查询成功", mtHostListDetailDTO);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("（维修）工地详情异常");
        }
    }

}
