package com.dangjia.acg.service.supervisor;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.supervisor.AuthorityDTO;
import com.dangjia.acg.dto.supervisor.PatrolRecordIndexDTO;
import com.dangjia.acg.dto.supervisor.SupHouseDetailsDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.supervisor.ISupervisorAuthorityMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.supervisor.DjBasicsSupervisorAuthority;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 督导APP端
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2020/1/6 8:20 PM
 */
@Service
public class SupervisorAppService {
    @Autowired
    private ISupervisorAuthorityMapper iSupervisorAuthorityMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;
    @Autowired
    private IMemberMapper memberMapper;

    /**
     * 督导首页数据获取
     *
     * @param userToken userToken
     * @param cityId    城市ID
     * @return ServerResponse
     */
    public ServerResponse getSupHomePage(String userToken, String cityId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<PatrolRecordIndexDTO> recordIndexDTOS = iSupervisorAuthorityMapper.getSupHomePage(worker.getId(), cityId);
        for (PatrolRecordIndexDTO recordIndexDTO : recordIndexDTOS) {
            recordIndexDTO.setImage(imageAddress + recordIndexDTO.getImage());
        }
        return ServerResponse.createBySuccess("查询成功", recordIndexDTOS);
    }

    /**
     * 督导获取工地列表
     *
     * @param pageDTO   分页
     * @param userToken userToken
     * @param cityId    城市ID
     * @param sortNum   1：全部，2：在施工地，3：本周新开，4：本月竣工，5：今日开工，6：连续3天未开工，7：超期施工，8：维保工地
     * @param type      0：默认，1：附近，2：价格降序（维保才有），3：价格升序（维保才有）
     * @param latitude  用户纬度
     * @param longitude 用户经度
     * @param searchKey 搜索关键字
     * @return ServerResponse
     */
    public ServerResponse getSupHouseList(PageDTO pageDTO, String userToken, String cityId, Integer sortNum,
                                          Integer type, String latitude, String longitude, String searchKey) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<AuthorityDTO> authorityDTOS = iSupervisorAuthorityMapper.getSupHouseList(worker.getId(), cityId, sortNum, type,
                latitude, longitude, searchKey);
        if (authorityDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(authorityDTOS);
        for (AuthorityDTO authorityDTO : authorityDTOS) {
            if (sortNum == 8) {//维保
                //TODO  查询维保的人员 金额
                authorityDTO.setType(1);
            } else {
                //工期
                int plan = 0;
                if (authorityDTO.getStartDate() != null && authorityDTO.getEndDate() != null) {
                    plan = 1 + DateUtil.daysofTwo(authorityDTO.getStartDate(), authorityDTO.getEndDate());//计划工期天数
                }
                int fatalism = iSupervisorAuthorityMapper.getFatalism(authorityDTO.getHouseId(), null);
                authorityDTO.setConstructionPeriod(plan + "/" + fatalism);
                //查询今日开工记录
                List<HouseFlowApply> todayStartList = houseFlowApplyMapper.getTodayStartByHouseId(authorityDTO.getHouseId(), new Date());
                if (todayStartList == null || todayStartList.size() == 0) {//没有今日开工记录
                    authorityDTO.setPersonnel("无");
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (HouseFlowApply houseFlowApply : todayStartList) {
                        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(workerType.getName());
                    }
                    authorityDTO.setPersonnel(stringBuilder.toString());
                }
                authorityDTO.setType(0);
            }
        }
        pageResult.setList(authorityDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 督导获取工地详情
     *
     * @param userToken userToken
     * @param houseId   房子ID
     * @return ServerResponse
     */
    public ServerResponse getSupHouseDetails(String userToken, String houseId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        House house = iHouseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "没有查询到相关房子");
        }
        Example example = new Example(DjBasicsSupervisorAuthority.class);
        example.createCriteria()
                .andEqualTo(DjBasicsSupervisorAuthority.MEMBER_ID, member.getId())
                .andEqualTo(DjBasicsSupervisorAuthority.HOUSE_ID, houseId);
        if (iSupervisorAuthorityMapper.selectCountByExample(example) > 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        SupHouseDetailsDTO detailsDTO = new SupHouseDetailsDTO();
        example = new Example(MemberAddress.class);
        example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID, houseId);
        MemberAddress memberAddress = iMasterMemberAddressMapper.selectOneByExample(example);
        detailsDTO.setHouseId(houseId);
        if (memberAddress == null) {
            detailsDTO.setHouseName(house.getHouseName());
        } else {
            detailsDTO.setHouseName(memberAddress.getAddress());
        }
        ModelingVillage modelingVillage = modelingVillageMapper.selectByPrimaryKey(house.getVillageId());
        if (memberAddress != null) {
            detailsDTO.setAddress(modelingVillage.getAddress());
            detailsDTO.setLongitude(modelingVillage.getLocationx());
            detailsDTO.setLatitude(modelingVillage.getLocationy());
        }

        //TODO  查询维保

        if (house.getVisitState() == 3) {
            detailsDTO.setButtonType(1);
        } else {
            detailsDTO.setButtonType(0);
        }
        example = new Example(HouseFlow.class);
        example.createCriteria()
                .andEqualTo(HouseFlow.WORK_TYPE, 4)
                .andGreaterThan(HouseFlow.WORKER_TYPE, 2)
                .andEqualTo(HouseFlow.HOUSE_ID, houseId);
        example.orderBy(HouseFlow.SORT).desc();
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(example);
        if (houseFlows.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        List<SupHouseDetailsDTO.SupHouseFlowDTO> flowDTOS = new ArrayList<>();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (HouseFlow houseFlow : houseFlows) {
            SupHouseDetailsDTO.SupHouseFlowDTO flowDTO = new SupHouseDetailsDTO.SupHouseFlowDTO();
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            flowDTO.setWorkerType(houseFlow.getWorkerType());
            flowDTO.setWorkerTypeId(houseFlow.getWorkerTypeId());
            if (workerType != null) {
                flowDTO.setWorkerTypeName(workerType.getName());
                flowDTO.setImage(imageAddress + workerType.getImage());
            }
            Member worker = memberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
            if (worker != null) {
                flowDTO.setMemberId(worker.getId());
                flowDTO.setMemberName(worker.getName());
            }
            List<SupHouseDetailsDTO.WorkerMapDTO> workerMapDTOS = new ArrayList<>();
            if (houseFlow.getWorkerType() == 3) {
                int plan = 0;
                if (house.getStartDate() != null && house.getEndDate() != null) {
                    plan = 1 + DateUtil.daysofTwo(house.getStartDate(), house.getEndDate());//计划工期天数
                }
                int fatalism = iSupervisorAuthorityMapper.getFatalism(houseId, null);
                SupHouseDetailsDTO.WorkerMapDTO workerMapDTO = new SupHouseDetailsDTO.WorkerMapDTO();
                workerMapDTO.setKeyName("工期");
                workerMapDTO.setValueName(plan + "/" + fatalism);
                workerMapDTOS.add(workerMapDTO);
                Long allPatrol = houseFlowApplyMapper.countPatrol(house.getId(), null);
                allPatrol = allPatrol == null ? 0 : allPatrol;
                int all = 0;
                int time = 0;//累计管家总阶段验收和完工验收次数
                for (HouseFlow flow : houseFlows) {
                    if (flow.getWorkerType() != 3 && flow.getPatrol() != null) {
                        all = all + flow.getPatrol();
                        if (flow.getWorkerType() == 4) {
                            time++;
                        } else {
                            time += 2;
                        }
                    }
                }
                workerMapDTO = new SupHouseDetailsDTO.WorkerMapDTO();
                workerMapDTO.setKeyName("巡查");
                workerMapDTO.setValueName(allPatrol + "/" + all);
                workerMapDTOS.add(workerMapDTO);
                Integer check = iSupervisorAuthorityMapper.getAcceptanceCheck(houseId);
                check = check == null ? 0 : check;
                workerMapDTO = new SupHouseDetailsDTO.WorkerMapDTO();
                workerMapDTO.setKeyName("验收");
                workerMapDTO.setValueName(check + "/" + time);
                workerMapDTOS.add(workerMapDTO);
            } else {
                if (houseFlow.getWorkSteta() == 1) {
                    flowDTO.setCompletion("阶段完工");
                } else if (houseFlow.getWorkSteta() == 2) {
                    flowDTO.setCompletion("整体完工");
                }
                int plan = 0;
                if (houseFlow.getStartDate() != null && houseFlow.getEndDate() != null) {
                    plan = 1 + DateUtil.daysofTwo(houseFlow.getStartDate(), houseFlow.getEndDate());//计划工期天数
                }
                int fatalism = iSupervisorAuthorityMapper.getFatalism(houseId, houseFlow.getWorkerType());
                SupHouseDetailsDTO.WorkerMapDTO workerMapDTO = new SupHouseDetailsDTO.WorkerMapDTO();
                workerMapDTO.setKeyName("工期");
                workerMapDTO.setValueName(plan + "/" + fatalism);
                workerMapDTOS.add(workerMapDTO);
                //节点
                int allNumber = iSupervisorAuthorityMapper.getAllNumber(houseId, houseFlow.getWorkerTypeId());
                int completeNumber = iSupervisorAuthorityMapper.getCompleteNumber(houseId, houseFlow.getWorkerTypeId());
                workerMapDTO = new SupHouseDetailsDTO.WorkerMapDTO();
                workerMapDTO.setKeyName("节点");
                workerMapDTO.setValueName(completeNumber + "/" + allNumber);
                workerMapDTOS.add(workerMapDTO);
            }
            flowDTO.setMapList(workerMapDTOS);
        }
        detailsDTO.setFlowDatas(flowDTOS);
        return ServerResponse.createBySuccess("查询成功", detailsDTO);
    }
}
