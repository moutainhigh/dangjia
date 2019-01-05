package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HouseResult;
import com.dangjia.acg.dto.core.NodeDTO;
import com.dangjia.acg.dto.house.FlowRecordDTO;
import com.dangjia.acg.dto.house.HouseDTO;
import com.dangjia.acg.dto.house.ShareDTO;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.design.IHouseDesignImageMapper;
import com.dangjia.acg.mapper.house.IHouseExpendMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingLayoutMapper;
import com.dangjia.acg.mapper.matter.IRenovationManualMapper;
import com.dangjia.acg.mapper.matter.IRenovationManualMemberMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.design.HouseDesignImage;
import com.dangjia.acg.modle.group.Group;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseExpend;
import com.dangjia.acg.modle.house.ModelingLayout;
import com.dangjia.acg.modle.matter.RenovationManual;
import com.dangjia.acg.modle.matter.RenovationManualMember;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.member.GroupInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/1 0001
 * Time: 17:56
 */
@Service
public class HouseService {
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
    private IHouseDesignImageMapper houseDesignImageMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IRenovationManualMapper renovationManualMapper;
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

    /**
     * 切换房产
     */
    public ServerResponse setSelectHouse(String userToken, String cityId, String houseId) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("memberId", accessToken.getMember().getId());
        List<House> houseList = iHouseMapper.selectByExample(example);
        for (House house : houseList) {
            if (house.getId().equals(houseId)) {
                house.setIsSelect(1);//改为选择
            } else {
                house.setIsSelect(0);
            }
            iHouseMapper.updateByPrimaryKeySelective(house);
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 房产列表
     */
    public ServerResponse getHouseList(String userToken, String cityId) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("memberId", accessToken.getMember().getId()).andEqualTo("visitState", 1);
        List<House> houseList = iHouseMapper.selectByExample(example);
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        for (House house : houseList) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("houseId", house.getId());
            map.put("houseName", house.getHouseName());
            map.put("task", this.getTask(house.getId()) + "");
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }
    /**待处理任务*/
    private int getTask(String houseId) {
        int task;
        //查询待支付工序
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("workType", 3).andEqualTo("houseId", houseId);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        task = houseFlowList.size();
        //TODO 还有补货 补人工 待审核待查询

        return task;
    }

    /**
     * 我的房子
     */
    public ServerResponse queryMyHouse(String userToken) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("memberId", accessToken.getMember().getId());
        List<House> houseList = iHouseMapper.selectByExample(example);
        List<Map> mapList = new ArrayList<>();
        for (House house : houseList) {
            Map map = BeanUtils.beanToMap(house);
            map.put("houseName", house.getHouseName());
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }

