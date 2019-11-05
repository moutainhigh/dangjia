package com.dangjia.acg.service.delivery;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.UserAPI;
import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.UserInfoResultDTO;
import com.dangjia.acg.dto.core.HouseResult;
import com.dangjia.acg.dto.delivery.*;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.order.IBillHouseMapper;
import com.dangjia.acg.mapper.order.IBillQuantityRoomImagesMapper;
import com.dangjia.acg.mapper.order.IBillQuantityRoomMapper;
import com.dangjia.acg.mapper.refund.IBillMendOrderMapper;
import com.dangjia.acg.mapper.sale.IBillDjAlreadyRobSingleMapper;
import com.dangjia.acg.mapper.sale.IBillMemberMapper;
import com.dangjia.acg.mapper.sale.IBillUserMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.group.GroupUserConfig;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.menu.MenuConfiguration;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.sale.royalty.DjAlreadyRobSingle;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.util.HouseUtil;
import com.dangjia.acg.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import cn.jiguang.common.utils.StringUtils;
import com.dangjia.acg.modle.delivery.DjDeliverOrder;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;


import java.util.List;

@Service
public class DjDeliverOrderService {
    @Autowired
    private IBillDjDeliverOrderMapper IBillDjDeliverOrderMapper;
    @Autowired
    private IBillDjAlreadyRobSingleMapper iBillDjAlreadyRobSingleMapper;
    @Autowired
    private IBillUserMapper iBillUserMapper;
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private IBillMemberMapper iBillMemberMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IBillHouseFlowMapper iBillHouseFlowMapper;
    @Autowired
    private IBillTechnologyRecordMapper iBillTechnologyRecordMapper;
    @Autowired
    private IBillDjDeliverOrderItemMapper iBillDjDeliverOrderItemMapper;
    @Autowired
    private IBillWorkerTypeMapper iBillWorkerTypeMapper;
    @Autowired
    private IBillHouseMapper iBillHouseMapper;
    @Autowired
    private MemberAPI memberAPI;
    @Autowired
    private IBillQuantityRoomMapper iBillQuantityRoomMapper;
    @Autowired
    private IBillQuantityRoomImagesMapper iBillQuantityRoomImagesMapper;

    private static Logger logger = LoggerFactory.getLogger(DjDeliverOrderService.class);
    @Autowired
    private IBillMendOrderMapper iBillMendOrderMapper;
    @Autowired
    private IBillHouseFlowApplyMapper iBillHouseFlowApplyMapper;
    @Autowired
    private IBillMenuConfigurationMapper iBillMenuConfigurationMapper;


    public Object getHouse(String memberId, HouseResult houseResult) {
        //该城市该用户所有开工房产
        List<House> houseList = iBillHouseMapper.selectByExample(getHouseExample(memberId));
        String houseId = getCurrentHouse(houseList);
        if (CommonUtil.isEmpty(houseId)) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        House house = iBillHouseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        if (houseResult != null) {
            //统计几套房
            int again = houseList.size();
            houseResult.setAgain(again);
            /*其它房产待处理任务列表状态*/
            int task = 0;
            for (House elseHouse : houseList) {
                if (!elseHouse.getId().equals(houseId)) {
                    task += this.getTask(elseHouse.getId());
                }
            }
            houseResult.setTask(task);
        }
        return house;
    }


