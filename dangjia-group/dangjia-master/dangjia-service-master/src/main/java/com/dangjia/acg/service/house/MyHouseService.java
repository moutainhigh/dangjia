package com.dangjia.acg.service.house;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HouseResult;
import com.dangjia.acg.dto.core.NodeDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.menu.IMenuConfigurationMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.group.GroupUserConfig;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.menu.MenuConfiguration;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.HouseFlowService;
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
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private HouseFlowService houseFlowService;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IMenuConfigurationMapper iMenuConfigurationMapper;

    @Autowired
    private UserMapper userMapper;
    protected static final Logger LOG = LoggerFactory.getLogger(MyHouseService.class);





    /**
     * APP我的房产
     */
    public ServerResponse getMyHouse(String userToken, String cityId,String isNew) {
        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        if(!CommonUtil.isEmpty(isNew)){
            return ServerResponse.createByErrorCodeResultObj(ServerCode.NO_DATA.getCode(), HouseUtil.getWorkerDatas(null,address));
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;

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
            return ServerResponse.createByErrorCodeResultObj(ServerCode.NO_DATA.getCode(), HouseUtil.getWorkerDatas(null,address));
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
            Map progress=nodeDTO.getProgress();
            Example example1 = new Example(HouseFlowApply.class);
            example1.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, house.getId()).andEqualTo(HouseFlowApply.MEMBER_CHECK, 1).andEqualTo(HouseFlowApply.APPLY_TYPE, 3);
            List<HouseFlowApply> houseFlowss = houseFlowApplyMapper.selectByExample(example1);
            int suspendDay = 0;//停工天数
            for (HouseFlowApply flowss : houseFlowss) {
                suspendDay += flowss.getSuspendDay();
            }
            int num = 1 + DateUtil.daysofTwo(houseFlow.getStartDate(), houseFlow.getEndDate());//工期天数
            progress.put("suspendDay",suspendDay);//停工天数
            progress.put("num",num);//计划施工天数
            nodeDTO.setProgress(progress);
            //工人信息
            if(!CommonUtil.isEmpty(houseFlow.getWorkerId())) {
                Member member1 = memberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
                member1.setPassword(null);
                member1.initPath(address);
                Map<String, Object> map = new HashMap<>();
                map.put("memberType", 1);
                map.put("id", member1.getId());
                map.put("targetId", member1.getId());
                map.put("targetAppKey", "49957e786a91f9c55b223d58");
                map.put("nickName", member1.getNickName());
                map.put("name", member1.getNickName());
                map.put("mobile", member1.getMobile());
                map.put("head", member1.getHead());
                map.put("workerTypeId", member1.getWorkerTypeId());
                map.put("workerName", workerType.getName());
                nodeDTO.setMember(map);
            }

            if(workerType.getType()==1){
                if( house.getDesignerOk()==0){
                   example = new Example(MainUser.class);
                   example.createCriteria().andEqualTo(MainUser.IS_RECEIVE,1);
                   example.orderBy(GroupUserConfig.CREATE_DATE).desc();
                   List<MainUser> list = userMapper.selectByExample(example);
                   if (list != null && list.size() > 0) {
                       MainUser user = list.get(0);
                       Map map = new HashMap();
                       map.put("id", user.getId());
                       map.put("targetId", user.getId());
                       map.put("targetAppKey", "49957e786a91f9c55b223d58");
                       map.put("nickName", "装修顾问 "+user.getUsername());
                       map.put("name", user.getUsername());
                       map.put("mobile", user.getMobile());
                       map.put("head", address+"qrcode/logo.png");
                       nodeDTO.setMember(map);
                   }
                }
                houseResult.setDesignList(nodeDTO);
            }else if(workerType.getType()==2){
                houseResult.setActuaryList(nodeDTO);
            }else{
                courseList.add(nodeDTO);
            }
            if(houseFlow.getWorkerType()<=3){
                setMenus(houseResult,house,houseFlow);
            }
        }
        houseResult.setProgress(HouseUtil.getWorkerDatas(house,address));
        houseResult.setCourseList(courseList);
        return ServerResponse.createBySuccess("查询成功",houseResult);
    }

    /**
     * 设置菜单
     */
    private void setMenus(HouseResult bean, House house, HouseFlow hf) {
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
        List<HouseResult.ListMapBean> bigList = new ArrayList<>();
        Example example = new Example(MenuConfiguration.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(MenuConfiguration.DATA_STATUS, 0);
        if(hf.getWorkerType()==1){
            criteria.andEqualTo(MenuConfiguration.MENU_TYPE, 2);
        }else if(hf.getWorkerType()==2){
            criteria.andEqualTo(MenuConfiguration.MENU_TYPE, 3);
        }else{
            criteria.andEqualTo(MenuConfiguration.MENU_TYPE, 4);
        }

        example.orderBy(MenuConfiguration.SORT).asc();
        List<MenuConfiguration> menuConfigurations2 = iMenuConfigurationMapper.selectByExample(example);
        for (MenuConfiguration configuration : menuConfigurations2) {
            configuration.initPath(imageAddress, webAddress, house.getId(), hf.getId(), null);
            HouseResult.ListMapBean mapBean = new HouseResult.ListMapBean();
            mapBean.setName(configuration.getName());
            mapBean.setUrl(configuration.getUrl());
            mapBean.setApiUrl(configuration.getApiUrl());
            mapBean.setImage(configuration.getImage());
            mapBean.setType(configuration.getType());
            bigList.add(mapBean);
        }
        bean.setBigList(bigList);//添加菜单到返回体中

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