    /**
     * APP我的房产
     */
    public ServerResponse getMyHouse(String userToken, String cityId) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member member = accessToken.getMember();
        //该城市该用户所有开工房产
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("memberId", member.getId())
                .andEqualTo("visitState", 1);
        List<House> houseList = iHouseMapper.selectByExample(example);
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
        } else {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "暂无房产");
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
        for(House elseHouse : houseList){
            if(!elseHouse.getId().equals(houseId)){
                task += this.getTask(elseHouse.getId());
            }
        }
        houseResult.setTask(task);
        houseResult.setState("00000");

        /**展示各种进度*/
        List<HouseFlow> houseFlowList = houseFlowMapper.getAllFlowByHouseId(houseId);
        List<NodeDTO> courseList = new ArrayList<NodeDTO>();
        for (HouseFlow houseFlow : houseFlowList) {
            NodeDTO nodeDTO = new NodeDTO();
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            nodeDTO.setNameA(workerType.getName());
            nodeDTO.setColor(workerType.getColor());
            nodeDTO.setNameC("工序详情");
            String url=configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                    String.format(DjConstants.YZPageAddress.WORKINGDETAILS,userToken,cityId,"工序详情")+"&houseFlowId="+houseFlow.getId();
            nodeDTO.setUrl(url);

            //0待抢单,4待支付,1已支付待发平面图,5平面图发给业主,6平面图审核不通过,7通过平面图待发施工图,2已发给业主施工图,8施工图片审核不通过,3施工图(全部图)审核通过
            if (workerType.getType() == 1){//设计
                nodeDTO.setState(0);
                nodeDTO.setTotal(9);
                if (house.getDesignerOk() == 0){
                    nodeDTO.setRank(1);
                    nodeDTO.setNameB("待抢单");
                }else if (house.getDesignerOk() == 4){
                    nodeDTO.setRank(2);
                    nodeDTO.setNameB("待支付");
                }else if (house.getDesignerOk() == 1){
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB("待上传平面图");
                }else if (house.getDesignerOk() == 5){
                    nodeDTO.setRank(4);
                    nodeDTO.setNameB("待审核平面图");
                }else if (house.getDesignerOk() == 6){
                    nodeDTO.setRank(5);
                    nodeDTO.setNameB("修改平面图");
                }else if(house.getDesignerOk() == 7){
                    nodeDTO.setRank(6);
                    nodeDTO.setNameB("待上传施工图");
                }else if(house.getDesignerOk() == 2){
                    nodeDTO.setRank(7);
                    nodeDTO.setNameB("待审核施工图");
                }else if(house.getDesignerOk() == 8){
                    nodeDTO.setRank(8);
                    nodeDTO.setNameB("修改施工图");
                }else if(house.getDesignerOk() == 3){
                    nodeDTO.setRank(9);
                    nodeDTO.setNameB("设计完成");
                }
            }else if (workerType.getType() == 2){//精算
                //默认0未开始,1已开始精算,-1已精算没有发给业主,2已发给业主,3审核通过,4审核不通过
                nodeDTO.setState(0);
                nodeDTO.setTotal(6);
                if (house.getBudgetOk() == 0){
                    nodeDTO.setRank(1);
                    nodeDTO.setNameB("未开始");
                }else if (house.getBudgetOk() == 1){
                    nodeDTO.setRank(2);
                    nodeDTO.setNameB("精算中");
                }else if (house.getBudgetOk() == -1){
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB("未发送精算");
                }else if (house.getBudgetOk() == 2) {
                    nodeDTO.setRank(4);
                    nodeDTO.setNameB("待审核精算");
                }else if (house.getBudgetOk() == 4){
                    nodeDTO.setRank(5);
                    nodeDTO.setNameB("修改精算");
                }else if (house.getBudgetOk() == 3){
                    nodeDTO.setRank(6);
                    nodeDTO.setNameB("精算完成");
                }
            }else if (workerType.getType() == 3){//管家
                //管家状态1未发布,2待抢单,3待支付,4已支付,5完工
                nodeDTO.setState(0);
                nodeDTO.setTotal(5);
                if (houseFlow.getWorkType() == 1){
                    nodeDTO.setRank(1);
                    nodeDTO.setNameB("未发布");
                }else if (houseFlow.getWorkType() == 2) {
                    nodeDTO.setRank(2);
                    nodeDTO.setNameB("待抢单");
                }else if(houseFlow.getWorkType() == 3){
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB("待支付");
                }else if (houseFlow.getWorkType() == 4){
                    nodeDTO.setRank(4);
                    nodeDTO.setNameB("监工中");
                }else if(houseFlow.getWorkSteta() == 2){
                    nodeDTO.setRank(5);
                    nodeDTO.setNameB("整体完工");
                }
            }else {
                //workType 1还没有发布,2等待被抢，3有工匠抢单,
                //workSteta  3已支付待交底 4施工中 1阶段完工通过,+ 2整体完工通过
                nodeDTO.setState(0);
                if (houseFlow.getWorkerType() == 4){//拆除没有阶段完工
                    nodeDTO.setTotal(6);//总共点
                }else {
                    nodeDTO.setTotal(7);//总共点
                }

                if (houseFlow.getWorkType() == 1){
                    nodeDTO.setRank(1);
                    nodeDTO.setNameB("未发布");
                }else if (houseFlow.getWorkType() == 2) {
                    nodeDTO.setRank(2);
                    nodeDTO.setNameB("待抢单");
                }else if (houseFlow.getWorkType() == 3){
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB("待支付");
                }else if (houseFlow.getWorkType() == 4){//已支付
                    if(houseFlow.getWorkSteta() == 3){
                        nodeDTO.setRank(4);
                        nodeDTO.setNameB("待交底");
                    }else if(houseFlow.getWorkSteta() == 4){
                        nodeDTO.setRank(5);
                        nodeDTO.setNameB("施工中");
                    }else{
                        if (houseFlow.getWorkerType() == 4){//拆除
                            if (houseFlow.getWorkSteta() == 2){
                                nodeDTO.setRank(6);
                                nodeDTO.setNameB("整体完工");
                            }
                        }else {
                            if(houseFlow.getWorkSteta() == 1){
                                nodeDTO.setRank(6);
                                nodeDTO.setNameB("阶段完工");
                            }else if (houseFlow.getWorkSteta() == 2){
                                nodeDTO.setRank(7);
                                nodeDTO.setNameB("整体完工");
                            }
                        }
                    }
                }
            }
            courseList.add(nodeDTO);
        }
        houseResult.setCourseList(courseList);
        return ServerResponse.createBySuccess("查询成功", houseResult);
    }

    /**
     * 开工页面
     */
    public ServerResponse startWorkPage(HttpServletRequest request, String houseId) {
        HouseDTO houseDTO = iHouseMapper.startWorkPage(houseId);
        if (StringUtil.isNotEmpty(houseDTO.getReferHouseId())) {
            House house = iHouseMapper.selectByPrimaryKey(houseDTO.getReferHouseId());
            houseDTO.setReferHouseName(house.getHouseName());
        }
        return ServerResponse.createBySuccess("查询成功", houseDTO);
    }

    /**
     * WEB确认开工
     */
    public ServerResponse startWork(HttpServletRequest request, HouseDTO houseDTO,String members,String prefixs) {
        if (houseDTO.getDecorationType() >= 3 || houseDTO.getDecorationType() == 0) {
            return ServerResponse.createByErrorMessage("装修类型参数错误");
        }
        if (StringUtils.isEmpty(houseDTO.getHouseId()) || StringUtils.isEmpty(houseDTO.getCityId())
                || StringUtils.isEmpty(houseDTO.getStyle()) || StringUtils.isEmpty(houseDTO.getVillageId())) {
            return ServerResponse.createByErrorMessage("参数为空");
        }
        if (houseDTO.getSquare() <= 0) {
            return ServerResponse.createByErrorMessage("面积错误");
        }
        ModelingLayout modelingLayout = modelingLayoutMapper.selectByPrimaryKey(houseDTO.getModelingLayoutId());

        House house = iHouseMapper.selectByPrimaryKey(houseDTO.getHouseId());
        house.setBuildSquare(new BigDecimal(modelingLayout.getBuildSquare()));//建筑面积
        house.setCityId(houseDTO.getCityId());
        house.setCityName(houseDTO.getCityName());
        house.setVillageId(houseDTO.getVillageId());
        house.setResidential(houseDTO.getResidential());
        house.setModelingLayoutId(houseDTO.getModelingLayoutId());
        house.setBuilding(houseDTO.getBuilding());
        house.setUnit(houseDTO.getUnit());
        house.setNumber(houseDTO.getNumber());
        house.setSquare(new BigDecimal(houseDTO.getSquare()));
        house.setReferHouseId(houseDTO.getReferHouseId());
        house.setStyle(houseDTO.getStyle());
        house.setHouseType(houseDTO.getHouseType());
        house.setDrawings(houseDTO.getDrawings());
        house.setDecorationType(houseDTO.getDecorationType());
        HouseFlow houseFlow = null;
        try {
            if (houseDTO.getDecorationType() == 1) {//远程设计
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey("1");
                Example example = new Example(HouseFlow.class);
                example.createCriteria().andEqualTo("houseId", houseDTO.getHouseId()).andEqualTo("workerTypeId", workerType.getId());
                List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
                if (houseFlowList.size() > 1) {
                    return ServerResponse.createByErrorMessage("设计异常,请联系平台部");
                } else if (houseFlowList.size() == 1) {
                    houseFlow = houseFlowList.get(0);
                    houseFlow.setReleaseTime(new Date());//发布时间
                    houseFlow.setMemberId(house.getMemberId());
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setSafe(workerType.getSafeState());
                    houseFlow.setWorkType(2);//开始设计等待被抢
                    houseFlow.setCityId(house.getCityId());
                    houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                } else {
                    houseFlow = new HouseFlow(true);
                    houseFlow.setReleaseTime(new Date());//发布时间
                    houseFlow.setWorkerTypeId(workerType.getId());
                    houseFlow.setWorkerType(workerType.getType());
                    houseFlow.setMemberId(house.getMemberId());
                    houseFlow.setHouseId(house.getId());
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setSafe(workerType.getSafeState());
                    houseFlow.setWorkType(2);//开始设计等待被抢
                    houseFlow.setCityId(house.getCityId());
                    houseFlowMapper.insert(houseFlow);
                }
            } else if (house.getDecorationType() == 2) {//自带设计,上传施工图先
                house.setDesignerOk(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }

        house.setVisitState(1);//开工成单
        iHouseMapper.updateByPrimaryKeySelective(house);

        try {
            //开始建群
            Group group = new Group();
            group.setHouseId(house.getId());
            group.setUserId(house.getMemberId());
//            String members = "";//创建群前，配置的成员userid组，多个以逗号分隔，不包含业主的userid组
//            String prefixs = "";//创建群前，配置的成员userid组的前缀，多个以逗号分隔，不包含业主的前缀组
            groupInfoService.addGroup(request, group, members, prefixs);

            //通知业主确认开工
            configMessageService.addConfigMessage(request,"zx",house.getMemberId(),"0","装修提醒",
                    String.format(DjConstants.PushMessage.START_FITTING_UP,house.getHouseName()) ,"");
            //通知设计师/精算师/大管家 抢单
            Example example = new Example(WorkerType.class);
            example.createCriteria().andCondition(WorkerType.TYPE+" in(1,2) ");
            List<WorkerType> workerTypeList = workerTypeMapper.selectByExample(example);
            for (WorkerType workerType:workerTypeList) {
                List<String> workerTypes = new ArrayList<>();
                workerTypes.add("workerTypeId"+workerType.getId());
                workerTypes.add(house.getId());
                configMessageService.addConfigMessage(request,"gj",StringUtils.join(workerTypes,","),"0",
                        "新的装修订单",DjConstants.PushMessage.SNAP_UP_ORDER ,"");

            }

        } catch (Exception e) {
            System.out.println("建群失败，异常：" + e.getMessage());
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * APP开始装修
     */
    public ServerResponse setStartHouse(String userToken, String cityId, int houseType, int drawings) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        String memberId = accessToken.getMemberId();
        if (StringUtils.isEmpty(memberId)) {
            return ServerResponse.createByErrorMessage("用户id不存在");
        }
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        List<House> houseList = iHouseMapper.selectByExample(example);
        int again = 1;
        if (houseList.size() > 0) {
            again += houseList.size();
            for (House house : houseList) {
                if (house.getVisitState() != 1) { //visitState 1开工，2有意向，3无装修需求，4恶意操作，默认为0
                    return ServerResponse.createByErrorMessage("有房子未确认开工,不能再装");
                }
            }
        }
        City city = iCityMapper.selectByPrimaryKey(cityId);
        House house = new House(true);//新增房产信息
        if (houseList.size() > 0) {
            house.setIsSelect(0);
        }
        house.setMemberId(memberId);//用户id
        house.setCityName(city.getName());//城市名
        house.setCityId(cityId);
        house.setAgain(again);//第几套房产
        house.setHouseType(houseType);//装修的房子类型0：新房；1：老房
        house.setDrawings(drawings);//有无图纸0：无图纸；1：有图纸
        iHouseMapper.insert(house);
        //房子花费
        HouseExpend houseExpend = new HouseExpend(true);
        houseExpend.setHouseId(house.getId());
        houseExpendMapper.insert(houseExpend);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 房子装修列表
     */
    public ServerResponse getList(String memberId) {
        Member member = memberMapper.selectByPrimaryKey(memberId);
        List<House> houseList = iHouseMapper.getList(memberId);
        List<Map<String,Object>> mapList = new ArrayList<>();
        for (House house : houseList){
            Map<String,Object> map = new HashMap<>();
            map.put("houseId", house.getId());
            map.put("cityName", house.getCityName());
            map.put("address", house.getHouseName());
            map.put("memberName", member.getNickName() == null ? member.getName() : member.getNickName());
            map.put("mobile", member.getMobile());
            map.put("visitState", house.getVisitState()); //0待确认开工,1装修中,2休眠中,3已完工
            map.put("showHouse", house.getShowHouse());//0不是，1是 是否精选
            map.put("style", house.getStyle());//设计风格
            map.put("square", house.getSquare());//外框面积
            map.put("buildSquare", house.getSquare()); //建筑面积
            map.put("decorationType", house.getDecorationType()); //装修类型  0表示没有开始，1远程设计，2自带设计，3共享装修
            map.put("houseType", house.getHouseType()); //0：新房；1：老房
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询用户列表成功", mapList);
    }

    /**
     * 修改房子精算状态
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setHouseBudgetOk(String houseId, Integer budgetOk) {
        try {
            House house = iHouseMapper.selectByPrimaryKey(houseId);
            if (house == null) {
                return ServerResponse.createByErrorMessage("修改房子精算状态失败");
            }
            if (budgetOk == 3) {//精算审核通过，调用此方法查询所有验收节点并保存
                HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseId, "2");
                hwo.setHaveMoney(hwo.getWorkPrice());
                houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
                //处理精算工钱
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("精算通过");
                workerDetail.setWorkerId(hwo.getWorkerId());
                workerDetail.setWorkerName(memberMapper.selectByPrimaryKey(hwo.getWorkerId()).getName());
                workerDetail.setHouseId(hwo.getHouseId());
                workerDetail.setMoney(hwo.getWorkPrice());
                workerDetail.setState(0);//进钱
                workerDetailMapper.insert(workerDetail);

                //通知大管家抢单
                HouseFlow houseFlow = houseFlowMapper.getHouseFlowByHidAndWty(houseId, 3);
                houseFlow.setWorkType(2);//待抢单
                houseFlow.setReleaseTime(new Date());//发布时间
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);

                /**
                 * 在这里算出大管家每次巡查拿的钱 和 每次验收拿的钱 记录到大管家的 houseflow里 houseflow,新增两个字段.
                 */
                List<HouseFlow> houseFlowList = houseFlowMapper.getForCheckMoney(houseId);
                int check = 0;//累计大管家总巡查次数
                int time = 0;//累计管家总阶段验收和完工验收次数
                for (HouseFlow hf : houseFlowList) {
                    //查出该工种工钱
                    Double workerTotal = forMasterAPI.getBudgetWorkerPrice(houseId, hf.getWorkerTypeId(), house.getCityId());
                    int inspectNumber = workerTypeMapper.selectByPrimaryKey(hf.getWorkerTypeId()).getInspectNumber();//该工种配置默认巡查次数
                    int thisCheck = (int) (workerTotal / 600);//该工种钱算出来的巡查次数
                    if (thisCheck > inspectNumber) {
                        thisCheck = inspectNumber;
                    }
                    hf.setPatrol(thisCheck);//保存巡查次数
                    houseFlowMapper.updateByPrimaryKeySelective(hf);
                    //累计总巡查
                    check += thisCheck;
                    //累计总验收
                    if (hf.getWorkerType() == 4) {
                        time++;
                    } else {
                        time += 2;
                    }
                }
                //拿到这个大管家工钱
                Double moneySup = forMasterAPI.getBudgetWorkerPrice(houseId, "3", house.getCityId());
                //算管家每次巡查钱
                Double patrolMoney =0d;
                if(check>0){
                     patrolMoney = moneySup * 0.2 / check;
                }
                //算管家每次验收钱
                Double checkMoney = 0d;
                if(time>0){
                    checkMoney = moneySup * 0.3 / time;
                }

                //保存到大管家的houseFlow
                houseFlow.setPatrolMoney(new BigDecimal(patrolMoney));
                houseFlow.setCheckMoney(new BigDecimal(checkMoney));
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            }
            house.setBudgetOk(budgetOk);//精算状态:-1已精算没有发给业主,默认0未开始,1已开始精算,2已发给业主,3审核通过,4审核不通过
            iHouseMapper.updateByPrimaryKeySelective(house);
            return ServerResponse.createBySuccessMessage("修改房子精算状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("修改房子精算状态失败");
        }
    }

    /**
     *  根据城市，小区，最小最大面积查询房子
     */
    public ServerResponse queryHouseByCity(String userToken, String cityId, String villageId, Double minSquare, Double maxSquare, PageDTO pageDTO) {
        try {
            if (pageDTO == null) {
                pageDTO = new PageDTO();
            }
            if (pageDTO.getPageNum() == null) {
                pageDTO.setPageNum(1);
            }
            if (pageDTO.getPageSize() == null) {
                pageDTO.setPageSize(10);
            }
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<ShareDTO> srlist = new ArrayList<>();
            List<House> houseList = iHouseMapper.getSameLayout(cityId, villageId, minSquare, maxSquare);
            for (House house : houseList) {
                ModelingLayout ml = modelingLayoutMapper.selectByPrimaryKey(house.getModelingLayoutId());
                ShareDTO shareDTO = new ShareDTO();
                shareDTO.setType("1");
                if (house.getShowHouse() == 0) {
                    if (accessToken != null) {
                        shareDTO.setName(house.getHouseName());
                    } else {
                        shareDTO.setName(house.getNoNumberHouseName());
                    }
                } else {
                    shareDTO.setName("*栋*单元*号");
                }
                shareDTO.setJianzhumianji("建筑面积:" + (house.getBuildSquare() == null ? "0" : house.getBuildSquare()) + "m²");//建筑面积
                shareDTO.setJvillageacreage("计算面积:" + (house.getSquare() == null ? "0" : house.getSquare()) + "m²");//计算面积
                String biaoqian = house.getLiangDian();//标签
                List<String> biaoqians = new ArrayList<>();
                if (!TextUtils.isEmpty(biaoqian)) {
                    for (String s1 : biaoqian.split(",")) {
                        if (!TextUtils.isEmpty(s1)) {
                            biaoqians.add(s1);
                        }
                    }
                }
                biaoqians.add((house.getBuildSquare() == null ? "0" : house.getBuildSquare()) + "m²");
                shareDTO.setBiaoqian(biaoqians);//亮点标签
                BigDecimal money = house.getMoney();
                shareDTO.setPrice("***" + (accessToken != null && money != null && money.toString().length() > 2 ?
                        money.toString().substring(money.toString().length() - 2) : "00"));//精算总价
                shareDTO.setShowHouse(house.getShowHouse());
                shareDTO.setHouseId(house.getId());
                shareDTO.setVillageId(house.getVillageId());//小区id
                shareDTO.setVillageName(house.getResidential());//小区名
                shareDTO.setLayoutId(house.getModelingLayoutId());//户型id
                shareDTO.setLayoutleft(ml == null ? "" : ml.getName());//户型名称
                String jobLocationDetail=configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                        String.format(DjConstants.YZPageAddress.JOBLOCATIONDETAIL,userToken,cityId,"施工现场")+"&houseId=" + house.getId();
                shareDTO.setUrl(jobLocationDetail);
                Example example = new Example(HouseDesignImage.class);
                example.createCriteria().andEqualTo("houseId", house.getId());
                List<HouseDesignImage> houseDesignImages = houseDesignImageMapper.selectByExample(example);
                if ((houseDesignImages != null) && (houseDesignImages.size() > 0)) {
                    shareDTO.setImageNum(houseDesignImages.size() + "张图片");
                    shareDTO.setImage(address + houseDesignImages.get(0).getImageurl());//户型图片
                } else {
                    shareDTO.setImageNum(0 + "张图片");
                    shareDTO.setImage("");//户型图片
                }
                srlist.add(shareDTO);
            }
            PageInfo pageResult = new PageInfo(houseList);
            pageResult.setList(srlist);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }


    //装修指南
    public ServerResponse getRenovationManual(String userToken, Integer type) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member member = accessToken.getMember();
            Map<String, Object> returnMap = new HashMap<String, Object>();//返回对象
            List<Map<String, Object>> workerTypeList = new ArrayList<Map<String, Object>>();
            Example example = new Example(WorkerType.class);
            example.createCriteria().andEqualTo("state", 0);
            List<WorkerType> wtList = workerTypeMapper.selectByExample(example);
            for (WorkerType wt : wtList) {
                if (wt.getType() == 1 || wt.getType() == 2 || wt.getType() == 3) {
                    continue;
                }
                List<RenovationManual> listR = renovationManualMapper.getRenovationManualByWorkertyId(wt.getId());
                Map<String, Object> wMap = new HashMap<String, Object>();
                wMap.put("workerTypeName", wt.getName());
                List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
                for (RenovationManual r : listR) {
                    Map<String, Object> map = CommonUtil.beanToMap(r);
                    example = new Example(RenovationManualMember.class);
                    example.createCriteria().andEqualTo("renovationManualId", r.getId()).andEqualTo("memberId", member.getId());
                    List<RenovationManualMember> rmList = renovationManualMemberMapper.selectByExample(example);
                    RenovationManualMember rm = new RenovationManualMember();
                    if (type == 1 && rmList.size() > 0) {//如果只查未勾选
                        continue;
                    }
                    if (rmList.size() > 0) {
                        map.put("isSelect", 1);//选中
                    } else {
                        map.put("isSelect", 0);//未选中
                    }
                    listMap.add(map);
                }
                wMap.put("rList", listMap);
                workerTypeList.add(wMap);
            }
            returnMap.put("list", workerTypeList);//大list
            return ServerResponse.createBySuccess("获取装修指南成功", returnMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取装修指南失败");
        }
    }

    /**
     * 保存装修指南
     *
     * @param userToken
     * @param savaList
     * @return
     */
    public ServerResponse savaRenovationManual(String userToken, String savaList) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member member = accessToken.getMember();
            if (savaList != null) {
                Example example = new Example(RenovationManualMember.class);
                example.createCriteria().andEqualTo("memberId", member.getId());
                renovationManualMemberMapper.deleteByExample(example);
                JSONArray jsonArr = JSONArray.parseArray(savaList);//格式化jsonArr
                for (int i = 0; i < jsonArr.size(); i++) {
                    JSONObject obj = jsonArr.getJSONObject(i);
                    if (obj.getInteger("state") == 1) {
                        RenovationManualMember rm = new RenovationManualMember();
                        rm.setMemberId(member.getId());
                        rm.setRenovationManualId(obj.getString("id"));
                        rm.setState(1);
                        renovationManualMemberMapper.insertSelective(rm);
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("保存装修指南成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,保存装修指南失败");
        }
    }

    /**
     * 施工记录（首页滚动）
     */
    public ServerResponse queryHomeConstruction() {
        try {
            Map<Integer, String> applyTypeMap = new HashMap<>();
            applyTypeMap.put(DjConstants.ApplyType.MEIRI_WANGGONG, "今日已完工");
            applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG, "今日阶段完工");
            applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG, "今日整体完工");
            applyTypeMap.put(DjConstants.ApplyType.TINGGONG, "今日已停工");
            applyTypeMap.put(DjConstants.ApplyType.MEIRI_KAIGONG, "已开工");
            applyTypeMap.put(DjConstants.ApplyType.YOUXIAO_XUNCHA, "今日已巡查");
            applyTypeMap.put(DjConstants.ApplyType.WUREN_XUNCHA, "今日已巡查");
            applyTypeMap.put(DjConstants.ApplyType.ZUIJIA_XUNCHA, "今日已巡查");
            PageHelper.startPage(1, 20);
            Example example = new Example(HouseFlowApply.class);
            example.orderBy(HouseFlowApply.CREATE_DATE).desc();
            List<HouseFlowApply> hfaList = houseFlowApplyMapper.selectByExample(example);
            List listMap = new ArrayList<>();
            for (HouseFlowApply hfa : hfaList) {
                StringBuffer name = new StringBuffer();
                House house = iHouseMapper.selectByPrimaryKey(hfa.getHouseId());
                if (house != null) {
                    name.append(house.getNoNumberHouseName());
                }
                Member member = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
                name.append(" " + workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());
                name.append(applyTypeMap.get(hfa.getApplyType()));
                listMap.add(name.toString());
            }
            return ServerResponse.createBySuccess("ok", listMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,查询施工记录失败");
        }
    }

    /**
     * 施工记录
     */
    public ServerResponse queryConstructionRecord(String houseId, Integer pageNum, Integer pageSize) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<HouseFlowApply> hfaList = houseFlowApplyMapper.queryAllHfaByHouseId(houseId);
        PageInfo pageResult = new PageInfo(hfaList);
        List<Map<String, Object>> listMap = this.houseFlowApplyDetail(hfaList);
        if (listMap == null){
            return ServerResponse.createByErrorMessage("系统出错,查询施工记录失败");
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("查询施工记录成功", pageResult);
    }

    /**
     * 工序记录
     */
    public ServerResponse queryFlowRecord(String houseFlowId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            FlowRecordDTO flowRecordDTO = new FlowRecordDTO();
            List<HouseFlowApply> hfaList = houseFlowApplyMapper.queryFlowRecord(houseFlowId);
            List<Map<String, Object>> listMap = this.houseFlowApplyDetail(hfaList);
            flowRecordDTO.setFlowApplyMap(listMap);

            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.HOUSE_FLOW_ID, houseFlowId).andNotEqualTo(HouseWorker.WORK_TYPE,5);
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);
            List<Map<String, Object>> houseWorkerMap = new ArrayList<>();
            for(HouseWorker houseWorker : houseWorkerList){
                Map<String, Object> map = new HashMap<>();
                Member member = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                map.put("workerHead", address + member.getHead());//工人头像
                map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());//工匠类型
                map.put("mobile", member.getMobile());//工人电话
                map.put("workerId", member.getId());//工人电话
                if(houseWorker.getWorkType() == 1){
                    map.put("workerName", member.getName() + "(待支付)");//工人名称
                }else if(houseWorker.getWorkType() == 6){
                    map.put("workerName", member.getName());//工人名称
                }else {
                    map.put("workerName", member.getName() + "(已更换)");//2被换人,4已开工被换人,7抢单后放弃
                }
                map.put("workType", houseWorker.getWorkType());
                houseWorkerMap.add(map);
            }
            flowRecordDTO.setHouseWorkerMap(houseWorkerMap);

            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            //已验收节点
            List<TechnologyRecord> checkList = technologyRecordMapper.allChecked(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
            List<Map<String, Object>> nodeMap = new ArrayList<>();
            for (TechnologyRecord technologyRecord : checkList){
                Map<String, Object> map = new HashMap<>();
                map.put("name", technologyRecord.getName());
                map.put("time", technologyRecord.getModifyDate());
                String[] imgArr = technologyRecord.getImage().split(",");
                for(int i = 0; i < imgArr.length; i++){
                    imgArr[i] = address + imgArr[i];
                }
                map.put("imgArr", imgArr);
                nodeMap.add(map);
            }
            flowRecordDTO.setNodeMap(nodeMap);

            return ServerResponse.createBySuccess("查询工序记录成功", flowRecordDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询工序记录失败");
        }
    }
    /**记录*/
    private List<Map<String, Object>> houseFlowApplyDetail(List<HouseFlowApply> hfaList){
        try {
            Map<Integer, String> applyTypeMap = new HashMap<>();
            applyTypeMap.put(DjConstants.ApplyType.MEIRI_WANGGONG, "每日完工申请");
            applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG, "阶段完工申请");
            applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG, "整体完工申请");
            applyTypeMap.put(DjConstants.ApplyType.TINGGONG, "停工申请");
            applyTypeMap.put(DjConstants.ApplyType.MEIRI_KAIGONG, "每日开工");
            applyTypeMap.put(DjConstants.ApplyType.YOUXIAO_XUNCHA, "巡查");
            applyTypeMap.put(DjConstants.ApplyType.WUREN_XUNCHA, "巡查");
            applyTypeMap.put(DjConstants.ApplyType.ZUIJIA_XUNCHA, "巡查");
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Map<String, Object>> listMap = new ArrayList<>();
            for (HouseFlowApply hfa : hfaList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", hfa.getId());
                Member member = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
                map.put("workerHead", address + member.getHead());//工人头像
                map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());//工匠类型
                map.put("workerName", member.getName());//工人名称
                Example example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo("houseId", hfa.getHouseId()).andEqualTo("workerId", hfa.getWorkerId());
                List<HouseWorker> listHw = houseWorkerMapper.selectByExample(example);
                if (listHw.size() > 0) {
                    HouseWorker houseWorker = listHw.get(0);
                    if (houseWorker.getWorkType() == 4) {
                        map.put("isNormal", "已更换");//施工状态
                    } else {
                        map.put("isNormal", "正常施工");
                    }
                } else {
                    map.put("isNormal", "正常施工");
                }
                map.put("content", hfa.getApplyDec());
                example = new Example(HouseFlowApplyImage.class);
                example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, hfa.getId());
                List<HouseFlowApplyImage> hfaiList = houseFlowApplyImageMapper.selectByExample(example);
                String[] imgArr = new String[hfaiList.size()];
                for (int i = 0; i < hfaiList.size(); i++) {
                    HouseFlowApplyImage hfai = hfaiList.get(i);
                    String string = hfai.getImageUrl();
                    imgArr[i] = address + string;
                }
                map.put("imgArr", imgArr);
                map.put("applyType", applyTypeMap.get(hfa.getApplyType()));
                map.put("createDate", hfa.getCreateDate().getTime());
                listMap.add(map);
            }
            return listMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据id查询房子信息
     *
     * @return
     */
    public House getHouseById(String houseId) {
        try {
            return iHouseMapper.selectByPrimaryKey(houseId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 参考报价
     *
     * @return
     */
    public ServerResponse getReferenceBudget(String villageId, Double minSquare, Double maxSquare, Integer houseType) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            List<House> listHouse = iHouseMapper.getReferenceBudget(villageId, houseType, minSquare, maxSquare);
            if (listHouse.size() > 0) {//根据条件查询所选小区总价最少的房子
                request.setAttribute(Constants.CITY_ID, listHouse.get(0).getCityId());
                return budgetWorkerAPI.gatEstimateBudgetByHId(request, listHouse.get(0).getId());
            }
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "暂无所需报价");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,查询参考报价失败");
        }
    }
}