    /**
     * 待处理任务
     */
    private int getTask(String houseId) {
        int task;
        //查询待支付工序
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("workType", 3).andEqualTo("houseId", houseId);
        List<HouseFlow> houseFlowList = iBillHouseFlowMapper.selectByExample(example);
        task = houseFlowList.size();
        House house = iBillHouseMapper.selectByPrimaryKey(houseId);
        example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)
                .andEqualTo(MendOrder.STATE, 3);//审核状态
        List<MendOrder> mendOrderList = iBillMendOrderMapper.selectByExample(example);
        task += mendOrderList.size();
        example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                .andEqualTo(MendOrder.STATE, 3);//补材料审核状态全通过
        mendOrderList = iBillMendOrderMapper.selectByExample(example);
        task += mendOrderList.size();
        if (house.getDesignerState() == 5 || house.getDesignerState() == 2) {
            task++;
        }
        if (house.getBudgetState() == 2) {
            task++;
        }
        //验收任务
        List<HouseFlowApply> houseFlowApplyList = iBillHouseFlowApplyMapper.getMemberCheckList(houseId);
        task += houseFlowApplyList.size();
        return task;
    }

    private String getCurrentHouse(List<House> houseList) {
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

    /**
     * 获取我的房产的查询条件
     *
     * @param memberId 用户ID
     * @return 条件实体
     */
    public Example getHouseExample(String memberId) {
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, memberId)
                .andNotEqualTo(House.VISIT_STATE, 2);
        return example;
    }

    /**
     * 查询我要装修首页
     * @param houseId
     * @return
     */
    public ServerResponse queryOrderNumber(String userToken,String houseId){
        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);

