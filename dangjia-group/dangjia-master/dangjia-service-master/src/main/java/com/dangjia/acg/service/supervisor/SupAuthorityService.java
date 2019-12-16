package com.dangjia.acg.service.supervisor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.supervisor.*;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.supervisor.DjBasicsSupervisorAuthorityMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.supervisor.DjBasicsSupervisorAuthority;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SupAuthorityService {
    private static Logger logger = LoggerFactory.getLogger(SupAuthorityService.class);
    @Autowired
    private DjBasicsSupervisorAuthorityMapper djBasicsSupervisorAuthorityMapper ;
    @Autowired
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper ;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private WorkerTypeAPI workerTypeAPI;
    /**
     * 删除已选
     * @param request
     * @param id
     * @return
     */
    public ServerResponse delAuthority(HttpServletRequest request, String id) {
        try {
            int i = djBasicsSupervisorAuthorityMapper.deleteByPrimaryKey(id);
            if (i <= 0)
                return ServerResponse.createByErrorMessage("删除失败");
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            logger.error("新建巡检异常", e);
            return ServerResponse.createByErrorMessage("新建巡检异常");
        }
    }

    /**
     * 搜索已选
     * @param request
     * @param visitState
     * @param keyWord
     * @return
     */
    public ServerResponse searchAuthority(HttpServletRequest request, String visitState, String keyWord, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjBasicsSupervisorAuthority> list=djBasicsSupervisorAuthorityMapper.searchAuthority(visitState,keyWord);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("新建巡检异常", e);
            return ServerResponse.createByErrorMessage("新建巡检异常");
        }
    }


    /**
     * 增加已选
     * @param request
     * @param          djBasicsSupervisorAuthority
     * @return
     */
    public ServerResponse addAuthority(HttpServletRequest request, DjBasicsSupervisorAuthority djBasicsSupervisorAuthority ) {
        try {
           int i= djBasicsSupervisorAuthorityMapper.insert(djBasicsSupervisorAuthority);
            if (i <= 0)
                return ServerResponse.createByErrorMessage("增加失败");
            return ServerResponse.createBySuccessMessage("增加成功");
        } catch (Exception e) {
            logger.error("增加已选异常", e);
            return ServerResponse.createByErrorMessage("增加已选异常");
        }
    }

    public ServerResponse addAllAuthority(HttpServletRequest request, String strAuthority,String operateId ) {
        try {
            JSONArray arr = JSONArray.parseArray(strAuthority);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String houseId = obj.getString("houseId");
                String memberId = obj.getString("memberId");
                DjBasicsSupervisorAuthority djBasicsSupervisorAuthority = new DjBasicsSupervisorAuthority();
                djBasicsSupervisorAuthority.setMemberId(memberId);
                djBasicsSupervisorAuthority.setHouseId(houseId);
                djBasicsSupervisorAuthority.setOperateId(operateId);
                djBasicsSupervisorAuthorityMapper.insert(djBasicsSupervisorAuthority);
            }
            return ServerResponse.createBySuccessMessage("增加成功");
        } catch (Exception e) {
            logger.error("增加已选异常", e);
            return ServerResponse.createByErrorMessage("增加已选异常");
        }
    }

    /**
     * 查看申请信息
     *
     * @param request
     * @return
     */
    public ServerResponse queryApplicationInfo(HttpServletRequest request,String houseId,PageDTO pageDTO) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<MaintenanceRecordDTO> list= djMaintenanceRecordMapper.queryApplicationInfo(houseId);
            list.forEach(maintenanceRecordDTO->{
                maintenanceRecordDTO.setOwnerImage(StringTool.getImage(maintenanceRecordDTO.getOwnerImage(),address));
                maintenanceRecordDTO.setOwnerImageDetail(StringTool.getImage(maintenanceRecordDTO.getOwnerImage(),address).split(","));
            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("增加已选异常", e);
            return ServerResponse.createByErrorMessage("增加已选异常");
        }
    }

    /**
     * 查看责任划分
     *
     * @param request
     * @return
     */
    public ServerResponse queryDvResponsibility(HttpServletRequest request,String houseId,PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjResponsiblePartyDTO> list=djMaintenanceRecordMapper.queryDvResponsibility(houseId);
            list.forEach(djResponsiblePartyDTO->{
                String responsiblePartyType=djResponsiblePartyDTO.getResponsiblePartyType();
               String responsiblePartyId= djResponsiblePartyDTO.getResponsiblePartyId();
               //维保责任方类型 1:店铺  3：工匠
                if (responsiblePartyType.equals("1"))
                {
                    List<StoreMaintenanceDTO> listStoreMaintenance=djMaintenanceRecordMapper.queryStoreMaintenance(responsiblePartyType,responsiblePartyId);
                    djResponsiblePartyDTO.setListStoreMaintenance(listStoreMaintenance);
                }
                if(responsiblePartyType.equals("3"))
                {
                    List<MemberMaintenanceDTO> listMemberMaintenance=djMaintenanceRecordMapper.queryMemberMaintenance(responsiblePartyType,responsiblePartyId);
                    djResponsiblePartyDTO.setListMemberMaintenance(listMemberMaintenance);
                }
            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("增加已选异常", e);
            return ServerResponse.createByErrorMessage("增加已选异常");
        }
    }

    /**
     * 工地列表
     * @param request
     * @param sortNum
     * @return
     */
    public ServerResponse querySupervisorHostList(HttpServletRequest request, String sortNum,PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<SupSitelistDTO> list= djMaintenanceRecordMapper.querySupervisorHostList();
            list.forEach(supSitelistDTO->{
                String houseId=supSitelistDTO.getHouseId();
            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("工地列表异常", e);
            return ServerResponse.createByErrorMessage("工地列表异常");
        }
    }

    /**
     * 工地详情
     * @param request
     * @param houseId
     * @return
     */
    public ServerResponse querySupervisorHostDetailList(HttpServletRequest request, String houseId) {
        try {
            WorkerSiteDetailsDTO workerSiteDetailsDTO = djMaintenanceRecordMapper.querySupervisorHostDetailList(houseId);
            return ServerResponse.createBySuccess("查询成功", workerSiteDetailsDTO);
        } catch (Exception e) {
            logger.error("工地详情异常", e);
            return ServerResponse.createByErrorMessage("工地详情异常");
        }
    }
    /**
     * 验收动态
     *
     * @param request
     * @return
     */
    public ServerResponse queryAcceptanceTrend(HttpServletRequest request,String houseId,PageDTO pageDTO) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<AcceptanceTrendDTO> list= djMaintenanceRecordMapper.queryAcceptanceTrend(houseId);
            list.forEach(acceptanceTrendDTO->{
                String id= acceptanceTrendDTO.getId();
                List<AcceptanceTrendDetailDTO> listDetail= djMaintenanceRecordMapper.queryAcceptanceTrendDetail(id);
                acceptanceTrendDTO.setListDetail(listDetail);
                listDetail.forEach(acceptanceTrendDetailDTO->{
                    String workerTypeId=acceptanceTrendDetailDTO.getWorkerType();
                    String workerTypeName=null;
                    ServerResponse response = workerTypeAPI.getWorkerType(workerTypeId);
                    if (response.isSuccess()) {
                        workerTypeName = (((JSONObject) response.getResultObj()).getString(WorkerType.NAME));
                    }
                    acceptanceTrendDetailDTO.setWorkerTypeName(workerTypeName);
                    acceptanceTrendDetailDTO.setStewardImage(StringTool.getImage(acceptanceTrendDetailDTO.getStewardRemark(),address));
                    acceptanceTrendDetailDTO.setStewardImageDetail(StringTool.getImage(acceptanceTrendDetailDTO.getStewardRemark(),address).split(","));
                });

            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("验收动态异常", e);
            return ServerResponse.createByErrorMessage("验收动态异常");
        }
    }

    /**
     *（维修)工地列表
     * @param request
     * @param houseId
     * @return
     */
    public ServerResponse queryMaintenanceHostList(HttpServletRequest request, String houseId) {
        try {


            return null;
        } catch (Exception e) {
            logger.error("增加已选异常", e);
            return ServerResponse.createByErrorMessage("增加已选异常");
        }
    }

    /**
     *（维保）工地详情
     * @param request
     * @param houseId
     * @return
     */
    public ServerResponse queryMtHostListDetail(HttpServletRequest request, String houseId) {
        try {
            return null;
        } catch (Exception e) {
            logger.error("（维保）工地详情异常", e);
            return ServerResponse.createByErrorMessage("（维保）工地详情异常");
        }
    }
}
