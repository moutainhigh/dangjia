package com.dangjia.acg.service.house;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HouseResult;
import com.dangjia.acg.dto.core.NodeDTO;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.*;
import com.dangjia.acg.mapper.matter.IRenovationManualMapper;
import com.dangjia.acg.mapper.matter.IRenovationManualMemberMapper;
import com.dangjia.acg.mapper.matter.IRenovationStageMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.HouseFlowService;
import com.dangjia.acg.service.design.DesignDataService;
import com.dangjia.acg.service.member.GroupInfoService;
import com.dangjia.acg.util.HouseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/1 0001
 * Time: 17:56
 */
@Service
public class MyHouseService {
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private GroupInfoService groupInfoService;
    @Autowired
    private ICityMapper iCityMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IModelingLayoutMapper modelingLayoutMapper;
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IRenovationManualMapper renovationManualMapper;
    @Autowired
    private IRenovationStageMapper renovationStageMapper;
    @Autowired
    private IRenovationManualMemberMapper renovationManualMemberMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IHouseExpendMapper houseExpendMapper;
    @Autowired
    private ITechnologyRecordMapper technologyRecordMapper;
    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private HouseChoiceCaseService houseChoiceCaseService;
    @Autowired
    private HouseFlowService houseFlowService;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private DesignDataService designDataService;
    @Autowired
    private HouseConstructionRecordMapper houseConstructionRecordMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseChoiceCaseMapper iHouseChoiceCaseMapper;

    @Autowired
    private IWorkDepositMapper workDepositMapper;
    protected static final Logger LOG = LoggerFactory.getLogger(MyHouseService.class);





    /**
     * APP我的房产
     */
    public ServerResponse getMyHouse(String userToken, String cityId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        //该城市该用户所有开工房产
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, member.getId())
                .andNotEqualTo(House.VISIT_STATE, 0)
                .andNotEqualTo(House.VISIT_STATE, 2)
                .andEqualTo(House.DATA_STATUS, 0);
        List<House> houseList = iHouseMapper.selectByExample(example);
        String houseId = getCurrentHouse(houseList);
        if(CommonUtil.isEmpty(houseId)){
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "暂无房产");
        }

        House house = iHouseMapper.selectByPrimaryKey(houseId);
        //统计几套房
        int again = houseList.size();
        HouseResult houseResult = new HouseResult();
        houseResult.setHouseName(house.getHouseName());
        houseResult.setAgain(again);
        houseResult.setHouseId(houseId);
        /*其它房产待处理任务列表状态*/
        int task = 0;
        for (House elseHouse : houseList) {
            if (!elseHouse.getId().equals(houseId)) {
                task += this.getTask(elseHouse.getId());
            }
        }
        houseResult.setTask(task);
        Map<Integer, String> applyTypeMap =DjConstants.VisitState.getVisitStateMap();
        houseResult.setBuildStage(applyTypeMap.get(house.getVisitState()));
        /*展示各种进度*/
        List<HouseFlow> houseFlowList = houseFlowMapper.getAllFlowByHouseId(houseId);
        List<NodeDTO> courseList = new ArrayList<>();
        for (HouseFlow houseFlow : houseFlowList) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            NodeDTO nodeDTO = HouseUtil.getWorkerDatas( house,  houseFlow,  workerType, address );

            //工人信息
            Member member1=memberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
            member1.setPassword(null);
            member1.initPath(address);
            Map<String, Object> map = new HashMap<>();
            map.put("memberType", 1);
            map.put("id", member.getId());
            map.put("nickName", member.getNickName());
            map.put("name", member.getNickName());
            map.put("mobile", member.getMobile());
            map.put("head", member.getHead());
            map.put("workerTypeId", member.getWorkerTypeId());
            map.put("workerName", workerType.getName());
            nodeDTO.setMember(map);

            if(workerType.getType()==1){
                houseResult.setDesignList(nodeDTO);
            }else if(workerType.getType()==2){
                houseResult.setActuaryList(nodeDTO);
            }else{
                courseList.add(nodeDTO);
            }
        }
        houseResult.setCourseList(courseList);
        return ServerResponse.createBySuccess("查询成功");
    }


    /**
     * 待处理任务
     */
    private int getTask(String houseId) {
        int task;
        //查询待支付工序
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("workType", 3).andEqualTo("houseId", houseId);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        task = houseFlowList.size();

        House house = iHouseMapper.selectByPrimaryKey(houseId);

        example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)
                .andEqualTo(MendOrder.STATE, 3);//审核状态
        List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
        task += mendOrderList.size();

        example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                .andEqualTo(MendOrder.STATE, 3);//补材料审核状态全通过
        mendOrderList = mendOrderMapper.selectByExample(example);
        task += mendOrderList.size();

        if (house.getDesignerOk() == 5 || house.getDesignerOk() == 2) {
            task++;
        }
        if (house.getBudgetOk() == 2) {
            task++;
        }
        //验收任务
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getMemberCheckList(houseId);
        task += houseFlowApplyList.size();
        return task;
    }
    private String getCurrentHouse(List<House> houseList){
        String houseId = null;
        if (houseList.size() > 1) {
            for (House house : houseList) {
                if (house.getIsSelect() == 1) {//当前选中
                    houseId = house.getId();
                    break;
                }
            }
            if (houseId == null) {//有很多房子但是没有isSelect为1的
                houseId = houseList.get(0).getId();
            }
        } else if (houseList.size() == 1) {
            houseId = houseList.get(0).getId();
        }
        return houseId;
    }

}