//        Object object = memberAPI.getMember(userToken);
//        if (object instanceof ServerResponse) {
//            return (ServerResponse) object;
//        }
//        JSONObject job = (JSONObject)object;
//        Member member = job.toJavaObject(Member.class);
//        HouseResult houseResult = new HouseResult();
//        object = getHouse(member.getId(), houseResult);
//        if (object instanceof ServerResponse) {
//            return ServerResponse.createByErrorCodeResultObj(ServerCode.NO_DATA.getCode(), HouseUtil.getWorkerDatas(null, address));
//        }

        //订单状态 1待付款，2已付款，3待收货
        WorkInFoDTO workInFoDTO = new WorkInFoDTO();
        Example example = new Example(Order.class);
        //待付款
        example.createCriteria().andEqualTo(Order.HOUSE_ID,houseId)
                .andEqualTo(Order.DATA_STATUS, 0)
                .andEqualTo(Order.ORDER_STATUS,1);
        Map<String,Object> map = new HashMap<>();
        map.put("one", IBillDjDeliverOrderMapper.selectCountByExample(example));

        //已付款
        example = new Example(Order.class);
        example.createCriteria().andEqualTo(Order.HOUSE_ID,houseId)
                .andEqualTo(Order.DATA_STATUS, 0)
                .andEqualTo(Order.ORDER_STATUS,2);
        map.put("two", IBillDjDeliverOrderMapper.selectCountByExample(example));

        //待收货
        example = new Example(Order.class);
        example.createCriteria().andEqualTo(Order.HOUSE_ID,houseId)
                .andEqualTo(Order.DATA_STATUS, 0)
                .andEqualTo(Order.ORDER_STATUS,3);
        map.put("three", IBillDjDeliverOrderMapper.selectCountByExample(example));
        map.put("four", 0);
        map.put("five", 0);
        workInFoDTO.setOrderMap(map);


        House house = iBillHouseMapper.selectByPrimaryKey(houseId);
        if(house != null) {
            String houseName = house.getResidential() + house.getBuilding() + "栋" +
                    house.getUnit() + "单元" + house.getNumber() + "号";
            workInFoDTO.setHouseName(houseName);
        }

        //1-销售阶段 2-房子 销售接单
        if(house != null && house.getVisitState() == 0){
            workInFoDTO.setHouseType(1);
        }else if(house != null && house.getIsRobStats() == 1){
            workInFoDTO.setHouseType(2);
        }else{
            workInFoDTO.setHouseType(3);
        }


        HouseFlowInfoDTO houseFlowInfoDTO = new HouseFlowInfoDTO();
        //查询今日播报信息
        List<HouseFlowDataDTO> sowingList = IBillDjDeliverOrderMapper.queryApplyDec();
        houseFlowInfoDTO.setNumber(2);
        houseFlowInfoDTO.setHouseFlowDataDTOS(sowingList);
        workInFoDTO.setHouseFlowInfoDTO(houseFlowInfoDTO);


        //查询当前房子状态
        List<WorkerTypeDTO> wtdList = IBillDjDeliverOrderMapper.queryType(houseId);
        if(!wtdList.isEmpty()){
            workInFoDTO.setType(wtdList.get(0).getType());
        }

        //查询房子工种类型
        example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID,houseId)
                .andEqualTo(HouseFlow.DATA_STATUS, 0);
        List<HouseFlow> houseFlows = iBillHouseFlowMapper.selectByExample(example);
        for (HouseFlow o : houseFlows) {
            if (o.getWorkerType() <= 3) {
                setMenus(workInFoDTO, house, o);
            }
        }

        //获取工序信息
        List<Object> workNodeListDTO = summationMethod(houseFlows,houseId);
        workInFoDTO.setWorkList(workNodeListDTO);

        //获取客服明细
        Example example1 = new Example(DjAlreadyRobSingle.class);
        example1.createCriteria()
                .andEqualTo(DjAlreadyRobSingle.HOUSE_ID, houseId)
                .andEqualTo(DjAlreadyRobSingle.DATA_STATUS, 0);
        List<DjAlreadyRobSingle> lists = iBillDjAlreadyRobSingleMapper.selectByExample(example1);
        if (!lists.isEmpty()) {
            String userid = lists.get(0).getUserId();
            example = new Example(MainUser.class);
            example.createCriteria().andEqualTo(MainUser.ID, userid);
            example.orderBy(MainUser.CREATE_DATE).desc();
            List<MainUser> list = iBillUserMapper.selectByExample(example);
            if (list != null && list.size() > 0) {
                MainUser user = list.get(0);
                map = new HashMap<>();
                map.put("id", user.getId());
                map.put("targetId", user.getId());
                UserInfoResultDTO userInfoResult = userAPI.getUserInfo(AppType.SALE.getDesc(), userid);
                if (userInfoResult != null && !CommonUtil.isEmpty(userInfoResult.getNickname())) {
                    map.put("nickName", "装修顾问 " + userInfoResult.getNickname());
                } else {
                    map.put("nickName", "装修顾问 小" + user.getUsername().substring(0, 1));
                }
                map.put("name", user.getUsername());
                map.put("mobile", user.getMobile());
                Member member1 = iBillMemberMapper.selectByPrimaryKey(user.getMemberId());
                if (member1 != null) {
                    member1.initPath(address);
                    map.put("head", member1.getHead());
                } else {
                    map.put("head", address + Utils.getHead());
                }
                workInFoDTO.setMap(map);
            }
        }


        return ServerResponse.createBySuccess("查询成功", workInFoDTO);
    }


    /**
     * 设置菜单
     */
    private  void setMenus(WorkInFoDTO workInFoDTO,House house, HouseFlow hf) {
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
        List<WorkInFoDTO.ListMapBean> bigList = new ArrayList<>();
        Example example = new Example(MenuConfiguration.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(MenuConfiguration.DATA_STATUS, 0);
        if (hf.getWorkerType() == 1) {
            criteria.andEqualTo(MenuConfiguration.MENU_TYPE, 2);
        } else if (hf.getWorkerType() == 2) {
            criteria.andEqualTo(MenuConfiguration.MENU_TYPE, 3);
        } else {
            criteria.andEqualTo(MenuConfiguration.MENU_TYPE, 4);
        }

        example.orderBy(MenuConfiguration.SORT).asc();
        List<MenuConfiguration> menuConfigurations2 = iBillMenuConfigurationMapper.selectByExample(example);
        for (MenuConfiguration configuration : menuConfigurations2) {
            configuration.initPath(imageAddress, webAddress, house.getId(), hf.getId(), null);
            WorkInFoDTO.ListMapBean mapBean = new WorkInFoDTO.ListMapBean();
            mapBean.setName(configuration.getName());
            mapBean.setUrl(configuration.getUrl());
            mapBean.setApiUrl(configuration.getApiUrl());
            mapBean.setImage(configuration.getImage());
            mapBean.setType(configuration.getType());
            bigList.add(mapBean);
        }
        workInFoDTO.setBigList(bigList);//添加菜单到返回体中

    }

    /**
     * 获取工序信息
     * @param houseId
     * @return
     */
    public List<Object> summationMethod(List<HouseFlow> houseFlows,String houseId){
        WorkNodeListDTO workNodeListDTO = new WorkNodeListDTO();
        //查询工序节点
        List<NodeNumberDTO> nodeNumberDTOS = iBillDjDeliverOrderItemMapper.queryNodeNumber(houseId);
        //查询材料数量
        List<MaterialNumberDTO> materialNumberDTOS = iBillDjDeliverOrderItemMapper.queryMaterialNumber(houseId);

        Example example = new Example(WorkerType.class);
        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<Object> workList = new ArrayList<>();
        List<Map<String,Object>> gList = new ArrayList<>();
        Map<String,Object> listMap = new HashMap<>();
        Map<String,Object> maps = new HashMap<>();
        //1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工
        for (HouseFlow houseFlow : houseFlows) {
            if(houseFlow.getWorkerType() == 1){
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE,houseFlow.getWorkerType())
                        .andEqualTo(WorkerType.DATA_STATUS, 0);
                List<WorkerType> workerType = iBillWorkerTypeMapper.selectByExample(example);
                gList = new ArrayList<>();
                listMap = new HashMap<>();
                listMap.put("name","预计工期");
                if(houseFlow.getStartDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                }
                gList.add(listMap);
                listMap = new HashMap<>();
                listMap.put("name","实际工期");
                if(houseFlow.getEndDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                }
                gList.add(listMap);

                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 1).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 1).count();
                listMap = new HashMap<>();
                if(iEnd == 0){
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",100 + "/" +100);//全部节点
                    workNodeListDTO.setHundred(100);
                }else{
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",iEnd + "/" +iStart);//全部节点
                    workNodeListDTO.setHundred((int)(iStart / iEnd * 100));
                }
                gList.add(listMap);

                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 1
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 1
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 1
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                listMap = new HashMap<>();
                listMap.put("name","材料使用");//全部节点
                listMap.put("value",aa + rr + "/" + ss);//全部节点
                gList.add(listMap);
                workNodeListDTO.setLists(gList);

                workNodeListDTO.setImage(address +workerType.get(0).getImage());
                workNodeListDTO.setWorkName("设计师");
                workList.add(workNodeListDTO);
            }else if(houseFlow.getWorkerType() == 2){
                workNodeListDTO = new WorkNodeListDTO();
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE,houseFlow.getWorkerType())
                        .andEqualTo(WorkerType.DATA_STATUS, 0);
                List<WorkerType> workerType = iBillWorkerTypeMapper.selectByExample(example);

                gList = new ArrayList<>();
                listMap = new HashMap<>();
                listMap.put("name","预计工期");
                if(houseFlow.getStartDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                }
                gList.add(listMap);

                listMap = new HashMap<>();
                listMap.put("name","实际工期");
                if(houseFlow.getEndDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                }
                gList.add(listMap);

                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 2).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 2).count();
                listMap = new HashMap<>();
                if(iEnd == 0){
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",100 + "/" +100);//全部节点
                    workNodeListDTO.setHundred(100);
                }else{
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",iEnd + "/" +iStart);//全部节点
                    workNodeListDTO.setHundred((int)(iStart / iEnd * 100));
                }
                gList.add(listMap);

                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 2
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 2
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 2
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                listMap = new HashMap<>();
                listMap.put("name","材料使用");//全部节点
                listMap.put("value",aa + rr + "/" + ss);//全部节点
                gList.add(listMap);

                workNodeListDTO.setLists(gList);
                workNodeListDTO.setImage(address +workerType.get(0).getImage());
                workNodeListDTO.setWorkName("精算师");
                workList.add(workNodeListDTO);
            }else if(houseFlow.getWorkerType() == 3){
                workNodeListDTO = new WorkNodeListDTO();
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE,houseFlow.getWorkerType())
                        .andEqualTo(WorkerType.DATA_STATUS, 0);
                List<WorkerType> workerType = iBillWorkerTypeMapper.selectByExample(example);

                gList = new ArrayList<>();
                listMap = new HashMap<>();
                listMap.put("name","预计工期");
                if(houseFlow.getStartDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                }
                gList.add(listMap);
                listMap = new HashMap<>();
                listMap.put("name","实际工期");
                if(houseFlow.getEndDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                }
                gList.add(listMap);

                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 3).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 3).count();
                listMap = new HashMap<>();
                if(iEnd == 0){
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",100 + "/" +100);//全部节点
                    workNodeListDTO.setHundred(100);
                }else{
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",iEnd + "/" +iStart);//全部节点
                    workNodeListDTO.setHundred((int)(iStart / iEnd * 100));
                }
                gList.add(listMap);

                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 3
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 3
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 3
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                listMap = new HashMap<>();
                listMap.put("name","材料使用");//全部节点
                listMap.put("value",aa + rr + "/" + ss);//全部节点
                gList.add(listMap);
                workNodeListDTO.setLists(gList);

                workNodeListDTO.setImage(address +workerType.get(0).getImage());
                workNodeListDTO.setWorkName("大管家");
                workList.add(workNodeListDTO);
            }else if(houseFlow.getWorkerType() == 4){
                workNodeListDTO = new WorkNodeListDTO();
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE,houseFlow.getWorkerType())
                        .andEqualTo(WorkerType.DATA_STATUS, 0);
                List<WorkerType> workerType = iBillWorkerTypeMapper.selectByExample(example);

                gList = new ArrayList<>();
                listMap = new HashMap<>();

                listMap.put("name","预计工期");
                if(houseFlow.getStartDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                }
                gList.add(listMap);
                listMap = new HashMap<>();
                listMap.put("name","实际工期");
                if(houseFlow.getEndDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                }
                gList.add(listMap);

                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 4).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 4).count();
                listMap = new HashMap<>();
                if(iEnd == 0){
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",100 + "/" +100);//全部节点
                    workNodeListDTO.setHundred(100);
                }else{
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",iEnd + "/" +iStart);//全部节点
                    workNodeListDTO.setHundred((int)(iStart / iEnd * 100));
                }
                gList.add(listMap);

                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 4
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 4
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 4
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                listMap = new HashMap<>();
                listMap.put("name","材料使用");//全部节点
                listMap.put("value",aa + rr + "/" + ss);//全部节点
                gList.add(listMap);
                workNodeListDTO.setLists(gList);

                workNodeListDTO.setImage(address +workerType.get(0).getImage());
                workNodeListDTO.setWorkName("拆除");
                workList.add(workNodeListDTO);
            }else if(houseFlow.getWorkerType() == 6){
                workNodeListDTO = new WorkNodeListDTO();
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE,houseFlow.getWorkerType())
                        .andEqualTo(WorkerType.DATA_STATUS, 0);
                List<WorkerType> workerType = iBillWorkerTypeMapper.selectByExample(example);
                gList = new ArrayList<>();
                listMap = new HashMap<>();

                listMap.put("name","预计工期");
                if(houseFlow.getStartDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                }
                gList.add(listMap);
                listMap = new HashMap<>();
                listMap.put("name","实际工期");
                if(houseFlow.getEndDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                }
                gList.add(listMap);

                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 6).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 6).count();
                listMap = new HashMap<>();
                if(iEnd == 0){
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",100 + "/" +100);//全部节点
                    workNodeListDTO.setHundred(100);
                }else{
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",iEnd + "/" +iStart);//全部节点
                    workNodeListDTO.setHundred((int)(iStart / iEnd * 100));
                }
                gList.add(listMap);

                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 6
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 6
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 6
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                listMap = new HashMap<>();
                listMap.put("name","材料使用");//全部节点
                listMap.put("value",aa + rr + "/" + ss);//全部节点
                gList.add(listMap);
                workNodeListDTO.setLists(gList);

                workNodeListDTO.setImage(address +workerType.get(0).getImage());
                workNodeListDTO.setWorkName("水电工");
                workList.add(workNodeListDTO);
            }else if(houseFlow.getWorkerType() == 7){
                workNodeListDTO = new WorkNodeListDTO();
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE,houseFlow.getWorkerType())
                        .andEqualTo(WorkerType.DATA_STATUS, 0);
                List<WorkerType> workerType = iBillWorkerTypeMapper.selectByExample(example);

                gList = new ArrayList<>();
                listMap = new HashMap<>();

                listMap.put("name","预计工期");
                if(houseFlow.getStartDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                }
                gList.add(listMap);
                listMap = new HashMap<>();
                listMap.put("name","实际工期");
                if(houseFlow.getEndDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                }
                gList.add(listMap);

                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 7).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 7).count();
                listMap = new HashMap<>();
                if(iEnd == 0){
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",100 + "/" +100);//全部节点
                    workNodeListDTO.setHundred(100);
                }else{
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",iEnd + "/" +iStart);//全部节点
                    workNodeListDTO.setHundred((int)(iStart / iEnd * 100));
                }
                gList.add(listMap);

                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 7
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 7
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 7
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                listMap = new HashMap<>();
                listMap.put("name","材料使用");//全部节点
                listMap.put("value",aa + rr + "/" + ss);//全部节点
                gList.add(listMap);
                workNodeListDTO.setLists(gList);

                workNodeListDTO.setImage(address +workerType.get(0).getImage());
                workNodeListDTO.setWorkName("防水");
                workList.add(workNodeListDTO);
            }else if(houseFlow.getWorkerType() == 8){
                workNodeListDTO = new WorkNodeListDTO();
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE,houseFlow.getWorkerType())
                        .andEqualTo(WorkerType.DATA_STATUS, 0);
                List<WorkerType> workerType = iBillWorkerTypeMapper.selectByExample(example);

                gList = new ArrayList<>();
                listMap = new HashMap<>();

                listMap.put("name","预计工期");
                if(houseFlow.getStartDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                }
                gList.add(listMap);
                listMap = new HashMap<>();
                listMap.put("name","实际工期");
                if(houseFlow.getEndDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                }
                gList.add(listMap);

                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 8).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 8).count();
                listMap = new HashMap<>();
                if(iEnd == 0){
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",100 + "/" +100);//全部节点
                    workNodeListDTO.setHundred(100);
                }else{
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",iEnd + "/" +iStart);//全部节点
                    workNodeListDTO.setHundred((int)(iStart / iEnd * 100));
                }
                gList.add(listMap);

                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 8
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 8
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 8
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                listMap = new HashMap<>();
                listMap.put("name","材料使用");//全部节点
                listMap.put("value",aa + rr + "/" + ss);//全部节点
                gList.add(listMap);
                workNodeListDTO.setLists(gList);

                workNodeListDTO.setImage(address +workerType.get(0).getImage());
                workNodeListDTO.setWorkName("泥工");
                workList.add(workNodeListDTO);
            }else if(houseFlow.getWorkerType() == 9){
                workNodeListDTO = new WorkNodeListDTO();
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE,houseFlow.getWorkerType())
                        .andEqualTo(WorkerType.DATA_STATUS, 0);
                List<WorkerType> workerType = iBillWorkerTypeMapper.selectByExample(example);

                gList = new ArrayList<>();
                listMap = new HashMap<>();
                listMap.put("name","预计工期");
                if(houseFlow.getStartDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                }
                gList.add(listMap);
                listMap = new HashMap<>();
                listMap.put("name","实际工期");
                if(houseFlow.getEndDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                }
                gList.add(listMap);

                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 9).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 9).count();
                listMap = new HashMap<>();
                if(iEnd == 0){
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",100 + "/" +100);//全部节点
                    workNodeListDTO.setHundred(100);
                }else{
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",iEnd + "/" +iStart);//全部节点
                    workNodeListDTO.setHundred((int)(iStart / iEnd * 100));
                }
                gList.add(listMap);

                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 9
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 9
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 9
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                listMap = new HashMap<>();
                listMap.put("name","材料使用");//全部节点
                listMap.put("value",aa + rr + "/" + ss);//全部节点
                gList.add(listMap);
                workNodeListDTO.setLists(gList);

                workNodeListDTO.setImage(address +workerType.get(0).getImage());
                workNodeListDTO.setWorkName("木工");
                workList.add(workNodeListDTO);
            }else if(houseFlow.getWorkerType() == 10){
                workNodeListDTO = new WorkNodeListDTO();
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE,houseFlow.getWorkerType())
                        .andEqualTo(WorkerType.DATA_STATUS, 0);
                List<WorkerType> workerType = iBillWorkerTypeMapper.selectByExample(example);

                gList = new ArrayList<>();
                listMap = new HashMap<>();
                listMap.put("name","预计工期");
                if(houseFlow.getStartDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                }
                gList.add(listMap);
                listMap = new HashMap<>();
                listMap.put("name","实际工期");
                if(houseFlow.getEndDate() == null){
                    listMap.put("value",0);
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                }
                gList.add(listMap);

                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 10).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 10).count();
                listMap = new HashMap<>();
                if(iEnd == 0){
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",100 + "/" +100);//全部节点
                    workNodeListDTO.setHundred(100);
                }else{
                    listMap.put("name","施工节点");//全部节点
                    listMap.put("value",iEnd + "/" +iStart);//全部节点
                    workNodeListDTO.setHundred((int)(iStart / iEnd * 100));
                }
                gList.add(listMap);

                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 10
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 10
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 10
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                listMap = new HashMap<>();
                listMap.put("name","材料使用");//全部节点
                listMap.put("value",aa + rr + "/" + ss);//全部节点
                gList.add(listMap);
                workNodeListDTO.setLists(gList);

                workNodeListDTO.setImage(address +workerType.get(0).getImage());
                workNodeListDTO.setWorkName("油漆工");
                workList.add(workNodeListDTO);
            }
        }

        return workList;
    }


    /**
     * 查询所有订单
     *
     * @param pageDTO
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse queryAllDeliverOrder(PageDTO pageDTO, String userId, String cityId, String orderStatus) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (StringUtils.isEmpty(userId)) {
                return ServerResponse.createByErrorMessage("用户ID不能为空!");
            }
            if (StringUtils.isEmpty(cityId)) {
                return ServerResponse.createByErrorMessage("城市ID不能为空!");
            }
//            if (StringUtils.isEmpty(orderStatus)) {
////                return ServerResponse.createByErrorMessage("订单状态不能为空!");
////            }
            Example example = new Example(DjDeliverOrder.class);
            example.createCriteria()
                    .andEqualTo(DjDeliverOrder.CITY_ID,cityId)
                    .andEqualTo(DjDeliverOrder.ORDER_STATUS,orderStatus)
                    .andEqualTo(DjDeliverOrder.MEMBER_ID,userId)
                    .andEqualTo(DjDeliverOrder.STOREFONT_ID,userId);
            List<Order> list = IBillDjDeliverOrderMapper.selectByExample(example);

            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("查询所有订单异常", e);
            return ServerResponse.createByErrorMessage("查询所有订单异常" + e);
        }
    }


    /**
     * "获取设计图
     * @param houseId
     * @return
     */
    public ServerResponse getDesignImag(String houseId) {
        //0:量房，，
        List<Object> list = new ArrayList<>();
        QuantityRoom quantityRoom = iBillQuantityRoomMapper.getBillQuantityRoom(houseId, 0);
        List<QuantityRoomImages> quantityRoomImages = getQuantityRoom(quantityRoom);
        if(!quantityRoomImages.isEmpty()){
            list.add(quantityRoomImages);
        }
        //1平面图
        QuantityRoom quantityRoom1 = iBillQuantityRoomMapper.getBillQuantityRoom(houseId, 1);
        List<QuantityRoomImages> quantityRoomImages1 = getQuantityRoom(quantityRoom1);
        if(!quantityRoomImages1.isEmpty()){
            list.add(quantityRoomImages1);
        }
        //2施工图
        QuantityRoom quantityRoom2 = iBillQuantityRoomMapper.getBillQuantityRoom(houseId, 2);
        List<QuantityRoomImages> quantityRoomImages2 = getQuantityRoom(quantityRoom2);
        if(!quantityRoomImages2.isEmpty()){
            list.add(quantityRoomImages2);
        }

        return ServerResponse.createBySuccess("查询成功", list);
    }

    private List<QuantityRoomImages> getQuantityRoom(QuantityRoom quantityRoom) {
        if (quantityRoom == null) {
            return null;
        }
        Example example = new Example(QuantityRoomImages.class);
        example.createCriteria()
                .andEqualTo(QuantityRoomImages.QUANTITY_ROOM_ID, quantityRoom.getId())
                .andEqualTo(QuantityRoomImages.DATA_STATUS, 0);
        example.orderBy(QuantityRoomImages.SORT).asc();
        List<QuantityRoomImages> quantityRoomImages = iBillQuantityRoomImagesMapper.selectByExample(example);
        if (quantityRoomImages == null || quantityRoomImages.size() <= 0) {
            return null;
        }
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        for (QuantityRoomImages quantityRoomImage : quantityRoomImages) {
            quantityRoomImage.initPath(imageAddress);
        }
        return quantityRoomImages;
    }

    /**
     * 获取设计验收过程
     * @param houseId
     * @return
     */
    public ServerResponse getDesignInfo(String houseId) {
        List<QuantityRoomDTO> quantityRoomDTOS = iBillQuantityRoomMapper.getQuantityRoomList(houseId);
        quantityRoomDTOS.forEach(quantityRoomDTO -> {
            if(quantityRoomDTO.getType() == 0){
                quantityRoomDTO.setName("量房");
            }else if(quantityRoomDTO.getType() == 1){
                if(quantityRoomDTO.getFlag() == 0){
                    quantityRoomDTO.setName("平面图审核通过");
                }else if(quantityRoomDTO.getFlag() == 1){
                    quantityRoomDTO.setName("平面图审核未通过");
                }else{
                    quantityRoomDTO.setName("上传平面图");
                }
            }else if(quantityRoomDTO.getType() == 2){
                if (quantityRoomDTO.getFlag() == 0) {
                    quantityRoomDTO.setName("施工图审核通过");
                } else if (quantityRoomDTO.getFlag() == 1) {
                    quantityRoomDTO.setName("施工图审核未通过");
                } else {
                    quantityRoomDTO.setName("上传施工图");
                }
            }
        });

        return ServerResponse.createBySuccess("查询成功", quantityRoomDTOS);
    }



    /**
     * 查询精算信息
     * @param houseId
     * @return
     */
    public ServerResponse getActuaryInfo(String houseId) {

        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID,houseId)
                .andEqualTo(HouseFlow.DATA_STATUS, 0)
                .andEqualTo(HouseFlow.WORKER_TYPE,2);
        List<HouseFlow> houseFlows = iBillHouseFlowMapper.selectByExample(example);

        List<Map<String,Object>> list = new ArrayList<>();
        if(!houseFlows.isEmpty()){
            houseFlows.forEach(houseFlow -> {
                Map<String,Object> map = new HashMap<>();
                map.put("name","精算确认");
                map.put("date",houseFlow.getCreateDate());
                list.add(map);
                if(houseFlow.getStartDate() != null) {
                    map = new HashMap<>();
                    map.put("name","开始精算");
                    map.put("date", houseFlow.getStartDate());
                    list.add(map);
                }
            });
        }

        return ServerResponse.createBySuccess("查询成功", list);
    }



}
