package com.dangjia.acg.service.delivery;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.UserAPI;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
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
import com.dangjia.acg.dto.design.CollectDataDTO;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.dto.design.WorkChartListDTO;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import com.dangjia.acg.dto.order.DOrderArrFineInfoDTO;
import com.dangjia.acg.dto.order.DOrderArrInfoDTO;
import com.dangjia.acg.dto.order.DOrderFineInfoDTO;
import com.dangjia.acg.dto.order.DOrderInfoDTO;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.order.IBillHouseMapper;
import com.dangjia.acg.mapper.order.IBillQuantityRoomImagesMapper;
import com.dangjia.acg.mapper.order.IBillQuantityRoomMapper;
import com.dangjia.acg.mapper.refund.IBillMendOrderMapper;
import com.dangjia.acg.mapper.sale.IBillDjAlreadyRobSingleMapper;
import com.dangjia.acg.mapper.sale.IBillMemberMapper;
import com.dangjia.acg.mapper.sale.IBillUserMapper;
import com.dangjia.acg.mapper.storeFront.IBillStorefrontMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.menu.MenuConfiguration;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.sale.royalty.DjAlreadyRobSingle;
import com.dangjia.acg.modle.sale.royalty.DjRoyaltyDetailsSurface;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.product.BillProductTemplateService;
import com.dangjia.acg.util.HouseUtil;
import com.dangjia.acg.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import com.dangjia.acg.common.model.PageDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DjDeliverOrderService {
    @Autowired
    private IBillDjDeliverOrderMapper iBillDjDeliverOrderMapper;
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

    @Autowired
    private HouseAPI houseAPI;
    @Autowired
    private IBillStorefrontMapper iBillStorefrontMapper;

    @Autowired
    private ForMasterAPI forMasterAPI;

    @Autowired
    private BillDjDeliverOrderSplitItemMapper billDjDeliverOrderSplitItemMapper;

    @Autowired
    private BillDjDeliverSplitDeliverMapper billDjDeliverSplitDeliverMapper;

    @Autowired
    private BillProductTemplateService billProductTemplateService;


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
     * @param userToken
     * @return
     */
    public ServerResponse queryOrderNumber(String userToken,String houseId){
        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);

        Object object = memberAPI.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        JSONObject job = (JSONObject)object;
        Member member = job.toJavaObject(Member.class);
        HouseResult houseResult = new HouseResult();
        object = getHouse(member.getId(), houseResult);
        if (object instanceof ServerResponse) {
            return ServerResponse.createByErrorCodeResultObj(ServerCode.NO_DATA.getCode(), HouseUtil.getWorkerDatas(null, address));
        }

        House house = (House) object;
        houseId = house.getId();

//        House house = iBillHouseMapper.selectByPrimaryKey(houseId);

        //订单状态 1待付款，2已付款，3待收货
        WorkInFoDTO workInFoDTO = new WorkInFoDTO();
        workInFoDTO.setHouseId(houseId);
        Example example = new Example(Order.class);
        //待付款
        example.createCriteria().andEqualTo(Order.HOUSE_ID,houseId)
                .andEqualTo(Order.DATA_STATUS, 0)
                .andEqualTo(Order.ORDER_STATUS,1);
        Map<String,Object> map = new HashMap<>();
        map.put("stayPayment", iBillDjDeliverOrderMapper.selectCountByExample(example));

        //已付款
        example = new Example(Order.class);
        example.createCriteria().andEqualTo(Order.HOUSE_ID,houseId)
                .andEqualTo(Order.DATA_STATUS, 0)
                .andEqualTo(Order.ORDER_STATUS,2);
        map.put("alreadyPayment", iBillDjDeliverOrderMapper.selectCountByExample(example));

        //待收货
        example = new Example(Order.class);
        example.createCriteria().andEqualTo(Order.HOUSE_ID,houseId)
                .andEqualTo(Order.DATA_STATUS, 0)
                .andEqualTo(Order.ORDER_STATUS,3);
        map.put("stayGoods", iBillDjDeliverOrderMapper.selectCountByExample(example));
        map.put("complete", 0);
        map.put("after", 0);
        workInFoDTO.setOrderMap(map);

        if(house != null) {
            String houseName = house.getResidential() + house.getBuilding() + "栋" +
                    house.getUnit() + "单元" + house.getNumber() + "号";
            workInFoDTO.setHouseName(houseName);
        }

        HouseFlowInfoDTO houseFlowInfoDTO = new HouseFlowInfoDTO();
        //查询今日播报信息
        List<HouseFlowDataDTO> sowingList = iBillDjDeliverOrderMapper.queryApplyDec();
        if(sowingList != null && !sowingList.isEmpty()){
            houseFlowInfoDTO.setDate(sowingList.get(0).getCreateDate());
            houseFlowInfoDTO.setHouseFlowDataDTOS(sowingList);
        }
        houseFlowInfoDTO.setNumber(iBillDjDeliverOrderMapper.queryApplyPayState(houseId).size());
        workInFoDTO.setHouseFlowInfoDTO(houseFlowInfoDTO);

        //1-下单后（销售阶段） 2-下单后（销售接单） 3-下单后（设计阶段）4-下单后（精算阶段）5-下单后(施工阶段)
        if(house != null && house.getVisitState() == 0){
            workInFoDTO.setHouseType(1);
        }else if(house != null && house.getIsRobStats() == 1){
            workInFoDTO.setHouseType(2);
        }

        //查询当前房子状态
        List<WorkerTypeDTO> wtdList = iBillDjDeliverOrderMapper.queryType(houseId);
        if(!wtdList.isEmpty()){
            workInFoDTO.setType(wtdList.get(0).getType());
            if(wtdList.get(0).getType() == 1){
                //3-下单后（设计阶段）
                workInFoDTO.setHouseType(3);
            }else if(wtdList.get(0).getType() == 2){
                //4-下单后（精算阶段）
                workInFoDTO.setHouseType(4);
            }else{
                //5-下单后(施工阶段
                workInFoDTO.setHouseType(5);
            }
        }


        //设置菜单
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

    public List< Map<String,Object>> optimizationHander(HouseFlow houseFlow){
        //1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工
        Map<String,Object> maps = new HashMap<>();
        if(houseFlow.getWorkerType() == 1){
            maps.put("i",houseFlow.getWorkerType());
            maps.put("name","设计师");
        }else if(houseFlow.getWorkerType() == 2){
            maps.put("i",houseFlow.getWorkerType());
            maps.put("name","精算师");
        }else if(houseFlow.getWorkerType() == 3){
            maps.put("i",houseFlow.getWorkerType());
            maps.put("name","大管家");
        }else if(houseFlow.getWorkerType() == 4){
            maps.put("i",houseFlow.getWorkerType());
            maps.put("name","拆除");
        }else if(houseFlow.getWorkerType() == 6){
            maps.put("i",houseFlow.getWorkerType());
            maps.put("name","水电工");
        }else if(houseFlow.getWorkerType() == 7){
            maps.put("i",houseFlow.getWorkerType());
            maps.put("name","防水");
        }else if(houseFlow.getWorkerType() == 8){
            maps.put("i",houseFlow.getWorkerType());
            maps.put("name","泥工");
        }else if(houseFlow.getWorkerType() == 9){
            maps.put("i",houseFlow.getWorkerType());
            maps.put("name","木工");
        }else if(houseFlow.getWorkerType() == 10){
            maps.put("i",houseFlow.getWorkerType());
            maps.put("name","油漆工");
        }
        List< Map<String,Object>> mapList = new ArrayList<>();
        mapList.add(maps);
        return mapList;
    }

    /**
     * 获取工序信息
     * @param houseId
     * @return
     */
    public List<Object> summationMethod(List<HouseFlow> houseFlows,String houseId){
        WorkNodeListDTO workNodeListDTO;
        //查询工序节点
        List<NodeNumberDTO> nodeNumberDTOS = iBillDjDeliverOrderItemMapper.queryNodeNumber(houseId);
        //查询材料数量
        List<MaterialNumberDTO> materialNumberDTOS = iBillDjDeliverOrderItemMapper.queryMaterialNumber(houseId);

        Example example;
        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<Object> workList = new ArrayList<>();
        List<Map<String,Object>> gList;
        Map<String,Object> listMap;

        //1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工
        for (HouseFlow houseFlow : houseFlows) {
                List<Map<String,Object>> mapList = optimizationHander(houseFlow);
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE,houseFlow.getWorkerType())
                        .andEqualTo(WorkerType.DATA_STATUS, 0);
                List<WorkerType> workerType = iBillWorkerTypeMapper.selectByExample(example);
                gList = new ArrayList<>();
                listMap = new HashMap<>();
                workNodeListDTO = new WorkNodeListDTO();
                listMap.put("name","预计工期" );
                if(houseFlow.getStartDate() == null){
                    listMap.put("value",0 + "天");
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()) + "天");
                }
                gList.add(listMap);
                listMap = new HashMap<>();
                listMap.put("name","实际工期");
                if(houseFlow.getEndDate() == null){
                    listMap.put("value",0 + "天");
                }else{
                    listMap.put("value",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()) + "天");
                }
                gList.add(listMap);

                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == mapList.get(0).get("i")).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() ==  mapList.get(0).get("i")).count();
                listMap = new HashMap<>();
                if(iEnd == 0){
                    listMap.put("name","施工节点" );
                    listMap.put("value",0 + "/" + 0);
                    workNodeListDTO.setHundred(0);
                }else{
                    listMap.put("name","施工节点" );
                    listMap.put("value",iEnd + "/" +iStart);
                    workNodeListDTO.setHundred((int)(iStart / iEnd * 100));
                }
                gList.add(listMap);

                int ss = materialNumberDTOS.stream().filter(x -> x.getType() ==  mapList.get(0).get("i")
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() ==  mapList.get(0).get("i")
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() ==  mapList.get(0).get("i")
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                listMap = new HashMap<>();
                listMap.put("name","材料使用");//全部节点
                listMap.put("value",aa + rr + "/" + ss);//全部节点
                gList.add(listMap);
                workNodeListDTO.setLists(gList);

                workNodeListDTO.setImage(address +workerType.get(0).getImage());
                workNodeListDTO.setWorkName((String) mapList.get(0).get("name"));
                workList.add(workNodeListDTO);
        }

        return workList;
    }




    /**
     * "获取设计图
     * @param houseId
     * @return
     */
    public ServerResponse getDesignImag(String houseId) {
        //0:量房，，
        List<Object> list = new ArrayList<>();
        WorkChartListDTO workChartListDTO;

        QuantityRoom quantityRoom = iBillQuantityRoomMapper.getBillQuantityRoom(houseId, 0);
        List<QuantityRoomImages> quantityRoomImages = getQuantityRoom(quantityRoom);
        if(quantityRoomImages != null && !quantityRoomImages.isEmpty()){
            workChartListDTO = new WorkChartListDTO();
            workChartListDTO.setDate(quantityRoomImages.get(0).getCreateDate());
            workChartListDTO.setName("量房");
            workChartListDTO.setList(quantityRoomImages);
            workChartListDTO.setType(0);
            list.add(workChartListDTO);
        }
        //1平面图
        QuantityRoom quantityRoom1 = iBillQuantityRoomMapper.getBillQuantityRoom(houseId, 1);
        List<QuantityRoomImages> quantityRoomImages1 = getQuantityRoom(quantityRoom1);
        if(quantityRoomImages1 != null && !quantityRoomImages1.isEmpty()){
            workChartListDTO = new WorkChartListDTO();
            workChartListDTO.setDate(quantityRoomImages1.get(0).getCreateDate());
            workChartListDTO.setName("平面图");
            workChartListDTO.setList(quantityRoomImages1);
            list.add(workChartListDTO);
        }
        //2施工图
        QuantityRoom quantityRoom2 = iBillQuantityRoomMapper.getBillQuantityRoom(houseId, 2);
        List<QuantityRoomImages> quantityRoomImages2 = getQuantityRoom(quantityRoom2);
        if(quantityRoomImages2 != null && !quantityRoomImages2.isEmpty()){
            workChartListDTO = new WorkChartListDTO();
            workChartListDTO.setDate(quantityRoomImages2.get(0).getCreateDate());
            workChartListDTO.setName("施工图");
            workChartListDTO.setList(quantityRoomImages2);
            list.add(workChartListDTO);
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
        if(!quantityRoomDTOS.isEmpty()){
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

        }

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



    /**
     * 查询验收过程
     * @param houseId
     * @return
     */
    public ServerResponse getCollectInfo(String houseId) {
        CollectDataDTO collectDataDTO = new CollectDataDTO();
        //查询预计完工时间
        Date date = iBillQuantityRoomMapper.selectMaxEndDate(houseId);

        //查询验收过程时间
        List<HouseFlowApply> houseFlowApplies = iBillQuantityRoomMapper.selectApplyInfo(houseId);

        //查询工人进场时间
        List<HouseWorker> houseWorkers = iBillQuantityRoomMapper.selectWorkerInfo(houseId);

        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map;


        for (HouseFlowApply houseFlowApply : houseFlowApplies) {
            for (HouseWorker houseWorker:houseWorkers) {
               if(houseFlowApply.getWorkerType() == 4 && houseWorker.getWorkerType() == 4){
                    map = new HashMap<>();
                    map.put("name","拆除进场");
                    map.put("date",houseWorker.getModifyDate());
                    list.add(map);
                    if(houseFlowApply.getApplyType() == 1){
                        map = new HashMap<>();
                        map.put("name","拆除申请阶段完工");
                        map.put("date",houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if(houseFlowApply.getMemberCheck() == 1){
                        map = new HashMap<>();
                        map.put("name","拆除申请阶段通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }else{
                        map = new HashMap<>();
                        map.put("name","拆除申请阶段未通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                }else if(houseFlowApply.getWorkerType() == 6 && houseWorker.getWorkerType() == 6){
                    map = new HashMap<>();
                    map.put("name","水电工进场");
                    map.put("date",houseWorker.getModifyDate());
                    list.add(map);
                    if(houseFlowApply.getApplyType() == 1){
                        map = new HashMap<>();
                        map.put("name","水电工申请阶段完工");
                        map.put("date",houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if(houseFlowApply.getMemberCheck() == 1){
                        map = new HashMap<>();
                        map.put("name","水电工申请阶段通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }else{
                        map = new HashMap<>();
                        map.put("name","水电工申请阶段未通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                }else if(houseFlowApply.getWorkerType() == 7 && houseWorker.getWorkerType() == 7){
                    map = new HashMap<>();
                    map.put("name","防水进场");
                    map.put("date",houseWorker.getModifyDate());
                    list.add(map);
                    if(houseFlowApply.getApplyType() == 1){
                        map = new HashMap<>();
                        map.put("name","防水申请阶段完工");
                        map.put("date",houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if(houseFlowApply.getMemberCheck() == 1){
                        map = new HashMap<>();
                        map.put("name","防水申请阶段通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }else{
                        map = new HashMap<>();
                        map.put("name","防水申请阶段未通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                }else if(houseFlowApply.getWorkerType() == 8 && houseWorker.getWorkerType() == 8){
                    map = new HashMap<>();
                    map.put("name","泥工进场");
                    map.put("date",houseWorker.getModifyDate());
                    list.add(map);
                    if(houseFlowApply.getApplyType() == 1){
                        map = new HashMap<>();
                        map.put("name","泥工申请阶段完工");
                        map.put("date",houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if(houseFlowApply.getMemberCheck() == 1){
                        map = new HashMap<>();
                        map.put("name","泥工申请阶段通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }else{
                        map = new HashMap<>();
                        map.put("name","泥工申请阶段未通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                }else if(houseFlowApply.getWorkerType() == 9 && houseWorker.getWorkerType() == 9){
                    map = new HashMap<>();
                    map.put("name","木工进场");
                    map.put("date",houseWorker.getModifyDate());
                    list.add(map);
                    if(houseFlowApply.getApplyType() == 1){
                        map = new HashMap<>();
                        map.put("name","木工申请阶段完工");
                        map.put("date",houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if(houseFlowApply.getMemberCheck() == 1){
                        map = new HashMap<>();
                        map.put("name","木工申请阶段通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }else{
                        map = new HashMap<>();
                        map.put("name","木工申请阶段未通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                }else if(houseFlowApply.getWorkerType() == 10 && houseWorker.getWorkerType() == 10){
                    map = new HashMap<>();
                    map.put("name","油漆工进场");
                    map.put("date",houseWorker.getModifyDate());
                    list.add(map);
                    if(houseFlowApply.getApplyType() == 1){
                        map = new HashMap<>();
                        map.put("name","油漆工申请阶段完工");
                        map.put("date",houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if(houseFlowApply.getMemberCheck() == 1){
                        map = new HashMap<>();
                        map.put("name","油漆工申请阶段通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }else{
                        map = new HashMap<>();
                        map.put("name","油漆工申请阶段未通过");
                        map.put("date",houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                }
            }
        }
        for (HouseWorker houseWorker1 : houseWorkers) {
            if (houseWorker1.getWorkerType() == 3) {
                map = new HashMap<>();
                map.put("name", "大管家进场");
                map.put("date", houseWorker1.getModifyDate());
                list.add(map);
            }
        }
        collectDataDTO.setName("预计完工");
        collectDataDTO.setDate(date);
        collectDataDTO.setList(list);
        return ServerResponse.createBySuccess("查询成功", collectDataDTO);
    }

    /**
     * 订单列表（待收货、已经完成） --发货单
     * @param pageDTO
     * @param userToken
     * @param houseId
     * @param cityId
     * @param orderStatus
     * @return
     */
    public ServerResponse queryDeliverOrderDsdListByStatus(PageDTO pageDTO, String userToken, String houseId, String cityId, String orderStatus) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<DjSplitDeliverOrderDTO> list = iBillDjDeliverOrderMapper.querySplitDeliverByHouse(cityId, houseId, orderStatus);
            if (list != null && list.size() > 0)
            {
                for (DjSplitDeliverOrderDTO djSplitDeliverOrderDTO : list) {
                    String number=djSplitDeliverOrderDTO.getNumber();//要货单号
                    Example example=new Example(OrderSplitItem.class);
                    example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID,number);
                    List<OrderSplitItem> orderSplitItemlist=billDjDeliverOrderSplitItemMapper.selectByExample(example);
                    if(orderSplitItemlist!=null)
                    {
                        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                        for (OrderSplitItem orderSplitItem :orderSplitItemlist) {
                            if (orderSplitItem!=null)
                            orderSplitItem.setImage(address+orderSplitItem.getImage());
                        }
                        djSplitDeliverOrderDTO.setOrderSplitItemlist(orderSplitItemlist);
                        djSplitDeliverOrderDTO.setItemListSize(orderSplitItemlist.size());//要货数大小
                    }

                }
            }

            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("订单列表（待收货、已经完成）异常", e);
            return ServerResponse.createByErrorMessage("订单列表（待收货、已经完成）异常" + e);
        }
    }

    /**
     * 订单列表（全部订单、待付款、待发货） -- 订单列表
     * @param pageDTO
     * @param userToken
     * @param houseId
     * @param cityId
     * @param orderStatus
     * @return
     */
    public ServerResponse queryDeliverOrderListByStatus(PageDTO pageDTO, String userToken, String houseId, String cityId, String orderStatus) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject)object;
            Member member = job.toJavaObject(Member.class);

            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<DjDeliverOrderDTO> list = iBillDjDeliverOrderMapper.selectDeliverOrderByHouse(cityId, houseId, orderStatus);
            for (DjDeliverOrderDTO jDeliverOrderDTO : list) {
                String orderId = jDeliverOrderDTO.getId();
                List<DjDeliverOrderItemDTO > djDeliverOrderItemDTOList = iBillDjDeliverOrderItemMapper.orderItemList(houseId, orderId);
                if (djDeliverOrderItemDTOList == null) {
                    jDeliverOrderDTO.setOrderItemlist(null);
                    jDeliverOrderDTO.setTotalSize(0);
                }
                 Integer i=0;// 是否预约计数
                for (DjDeliverOrderItemDTO djDeliverOrderItemDTO :djDeliverOrderItemDTOList)
                {
                    String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                    djDeliverOrderItemDTO.setImageDetail(address+djDeliverOrderItemDTO.getImage());
                    String isReservationDeliver=djDeliverOrderItemDTO.getIsReservationDeliver();
                    if(isReservationDeliver!=null&&isReservationDeliver.equals("1"))
                    {
                        i++;
                    }
                }
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                jDeliverOrderDTO.setStorefrontIcon(imageAddress+jDeliverOrderDTO.getStorefrontIcon());//店铺图标
                jDeliverOrderDTO.setOrderItemlist(djDeliverOrderItemDTOList);
                jDeliverOrderDTO.setTotalSize(djDeliverOrderItemDTOList.size());

                Integer orderSource=jDeliverOrderDTO.getOrderSource();//订单来源(1,精算制作，2业主自购，3购物车）
                String dborderStatus=jDeliverOrderDTO.getOrderStatus();//订单状态

                if(dborderStatus!=null&&dborderStatus.equals("5"))
                {//已取消
                    List< Map<String, Object>> rows=new ArrayList<Map<String, Object>>();
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("name", "再次购买");
                    resultMap.put("type", 2);
                    rows.add(resultMap);
                    if(orderSource == 2 || orderSource == 3)
                    {
                        Map<String, Object> resultMap2 = new HashMap<>();
                        resultMap2.put("name", "取消订单");
                        resultMap2.put("type", 1);
                        rows.add(resultMap2);
                    }
                    jDeliverOrderDTO.setButtonList(rows);
                }
                if(dborderStatus!=null&&dborderStatus.equals("1"))
                {
                    //1待付款
                    List< Map<String, Object>> rows=new ArrayList<Map<String, Object>>();
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("name", "付款");
                    resultMap.put("type", 4);
                    rows.add(resultMap);
                    if(orderSource == 2 || orderSource == 3)
                    {
                        Map<String, Object> resultMap2 = new HashMap<>();
                        resultMap2.put("name", "取消订单");
                        resultMap2.put("type", 1);
                    }
                    jDeliverOrderDTO.setButtonList(rows);
                }
                //2已付款就是待发货
                if(dborderStatus!=null&&dborderStatus.equals("2"))
                {
                    if(i>0)
                    {
                        List< Map<String, Object>> rows=new ArrayList<Map<String, Object>>();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("name", "预约发货");
                        resultMap.put("type", 5);
                        rows.add(resultMap);
                        jDeliverOrderDTO.setButtonList(rows);
                    }
                }
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("查询所有订单异常", e);
            return ServerResponse.createByErrorMessage("查询所有订单异常" + e);
        }
    }


    /**
     * 订单详情明细
     *@param orderId
     * @return
     */
    public ServerResponse deliverOrderItemDetail(String orderId,Integer orderStatus ) {
        try {

            Order order= iBillDjDeliverOrderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                return ServerResponse.createByErrorMessage("该订单不存在");
            }
            House house= houseAPI.selectHouseById(order.getHouseId());
            if (house == null) {
                return ServerResponse.createByErrorMessage("该房产不存在");
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class); //图片地址
            AppOrderDetailDTO appOrderDetailDTO=iBillDjDeliverOrderMapper.selectOrderDetailById(order.getHouseId(),order.getId());
            if(appOrderDetailDTO!=null)
            {
                List<AppOrderItemDetailDTO> list= iBillDjDeliverOrderMapper.selectOrderItemDetailById(appOrderDetailDTO.getOrderId(),orderStatus);
                for (AppOrderItemDetailDTO appOrderItemDetailDTO :list) {
                    if (appOrderItemDetailDTO!=null) {
                        String productId = appOrderItemDetailDTO.getProductId();
                        String brandName = forMasterAPI.brandName("", productId);  //通过商品id去关联，然后组合商品名称
                        appOrderItemDetailDTO.setBrandName(brandName);//组合后的商品名称
                        appOrderItemDetailDTO.setImageDetail(address + appOrderItemDetailDTO.getImage());//商品图片详情
                    }
                    //待发货：增加退款按钮
                    if(orderStatus==2)
                    {
                        List< Map<String, Object>> rows=new ArrayList<Map<String, Object>>();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("name", "退款");
                        resultMap.put("type", 1);
                        rows.add(resultMap);
                        appOrderItemDetailDTO.setDetailMaplist(rows);
                    }
                    //已经取消：增加加入购物车按钮
                    if(orderStatus==5)
                    {
                        List< Map<String, Object>> rows=new ArrayList<Map<String, Object>>();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("name", "加入购物车");
                        resultMap.put("type", 2);
                        rows.add(resultMap);
                        appOrderItemDetailDTO.setDetailMaplist(rows);
                    }
                    //已经完成：增加退款和加入购物车按钮
                    if(orderStatus==4)
                    {
                        List< Map<String, Object>> rows=new ArrayList<Map<String, Object>>();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("name", "退款");
                        resultMap.put("type", 1);
                        rows.add(resultMap);
                        Map<String, Object> resultMap2 = new HashMap<>();
                        resultMap2.put("name", "加入购物车");
                        resultMap2.put("type", 2);
                        rows.add(resultMap2);
                        appOrderItemDetailDTO.setDetailMaplist(rows);
                    }
                }
                appOrderDetailDTO.setDetaillist(list);
                String houseName = house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber() + "号";
                appOrderDetailDTO.setShipAddress(houseName);//房子地址
            }
            return ServerResponse.createBySuccess("查询成功", appOrderDetailDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询订单明细异常");
        }

    }

    /**
     * 查询全部订单
     * @param userId
     * @param cityId
     * @param orderKey
     * @param state
     * @return
     */
    public ServerResponse queryOrderInfo(PageDTO pageDTO, String userId, String cityId,
                                         String orderKey, int state) {

        Example example = new Example(Storefront.class);
        example.createCriteria().andEqualTo(Storefront.USER_ID, userId).
                andEqualTo(Storefront.CITY_ID, cityId);
        List<Storefront> storefrontList = iBillStorefrontMapper.selectByExample(example);
        if(storefrontList == null && storefrontList.size() == 0){
            return ServerResponse.createByErrorMessage("门店不存在");
        }

        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        DOrderArrInfoDTO dOrderArrInfoDTO = new DOrderArrInfoDTO();
        Map<String,Object> map = new HashMap<>();
        map.put("orderKey",orderKey);
        map.put("state",state);
        map.put("storefontId",storefrontList.get(0).getId());
        List<DOrderInfoDTO>  orderInfoDTOS = iBillDjDeliverOrderMapper.queryOrderInfo(map);
        PageInfo  orderInfoDTOSs = new PageInfo(orderInfoDTOS);
        map = new HashMap<>();
        map.put("storefontId",storefrontList.get(0).getId());
        List<DOrderInfoDTO>  arrOrderInfoDTOS = iBillDjDeliverOrderMapper.queryOrderInfo(map);
        dOrderArrInfoDTO.setNoPaymentNumber((int)arrOrderInfoDTOS.stream().filter(x -> x.getState() == 2 || x.getState() == 1).count());
        dOrderArrInfoDTO.setYesPaymentNumber((int)arrOrderInfoDTOS.stream().filter(x -> x.getState() == 3).count());
        dOrderArrInfoDTO.setYesCancel((int)arrOrderInfoDTOS.stream().filter(x -> x.getState() == 4).count());
        dOrderArrInfoDTO.setList(orderInfoDTOSs);

      return ServerResponse.createBySuccess("查询成功",dOrderArrInfoDTO);
    }


    /**
     * 查询订单详情
     * @param orderId
     * @return
     */
    public ServerResponse queryOrderFineInfo(PageDTO pageDTO, String orderId) {
        if (CommonUtil.isEmpty(orderId)) {
            return ServerResponse.createByErrorMessage("订单id不能为空");
        }
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        DOrderArrFineInfoDTO dOrderArrFineInfoDTO = new DOrderArrFineInfoDTO();
        List<DOrderFineInfoDTO> dOrderArrInfoDTO =  iBillDjDeliverOrderMapper.queryOrderFineInfo(orderId);

        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        for (DOrderFineInfoDTO dOrderFineInfoDTO : dOrderArrInfoDTO) {
            if(dOrderFineInfoDTO.getImage().contains(",")){
                List<String> result = Arrays.asList(dOrderFineInfoDTO.getImage().split(","));
                dOrderFineInfoDTO.setImage(imageAddress + result.get(0));
            }else{
                dOrderFineInfoDTO.setImage(imageAddress + dOrderFineInfoDTO.getImage());
            }
        }
        PageInfo  orderInfoDTOSs = new PageInfo(dOrderArrInfoDTO);
        Map<String,Object> map = new HashMap<>();
        map.put("id",orderId);
        List<DOrderInfoDTO>  orderInfoDTOS = iBillDjDeliverOrderMapper.queryOrderInfo(map);
        if(orderInfoDTOS != null && orderInfoDTOS.size() > 0){
            dOrderArrFineInfoDTO.setActualPaymentPrice(orderInfoDTOS.get(0).getActualPaymentPrice());
            dOrderArrFineInfoDTO.setHouseName(orderInfoDTOS.get(0).getHouseName());
            dOrderArrFineInfoDTO.setOrderNumber(orderInfoDTOS.get(0).getOrderNumber());
            dOrderArrFineInfoDTO.setOrderPayTime(orderInfoDTOS.get(0).getOrderPayTime());
            dOrderArrFineInfoDTO.setPboId(orderInfoDTOS.get(0).getPboId());
            if(orderInfoDTOS.get(0).getState() == 1 || orderInfoDTOS.get(0).getState() == 2){
                dOrderArrFineInfoDTO.setState(0);
            }else if(orderInfoDTOS.get(0).getState() == 3){
                dOrderArrFineInfoDTO.setState(1);
            }

            if(orderInfoDTOS.get(0).getPboImage() != null && orderInfoDTOS.get(0).getPboImage() != ""){
                List<String> result = Arrays.asList(orderInfoDTOS.get(0).getPboImage().split(","));
                List<String> strList = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    String a = result.get(i);
                    String str = imageAddress + result.get(i);
                    strList.add(str);
                }
                dOrderArrFineInfoDTO.setImageList(strList);
            }


        }
        dOrderArrFineInfoDTO.setList(orderInfoDTOSs);
        return ServerResponse.createBySuccess("查询成功", dOrderArrFineInfoDTO);
    }


    /**
     * 订单快照
     * @param orderId
     * @param orderStatus
     * @return
     */
    public ServerResponse orderSnapshop(String orderId, Integer orderStatus) {
        try {
            return null;
        } catch (Exception e) {
            logger.error("订单快照异常", e);
            return ServerResponse.createByErrorMessage("订单快照异常" + e);
        }
    }

    /**
     * 发货详情
     * @param orderId
     * @param orderStatus
     * @return
     */
    public ServerResponse shippingDetail(String orderId, Integer orderStatus) {
        try {
            return null;
        } catch (Exception e) {
            logger.error("发货详情异常", e);
            return ServerResponse.createByErrorMessage("发货详情异常" + e);
        }
    }

    /**
     * 搬运费详情
     * @param orderId
     * @param orderStatus
     * @return
     */
    public ServerResponse stevedorageCostDetail(PageDTO pageDTO,String orderId, Integer orderStatus) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<CostDetailDTO> list=iBillDjDeliverOrderMapper.queryStevedorage(orderId);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("搬运费详情异常", e);
            return ServerResponse.createByErrorMessage("搬运费详情异常" + e);
        }
    }


    /**
     * 运费详情
     * @param orderId
     * @param orderStatus
     * @return
     */
    public ServerResponse transportationCostDetail(PageDTO pageDTO,String orderId, Integer orderStatus) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<CostDetailDTO> list=iBillDjDeliverOrderMapper.queryTransportationCost(orderId);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("运费详情异常", e);
            return ServerResponse.createByErrorMessage("运费详情异常" + e);
        }
    }




    /**
     * App订单列表（待收货、已经完成） --发货单
     * @param pageDTO
     * @param userToken
     * @param houseId
     * @param cityId
     * @param orderStatus
     * @return
     */
    public ServerResponse queryAppOrderList(PageDTO pageDTO, String userToken, String houseId, String cityId, String orderStatus) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<DjSplitDeliverOrderDTO> list = iBillDjDeliverOrderMapper.queryAppOrderList(cityId, houseId, orderStatus);
            if (list != null && list.size() > 0)
            {
                for (DjSplitDeliverOrderDTO djSplitDeliverOrderDTO : list) {

                    String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                    djSplitDeliverOrderDTO.setStorefrontIcon(address + djSplitDeliverOrderDTO.getStorefrontIcon());
                    Example example=new Example(OrderSplitItem.class);
                    example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID,djSplitDeliverOrderDTO.getId());
                    List<OrderSplitItem> orderSplitItemlist=billDjDeliverOrderSplitItemMapper.selectByExample(example);
                    if(orderSplitItemlist!=null)
                    {
                        djSplitDeliverOrderDTO.setImage(getStartTwoImage1(orderSplitItemlist,address));
                        djSplitDeliverOrderDTO.setItemListSize(orderSplitItemlist.size());//要货数大小
                    }
                }
            }

            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("订单列表（待收货、已经完成）异常", e);
            return ServerResponse.createByErrorMessage("订单列表（待收货、已经完成）异常" + e);
        }
    }


    /**
     * 获取前两个商品的图片
     * @return
     */
    String getStartTwoImage1(List<OrderSplitItem> os,String address){
        String imageUrl="";
        if(os!=null && os.size()>0){
            for(OrderSplitItem ap : os){
                String image = ap.getImage();
                //添加图片详情地址字段
                if(StringUtils.isNotBlank(image)){
                    String[] imgArr = image.split(",");
                    if(StringUtils.isBlank(imageUrl)){
                        imageUrl=address+imgArr[0];
                    }else{
                        imageUrl=imageUrl+","+address+imgArr[0];
                        break;
                    }
                }
            }
        }
        return imageUrl;
    }

    /**
     * App 确定收货
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateAppOrderStats(String lists,String id){

        try {
            if (CommonUtil.isEmpty(lists)) {
                return ServerResponse.createByErrorMessage("lists不能为空");
            }
            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }
            SplitDeliver splitDeliver = new SplitDeliver();
            splitDeliver.setId(id);
            splitDeliver.setRecTime(new Date());
            splitDeliver.setShippingState(2);
            billDjDeliverSplitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);

            JSONArray list = JSON.parseArray(lists);
            for (int i = 0; i < list.size(); i++) {
                JSONObject JS = list.getJSONObject(i);
                OrderSplitItem orderSplitItem = new OrderSplitItem();
                orderSplitItem.setId(JS.getString("id"));
                orderSplitItem.setReceive(JS.getDouble("receive"));
                billDjDeliverOrderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
            }
            return ServerResponse.createBySuccessMessage("收货成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("收货失败");
        }
    }


    /**
     * app订单详情查询（待收货，待安装，已完成）
     * @param id
     * @return
     */
    public ServerResponse  queryAppOrderInFoList(String id,Integer shippingState){
        if (CommonUtil.isEmpty(id)) {
            return ServerResponse.createByErrorMessage("id不能为空");
        }
        if(CommonUtil.isEmpty(shippingState)){
            return ServerResponse.createByErrorMessage("shippingState不能为空");
        }
        Example example=new Example(SplitDeliver.class);
        example.createCriteria().andEqualTo(SplitDeliver.SHIPPING_STATE,shippingState)
                .andEqualTo(SplitDeliver.ID,id);
        SplitDeliver splitDeliver = billDjDeliverSplitDeliverMapper.selectOneByExample(example);

        if(splitDeliver == null){
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
        }
        Storefront storefront = iBillStorefrontMapper.selectByPrimaryKey(splitDeliver.getStorefrontId());
        if(storefront == null){
            return ServerResponse.createByErrorMessage("门店不存在");
        }

        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        OrderCollectInFoDTO orderCollectInFoDTO = new OrderCollectInFoDTO();
        House house= houseAPI.selectHouseById(splitDeliver.getHouseId());
        String houseName = house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber() + "号";
        orderCollectInFoDTO.setHouseName(houseName);
        orderCollectInFoDTO.setCreateDate(splitDeliver.getCreateDate());
        orderCollectInFoDTO.setRecTime(splitDeliver.getRecTime());
        orderCollectInFoDTO.setSendTime(splitDeliver.getRecTime());
        orderCollectInFoDTO.setNumber(splitDeliver.getNumber());
        orderCollectInFoDTO.setTotalAmount(splitDeliver.getTotalAmount());
        orderCollectInFoDTO.setInstallName(splitDeliver.getInstallName());//安装人姓名
        orderCollectInFoDTO.setInstallMobile(splitDeliver.getInstallMobile());//安装人号码
        orderCollectInFoDTO.setDeliveryMobile(splitDeliver.getDeliveryMobile());;//送货人号码
        orderCollectInFoDTO.setDeliveryName(splitDeliver.getDeliveryName());//送货人姓名

        orderCollectInFoDTO.setStorefrontIcon(address + storefront.getSystemLogo());//店铺图标
        orderCollectInFoDTO.setStorefrontName(storefront.getStorefrontName());//店铺名称

        example=new Example(OrderSplitItem.class);
        example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID,splitDeliver.getId());
        List<OrderSplitItem> orderSplitItem = billDjDeliverOrderSplitItemMapper.selectByExample(example);

        Map<String,Object> map;
        List<Map<String,Object>> list = new ArrayList<>();
        if(orderSplitItem != null && orderSplitItem.size() >0){
            for (OrderSplitItem splitItem : orderSplitItem) {
                String str = iBillDjDeliverOrderMapper.queryValueIdArr(splitItem.getId());
                String valueNameArr = billProductTemplateService.getNewValueNameArr(str);
                map = new HashMap<>();
                map.put("productName",splitItem.getProductName());
                map.put("num",splitItem.getNum());
                map.put("price", splitItem.getPrice() + "/" + splitItem.getUnitName());
                map.put("valueNameArr",valueNameArr);
                List<String> result = Arrays.asList(splitItem.getImage().split(","));
                map.put("image",address + result.get(0));
                list.add(map);
            }
        }
        orderCollectInFoDTO.setList(list);

        return ServerResponse.createBySuccess("查询成功", orderCollectInFoDTO);
    }



    /**
     * 我的订单,待发货
     * @param pageDTO
     * @param houseId
     * @return
     */
    public ServerResponse queryDeliverOrderHump(PageDTO pageDTO, String houseId, String state) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<OrderStorefrontDTO> orderStorefrontDTOS=null;
            if (state.equals("2")){
                orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryDeliverOrderObligation(houseId);
                orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                    List<AppointmentDTO> appointmentDTOS = iBillDjDeliverOrderMapper.queryDeliverOrderItemObligation(orderStorefrontDTO.getOrderId());
                    orderStorefrontDTO.setProductCount(appointmentDTOS.size());
                    orderStorefrontDTO.setProductImageArr(getStartTwoImage(appointmentDTOS,imageAddress));
                    orderStorefrontDTO.setStorefrontLogo(imageAddress+orderStorefrontDTO.getStorefrontLogo());
                    if(null!=appointmentDTOS && appointmentDTOS.size()>1){
                        AppointmentDTO appointmentDTO = appointmentDTOS.get(0);
                        orderStorefrontDTO.setProductName(appointmentDTO.getProductName());
                    }
                });
            }else {
                orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryDeliverOrderHump(houseId,state);
                orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                    List<AppointmentDTO> appointmentDTOS = iBillDjDeliverOrderMapper.queryAppointmentHump(orderStorefrontDTO.getOrderId(), state);
                    orderStorefrontDTO.setProductCount(appointmentDTOS.size());
                    orderStorefrontDTO.setProductImageArr(getStartTwoImage(appointmentDTOS,imageAddress));
                    orderStorefrontDTO.setStorefrontLogo(imageAddress+orderStorefrontDTO.getStorefrontLogo());
                    if(null!=appointmentDTOS && appointmentDTOS.size()>1){
                        AppointmentDTO appointmentDTO = appointmentDTOS.get(0);
                        orderStorefrontDTO.setProductName(appointmentDTO.getProductName());
                    }
                });
            }
            if(orderStorefrontDTOS.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            PageInfo pageResult = new PageInfo(orderStorefrontDTOS);
            return  ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 获取前两个商品的图片
     * @return
     */
    String getStartTwoImage(List<AppointmentDTO> appointmentDTOS,String address){
        String imageUrl="";
        if(appointmentDTOS!=null&&appointmentDTOS.size()>0){
            for(AppointmentDTO ap:appointmentDTOS){
                String image=ap.getImage();
                //添加图片详情地址字段
                if(StringUtils.isNotBlank(image)){
                    String[] imgArr = image.split(",");
                    if(StringUtils.isBlank(imageUrl)){
                        imageUrl=address+imgArr[0];
                    }else{
                        imageUrl=imageUrl+","+address+imgArr[0];
                        break;
                    }
                }

            }
        }
        return imageUrl;
    }

}
