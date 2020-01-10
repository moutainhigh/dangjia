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
     * 工地详情
     *
     * @param request
     * @param houseId
     * @return
     */
    public ServerResponse querySupervisorHostDetailList(HttpServletRequest request, String houseId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            WorkerSiteDetailsDTO workerSiteDetailsDTO = djMaintenanceRecordMapper.querySupervisorHostDetailList(houseId);
            if (workerSiteDetailsDTO != null) {
                String detailhouseId = workerSiteDetailsDTO.getHouseId();
                //循环便利工种
                List<CraftsManDTO> craftsManDTOList = new ArrayList<CraftsManDTO>();
                List<HouseFlow> list = houseFlowMapper.getAllFlowByHouseId(detailhouseId);
                HouseKeeperDTO houseKeeperDTO = new HouseKeeperDTO();
                list.forEach(houseFlow -> {
                    //3是大管家
                    String workerTypeId = houseFlow.getWorkerTypeId();
                    Integer workState = houseFlow.getWorkSteta();
                    if (workerTypeId.equals("3")) {
                        houseKeeperDTO.setWorkerTypeName("大管家");//工种名称
                        houseKeeperDTO.setName("刘晓庆");//姓名
                        houseKeeperDTO.setProjectTime("5/12");//工期
                        houseKeeperDTO.setPatrol("3/12");//巡查
                        houseKeeperDTO.setCheckTimes("7/12");//验收
                        houseKeeperDTO.setIamge(address + "iconWork/icon_dgj.png");
                    } else {
                        //其它类型是工匠
                        CraftsManDTO craftsManDTO = new CraftsManDTO();
                        craftsManDTO.setWorkerTypeName("水电");//工种名称
                        craftsManDTO.setName("李哈哈");//姓名
                        //施工状态，0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中，5收尾施工
                        if (workState == 0) {
                            craftsManDTO.setWorkSteta("未开始");
                        }
                        if (workState == 1) {
                            craftsManDTO.setWorkSteta("阶段完工通过");
                        }
                        if (workState == 2) {
                            craftsManDTO.setWorkSteta("整体完工通过");
                        }
                        if (workState == 3) {
                            craftsManDTO.setWorkSteta("待交底");
                        }
                        if (workState == 4) {
                            craftsManDTO.setWorkSteta("施工中");
                        }
                        if (workState == 5) {
                            craftsManDTO.setWorkSteta("收尾施工");
                        }

                        craftsManDTO.setProjectTime("5/25");//工期
                        craftsManDTO.setNode("16/42");//节点
                        craftsManDTO.setIamge(address + "iconWork/icon_ptgj.png");
                        craftsManDTOList.add(craftsManDTO);
                    }
                });
                workerSiteDetailsDTO.setHouseKeeperDTO(houseKeeperDTO);
                workerSiteDetailsDTO.setCraftsManDTOList(craftsManDTOList);
            }
            return ServerResponse.createBySuccess("查询成功", workerSiteDetailsDTO);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("工地详情异常");
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
