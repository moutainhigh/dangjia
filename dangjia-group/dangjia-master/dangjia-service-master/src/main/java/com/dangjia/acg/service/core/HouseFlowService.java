package com.dangjia.acg.service.core;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.AllgrabBean;
import com.dangjia.acg.dto.group.GroupDTO;
import com.dangjia.acg.dto.pay.WorkerDTO;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.delivery.IOrderItemMapper;
import com.dangjia.acg.mapper.delivery.IOrderMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomImagesMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordProductMapper;
import com.dangjia.acg.mapper.house.IHouseAddressMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.worker.IInsuranceMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishConditionMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.group.Group;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseAddress;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.worker.Insurance;
import com.dangjia.acg.modle.worker.RewardPunishCondition;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.configRule.ConfigRuleUtilService;
import com.dangjia.acg.service.engineer.DjMaintenanceRecordService;
import com.dangjia.acg.service.member.GroupInfoService;
import com.dangjia.acg.service.product.MasterProductTemplateService;
import com.dangjia.acg.service.worker.EvaluateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 17:00
 */
@Service
public class HouseFlowService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;

    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowCountDownTimeMapper houseFlowCountDownTimeMapper;
    @Autowired
    private IMemberMapper memberMapper;

    @Autowired
    private ConfigRuleUtilService configRuleUtilService;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper;
    @Autowired
    private IRewardPunishConditionMapper rewardPunishConditionMapper;
    @Value("${spring.profiles.active}")
    private String active;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private EvaluateService evaluateService;
    @Autowired
    private IBusinessOrderMapper businessOrderMapper;
    @Autowired
    private IInsuranceMapper insuranceMapper;
    @Autowired
    private GroupInfoService groupInfoService;

    @Autowired
    private IHouseAddressMapper iHouseAddressMapper;

    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private IOrderItemMapper orderItemMapper;


    @Autowired
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper;
    @Autowired
    private DjMaintenanceRecordProductMapper maintenanceRecordProductMapper;


    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;
    @Autowired
    private DjMaintenanceRecordService maintenanceRecordService;


    @Autowired
    private IQuantityRoomImagesMapper quantityRoomImagesMapper;

    private static Logger LOG = LoggerFactory.getLogger(HouseFlowService.class);

    /**
     * 判断是否存在自己抢过的包括被拒的
     *
     * @param hwList 自己的所有已抢单
     * @param hf     当前施工单
     * @return 是否存在
     */
    private boolean isHouseWorker(List<HouseWorker> hwList, HouseFlow hf) {
        for (HouseWorker houseWorker : hwList) {
            if (houseWorker.getHouseId().equals(hf.getHouseId())
                    && houseWorker.getWorkerTypeId().equals(hf.getWorkerTypeId())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 抢单播报
     */
    public ServerResponse getGrabBroadcast(String userToken, Integer type) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = memberMapper.selectByPrimaryKey(((Member) object).getId());
            if (member == null) {
                return ServerResponse.createbyUserTokenError();
            }
            List<Map> grabList = new ArrayList<>();//返回的任务list
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.WORK_TYPE, 1)
//                    .andGreaterThan(HouseWorker.CREATE_DATE, DateUtil.delDateMinutes(new Date(),30));
                    .andGreaterThan(HouseWorker.CREATE_DATE, DateUtil.delDateDays(new Date(),30));
            List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的所有已抢单
            for (HouseWorker houseWorker : hwList) {
                Map map =new HashMap();
                if(houseWorker.getType()!=1){
                    House house = houseMapper.selectByPrimaryKey(houseWorker.getHouseId());
                    map.put("name",house.getNoNumberHouseName()+"已被抢单");
                }else{
                    Order order =  orderMapper.selectByPrimaryKey(houseWorker.getBusinessId());
                    MemberAddress memberAddress=iMasterMemberAddressMapper.selectByPrimaryKey(order.getAddressId());
                    map.put("name",memberAddress.getName()+"已被抢单");
                }
                map.put("minutes",DateUtil.daysBetweenMinutes(houseWorker.getCreateDate(),new Date())+"分钟前");
                grabList.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", grabList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,查询失败");
        }
    }

    /**
     * 抢单列表-单量总数
     */
    public ServerResponse getGrabNumber(String userToken,String cityId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member =(Member) object;
            if (member == null) {
                return ServerResponse.createbyUserTokenError();
            }
            Map grab = new HashMap();//返回的任务list
            Integer renovation=0;
            Integer experience=0;
            Integer repair=0;

            List<AllgrabBean> allgrabBeans = houseWorkerMapper.getGrabList(member.getId(),member.getWorkerTypeId(),cityId,member.getWorkerType(),null);//返回的任务list
            for (AllgrabBean allgrabBean : allgrabBeans) {
                //装修单
                if(allgrabBean.getType()==0){
                    HouseFlow houseFlow =houseFlowMapper.selectByPrimaryKey(allgrabBean.getHouseFlowId());
                    Example example = new Example(HouseWorker.class);
                    example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId())
                            .andEqualTo(HouseWorker.TYPE, allgrabBean.getType())
                            .andEqualTo(HouseWorker.HOUSE_ID, houseFlow.getHouseId());
                    List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的所有已抢单
                    if (isHouseWorker(hwList, houseFlow)) {
                        continue;
                    }
                    if (houseFlow.getGrabLock() == 1 && !houseFlow.getNominator().equals(member.getId())) {
                        continue;
                    }
                    House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
                    if (house == null) continue;
                    if (house.getVisitState() == 2 || house.getVisitState() == 3 || house.getVisitState() == 4) {
                        continue;
                    }
                    renovation++;
                }
                //体验单
                if(allgrabBean.getType()==1){
                    experience++;
                }
                //维保单
                if(allgrabBean.getType()==2){
                    Example example = new Example(HouseWorker.class);
                    example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId())
                            .andEqualTo(HouseWorker.HOUSE_ID, allgrabBean.getHouseId())
                            .andEqualTo(HouseWorker.TYPE, allgrabBean.getType())
                            .andEqualTo(HouseWorker.BUSINESS_ID, allgrabBean.getHouseFlowId());
                    List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的是否已抢单
                    if (hwList.size()>0) {
                        continue;
                    }
                    repair++;
                }
            }
            grab.put("renovation",renovation);//装修数
            grab.put("experience",experience);//体验数
            grab.put("repair",repair);//维修数
            return ServerResponse.createBySuccess("查询成功", grab);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,查询失败");
        }
    }
    /**
     * 抢单列表
     */
    public ServerResponse getGrabList(HttpServletRequest request, PageDTO pageDTO, String userToken, String cityId,Integer type) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member =(Member) object;
            if (member == null) {
                return ServerResponse.createbyUserTokenError();
            }
            //工匠没有实名认证不应该展示数据
            if (CommonUtil.isEmpty(member.getWorkerTypeId()) || member.getCheckType() != 2 || member.getRealNameState() != 3) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            String workerTypeId = member.getWorkerTypeId();
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<AllgrabBean> grabList = new ArrayList<>();
            if(type!=null&&type==0&&member.getWorkerType()==3){
                Member worker  = memberMapper.selectByPrimaryKey(member.getId());
                Map map =new HashMap();
                map.put(Member.AUTO_ORDER,worker.getAutoOrder());
                Example example = new Example(HouseWorker.class);
                example.createCriteria().andIn(HouseWorker.WORK_TYPE, Arrays.asList(new String[]{"1","6"}))
                        .andEqualTo(HouseWorker.TYPE, type)
                        .andEqualTo(HouseWorker.WORKER_ID, member.getId());
                map.put(Member.METHODS,houseWorkerMapper.selectCountByExample(example));
                Integer allMethods=configRuleUtilService.getMethodsCount(member.getWorkerTypeId(),member.getEvaluationScore());
                map.put("allMethods",allMethods);
                return ServerResponse.createBySuccess("查询成功", map);
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<AllgrabBean> allgrabBeans = houseWorkerMapper.getGrabList(member.getId(),workerTypeId,cityId,member.getWorkerType(),type);//返回的任务list
            PageInfo pageResult = new PageInfo(allgrabBeans);
            for (AllgrabBean allgrabBean : allgrabBeans) {
                //装修单
                if(allgrabBean.getType()==0){
                    HouseFlow houseFlow =houseFlowMapper.selectByPrimaryKey(allgrabBean.getHouseFlowId());
                    Example example = new Example(HouseWorker.class);
                    example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId())
                            .andEqualTo(HouseWorker.TYPE, type)
                            .andEqualTo(HouseWorker.HOUSE_ID, houseFlow.getHouseId());
                    List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的所有已抢单
                    if (isHouseWorker(hwList, houseFlow)) {
                        continue;
                    }
                    if (houseFlow.getGrabLock() == 1 && !houseFlow.getNominator().equals(member.getId())) {
                        continue;
                    }
                    House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
                    if (house == null) continue;
                    if (house.getVisitState() == 2 || house.getVisitState() == 3 || house.getVisitState() == 4) {
                        continue;
                    }
                    if (houseFlow.getWorkType() == 3) {
                        allgrabBean.setButType("1");
                    }
                    //是否为新单
                    if (DateUtil.addDateDays(houseFlow.getReleaseTime(), 1).getTime() < new Date().getTime()) {
                        allgrabBean.setOrderType(1);
                    }
                    example = new Example(HouseWorker.class);
                    example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseFlow.getHouseId())
                            .andEqualTo(HouseWorker.WORKER_TYPE_ID, houseFlow.getWorkerTypeId());
                    Integer qdjl = houseWorkerMapper.selectCountByExample(example);//查出所有抢单记录
                    //是否为二手商品
                    if (qdjl > 0) {
                        allgrabBean.setOrderType(2);
                    }
                    allgrabBean.setWorkertotal("¥0");//工钱
                    double totalPrice = 0;
                    ServerResponse serverResponse = budgetWorkerAPI.getWorkerTotalPrice(house.getCityId(), houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
                    if (serverResponse.isSuccess()) {
                        if (serverResponse.getResultObj() != null) {
                            JSONObject obj = JSONObject.parseObject(serverResponse.getResultObj().toString());
                            totalPrice = Double.parseDouble(obj.getString("totalPrice"));
                        }
                    }
                    allgrabBean.setWorkertotal("¥" + String.format("%.2f", totalPrice));//工钱
                    grabList.add(allgrabBean);
                }
                //体验单
                if(allgrabBean.getType()==1){
                    allgrabBean.setWorkertotal("¥" + String.format("%.2f", allgrabBean.getWorkertotal()));//工钱
                    grabList.add(allgrabBean);
                }
                //维保单
                if(allgrabBean.getType()==2){
                    Example example = new Example(HouseWorker.class);
                    example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId())
                            .andEqualTo(HouseWorker.HOUSE_ID, allgrabBean.getHouseId())
                            .andEqualTo(HouseWorker.TYPE, type)
                            .andEqualTo(HouseWorker.BUSINESS_ID, allgrabBean.getHouseFlowId());
                    List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的是否已抢单
                    if (hwList.size()>0) {
                        continue;
                    }
                    //是否为新单
                    if (DateUtil.addDateDays(allgrabBean.getCreateDate(), 1).getTime() < new Date().getTime()) {
                        allgrabBean.setOrderType(1);
                    }
                    example = new Example(HouseWorker.class);
                    example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, allgrabBean.getHouseId())
                            .andEqualTo(HouseWorker.TYPE, type)
                            .andEqualTo(HouseWorker.BUSINESS_ID, allgrabBean.getHouseFlowId());
                    Integer qdjl = houseWorkerMapper.selectCountByExample(example);//查出所有抢单记录
                    //是否为二手商品
                    if (qdjl > 0) {
                        allgrabBean.setOrderType(2);
                    }
                    allgrabBean.setWorkertotal("¥0");//工钱
                    Double totalPrice = maintenanceRecordProductMapper.getTotalPriceByRecordId(allgrabBean.getHouseFlowId(),1);
                    if(totalPrice!=null) {
                        allgrabBean.setWorkertotal("¥" + String.format("%.2f", totalPrice));//工钱
                    }
                    grabList.add(allgrabBean);
                }
            }
            if (grabList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            pageResult.setList(grabList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,查询失败");
        }
    }
    /**
     * 抢单详情
     */
    public ServerResponse getGrabInfo(HttpServletRequest request,String userToken, String houseFlowId,Integer type) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = memberMapper.selectByPrimaryKey(((Member) object).getId());
        if (member == null) {
            return ServerResponse.createbyUserTokenError();
        }
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        AllgrabBean allgrabBean = new AllgrabBean();

        if(type==1){
            Order order =  orderMapper.selectByPrimaryKey(houseFlowId);
            MemberAddress memberAddress=iMasterMemberAddressMapper.selectByPrimaryKey(order.getAddressId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
            allgrabBean.setHouseFlowId(order.getId());
            allgrabBean.setWorkerTypeId(member.getWorkerTypeId());
            allgrabBean.setWorkerTypeName(workerType.getName());
            allgrabBean.setCreateDate(order.getCreateDate());
            allgrabBean.setHouseName(memberAddress.getAddress());
            allgrabBean.setType(type);
            allgrabBean.setOrderType(0);
            allgrabBean.setHouseMember( memberAddress.getName());//业主名称
            allgrabBean.setWorkertotal("¥0");//工钱
            double totalPrice = order.getTotalAmount().doubleValue();
            allgrabBean.setWorkertotal("¥" + String.format("%.2f", totalPrice));//工钱
            List<OrderItem> orderItems=orderItemMapper.byOrderIdList(order.getId());
            for (OrderItem orderItem : orderItems) {
                orderItem.setImageUrl(address + orderItem.getImage());
            }
            allgrabBean.setGoodsData(orderItems);
        }else if(type==2){
            DjMaintenanceRecord record=djMaintenanceRecordMapper.selectByPrimaryKey(houseFlowId);
            House house = houseMapper.selectByPrimaryKey(record.getHouseId());
            Member mem = memberMapper.selectByPrimaryKey(house.getMemberId());
            allgrabBean.setWorkerTypeId(record.getWorkerTypeId());
            allgrabBean.setHouseFlowId(record.getId());
            allgrabBean.setCreateDate(record.getCreateDate());
            allgrabBean.setHouseId(house.getId());
            allgrabBean.setHouseName(house.getHouseName());
            allgrabBean.setType(type);
            allgrabBean.setOrderType(0);
            allgrabBean.setSquare( (house.getSquare() == null ? "***" : house.getSquare()) + "m²");//面积
            allgrabBean.setHouseMember( (mem.getNickName() == null ? mem.getName() : mem.getNickName()));//业主名称
            Double totalPrice = maintenanceRecordProductMapper.getTotalPriceByRecordId(record.getId(),1);
            allgrabBean.setWorkertotal("¥0");//工钱
            if(totalPrice!=null) {
                allgrabBean.setWorkertotal("¥" + String.format("%.2f", totalPrice));//工钱
            }
            //2.查询业主提交的维保信息
            Map<String,Object> dataMap=new HashMap<>();
            Map mintenaceRecordInfo=maintenanceRecordService.getMaintenaceRecordInfo(record.getId(),3);
            String isAcceptance="1";//是否等待被接受，0=待接受  1=待抢单
            if(member.getWorkerType()==3&&!CommonUtil.isEmpty(record.getStewardId())){
                isAcceptance="0";
            }
            if(member.getWorkerType()!=3&&!CommonUtil.isEmpty(record.getWorkerMemberId())){
                isAcceptance="0";
            }
            if("0".equals(isAcceptance)){
               Integer hourNum= configRuleUtilService.getGuaranteedQualityTime();
                allgrabBean.setCountDownTime(DateUtil.addDateHours(record.getCreateDate(),hourNum).getTime());
                mintenaceRecordInfo.put("countDownTime",DateUtil.addDateHours(record.getCreateDate(),hourNum));
            }
            allgrabBean.setIsAcceptance(isAcceptance);
            mintenaceRecordInfo.put("isAcceptance",isAcceptance);
            dataMap.put("maintenaceRecoreInfo",mintenaceRecordInfo);
            //判断当前登陆的人员是大管家，还是工匠
            if(mem.getWorkerType()==3){
                //查询勘查费用商品
                dataMap.put("maintenanceRecordProductList",maintenanceRecordService.getMaintenanceProductList(record.getId(),2));//勘查费用商品
            }else{
                //查询质保商品
                dataMap.put("maintenanceRecordProductList",maintenanceRecordService.getMaintenanceProductList(record.getId(),1));//业主提交的质保商品
            }
            List list=new ArrayList();
            list.add(dataMap);
            allgrabBean.setGoodsData(list);

            //是否为新单
            if (DateUtil.addDateDays(record.getCreateDate(), 1).getTime() < new Date().getTime()) {
                allgrabBean.setOrderType(1);
            }
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, record.getHouseId()).andEqualTo(HouseWorker.TYPE, type)
                    .andEqualTo(HouseWorker.BUSINESS_ID, record.getId());
            Integer qdjl = houseWorkerMapper.selectCountByExample(example);//查出所有抢单记录
            //是否为二手商品
            if (qdjl > 0) {
                allgrabBean.setOrderType(2);
            }
        }else {
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId()).andEqualTo(HouseWorker.TYPE, type)
                    .andEqualTo(HouseWorker.HOUSE_ID, houseFlow.getHouseId());
            List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的所有已抢单
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());

            //新手保护数量
            Integer protectMethodsCount = configRuleUtilService.getProtectMethodsCount();
            example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId());
            HouseFlowCountDownTime houseFlowCountDownTime = new HouseFlowCountDownTime();
            if (protectMethodsCount < hwList.size()) {
                //非新手，检测需要的排队的时间
                example = new Example(HouseFlowCountDownTime.class);
                example.createCriteria().andEqualTo(HouseFlowCountDownTime.WORKER_ID, member.getId()).andEqualTo(HouseFlowCountDownTime.HOUSE_FLOW_ID, houseFlow.getId());
                List<HouseFlowCountDownTime> houseFlowDownTimeList = houseFlowCountDownTimeMapper.selectByExample(example);
                if (houseFlowDownTimeList != null && houseFlowDownTimeList.size() > 0) {
                    houseFlowCountDownTime = houseFlowDownTimeList.get(0);
                } else {
                    //如果这个单没有存在倒计时，说明是新单没有被该工匠刷到过
                    houseFlowCountDownTime.setWorkerId(member.getId());//工匠id
                    houseFlowCountDownTime.setHouseFlowId(houseFlow.getId());//houseFlowId
                    BigDecimal evaluation = member.getEvaluationScore();
                    if (evaluation == null) {
                        member.setEvaluationScore(new BigDecimal(60));
                        memberMapper.updateByPrimaryKeySelective(member);
                    }
                    //抢单列表根据积分设置排队时间
                    Date date = configRuleUtilService.getCountDownTime(member.getEvaluationScore());
                    houseFlowCountDownTime.setCountDownTime(date);//可抢单时间
                    List<HouseFlowCountDownTime> houseFlowCountDownTimes = houseFlowCountDownTimeMapper.selectByExample(example);
                    if (houseFlowCountDownTimes == null || houseFlowCountDownTimes.size() == 0) {//新增此数据前查询是否已存在，避免重复插入
                        houseFlowCountDownTimeMapper.insert(houseFlowCountDownTime);
                    }
                }
            } else {
                houseFlowCountDownTime.setCountDownTime(new Date());//可抢单时间
            }
            Member mem = memberMapper.selectByPrimaryKey(house.getMemberId());
            allgrabBean.setButType("0");
            if (houseFlow.getWorkType() == 3) {
                allgrabBean.setButType("1");
            }
            //是否为新单
            if (DateUtil.addDateDays(houseFlow.getReleaseTime(), 1).getTime() < new Date().getTime()) {
                allgrabBean.setOrderType(1);
            }
            example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseFlow.getHouseId()).andEqualTo(HouseWorker.TYPE, type)
                    .andEqualTo(HouseWorker.BUSINESS_ID, houseFlow.getId());
            Integer qdjl = houseWorkerMapper.selectCountByExample(example);//查出所有抢单记录
            //是否为二手商品
            if (qdjl > 0) {
                allgrabBean.setOrderType(2);
            }
            example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseFlow.getHouseId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            allgrabBean.setWorkerTypeName(workerType.getName());
            allgrabBean.setWorkerTypeId(houseFlow.getWorkerTypeId());
            allgrabBean.setHouseFlowId(houseFlow.getId());
            allgrabBean.setCreateDate(houseFlow.getCreateDate());
            allgrabBean.setHouseName(house.getHouseName());
            allgrabBean.setHouseId(house.getId());
            allgrabBean.setType(type);
            allgrabBean.setOrderType(0);
            allgrabBean.setSquare((house.getSquare() == null ? "***" : house.getSquare()) + "m²");//面积
            allgrabBean.setHouseMember((mem.getNickName() == null ? mem.getName() : mem.getNickName()));//业主名称
            allgrabBean.setWorkertotal("¥0");//工钱
            double totalPrice = 0;
            ServerResponse serverResponse = budgetWorkerAPI.getWorkerTotalPrice(house.getCityId(), houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
            if (serverResponse.isSuccess()) {
                if (serverResponse.getResultObj() != null) {
                    JSONObject obj = JSONObject.parseObject(serverResponse.getResultObj().toString());
                    totalPrice = Double.parseDouble(obj.getString("totalPrice"));
                }
            }
            allgrabBean.setWorkertotal("¥" + String.format("%.2f", totalPrice));//工钱

            allgrabBean.setReleaseTime(houseFlow.getReleaseTime());//发布时间
            long countDownTime = houseFlowCountDownTime.getCountDownTime().getTime() - new Date().getTime();//获取倒计时
            allgrabBean.setCountDownTime(countDownTime);//可接单时间

            allgrabBean.setStartDate(houseFlow.getStartDate());
            allgrabBean.setEndDate(houseFlow.getEndDate());
            allgrabBean.setSchedulingDay(0);
            if (houseFlow.getStartDate() != null) {
                int num = 1 + DateUtil.daysofTwo(houseFlow.getStartDate(), houseFlow.getEndDate());//逾期工期天数
                allgrabBean.setSchedulingDay(num);
            }
            //查询销售人员输入的房子类型
            Example example1 = new Example(HouseAddress.class);
            example1.createCriteria()
                    .andEqualTo(HouseAddress.HOUSE_ID, house.getId())
                    .andEqualTo(HouseAddress.DATA_STATUS, 0);
            //查询房子类型
            List<HouseAddress> houseAddress = iHouseAddressMapper.selectByExample(example1);
            if (houseAddress.size() > 0) {
                HouseAddress houseAddressInfo = houseAddress.get(0);
                allgrabBean.setLatitude(houseAddressInfo.getLatitude());
                allgrabBean.setLongitude(houseAddressInfo.getLongitude());
            }

            String quantityRoomImages = quantityRoomImagesMapper.getBillQuantityRoom(allgrabBean.getHouseId());
            allgrabBean.setIsQuantityRoom(CommonUtil.isEmpty(quantityRoomImages)?1:0);
        }
        return ServerResponse.createBySuccess("查询成功", allgrabBean);
    }
    /**
     * 精算生成houseFlow
     */
    public ServerResponse makeOfBudget(String houseId, String workerTypeId) {
        try {
            Map<String, Object> map = new HashMap<>();
            House house = houseMapper.selectByPrimaryKey(houseId);
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(workerTypeId);
            if (house == null) {
                return ServerResponse.createByErrorMessage("根据houseId查询房产失败");
            } else if (workerType == null) {
                return ServerResponse.createByErrorMessage("根据workerTypeId查询失败");
            }
            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo("houseId", houseId).
                    andEqualTo("workerTypeId", workerTypeId);
            List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
            if (houseFlowList.size() > 1) {
                return ServerResponse.createByErrorMessage("精算异常,请联系平台部");
            } else if (houseFlowList.size() == 1) {
                HouseFlow houseFlow = houseFlowList.get(0);
                map.put("houseFlowId", houseFlow.getId());
                return ServerResponse.createBySuccess("查询houseFlow成功", map);
            } else {
                HouseFlow houseFlow = new HouseFlow(true);
                houseFlow.setWorkerTypeId(workerTypeId);
                houseFlow.setWorkerType(workerType.getType());
                houseFlow.setHouseId(house.getId());
                houseFlow.setState(workerType.getState());
                if (!StringUtils.isNoneBlank(house.getCustomSort()))
                    houseFlow.setSort(workerType.getSort());
                else {
                    if (workerType.getType() >= 3) {//只从 3 大管家 开始有自定排序
                        int sort = getCustomSortIndex(house.getCustomSort(), workerType.getType() + "");
                        if (sort == -1) {
                            LOG.info("makeOfBudget sort:" + sort);
                            return ServerResponse.createByErrorMessage("在自定义排序中，不存在 workerType" + workerType.getType());
                        }
                        houseFlow.setSort(sort);
                    } else {
                        houseFlow.setSort(workerType.getSort());
                    }
                }
                houseFlow.setWorkType(1);//生成默认房产，工匠还不能抢
                houseFlow.setCityId(house.getCityId());
                houseFlowMapper.insert(houseFlow);
                map.put("houseFlowId", houseFlow.getId());
                return ServerResponse.createBySuccess("创建houseFlow成功", map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,查询失败");
        }
    }

    /**
     * 查找 自定义 施工顺序 ，找出某个 workerType 是从3（大管家） 开始的几个工序
     *
     * @param customSort
     * @param workerType
     * @return
     */
    public int getCustomSortIndex(String customSort, String workerType) {
        String[] strArr = customSort.split(",");
        int indexSort = 3;
        for (String aStrArr : strArr) {
            if (aStrArr.equals(workerType))
                return indexSort;
            indexSort++;
        }
        LOG.info("getCustomSortIndex 返回错误 -1 customSort:" + customSort + " workerType:" + workerType);
        return -1;

    }

    /**
     * 抢单验证
     *
     * @param userToken 用户登录信息
     * @param cityId    城市ID
     * @return 抢单确认H5页面
     */
    public ServerResponse setGrabVerification(String userToken, String cityId, String houseFlowId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = memberMapper.selectByPrimaryKey(((Member) object).getId());
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            Example example = new Example(RewardPunishRecord.class);
            example.createCriteria().andEqualTo(RewardPunishRecord.MEMBER_ID, member.getId()).andEqualTo(RewardPunishRecord.STATE, "0");
            List<RewardPunishRecord> recordList = rewardPunishRecordMapper.selectByExample(example);
            if (hf.getWorkType() >= 3) {
                return ServerResponse.createByErrorMessage("订单已经被抢了！");
            }
            if (hf.getGrabLock() == 1 && !hf.getNominator().equals(member.getId())) {
                return ServerResponse.createByErrorMessage("订单已经被抢了！");
            }
            if (member.getCheckType() == 0) {
                //审核中的人不能抢单
                return ServerResponse.createByErrorMessage("您的账户正在审核中！");
            }
            if (member.getEvaluationScore().intValue() < 60) {
                //审核中的人不能抢单
                return ServerResponse.createByErrorMessage("当前积分低于60，不能接单，请到当家学院接受培训后，可重新接单！");
            }
            if (member.getCheckType() == 1) {
                //审核未通过 的人不能抢单
                return ServerResponse.createByErrorMessage("您的帐户审核未通过！");
            }
            if (member.getCheckType() == 3) {
                //被禁用的帐户不能抢单
                return ServerResponse.createByErrorMessage("您的帐户已经被禁用！");
            }
            if (member.getCheckType() == 4) {
                //冻结的帐户不能抢单
                return ServerResponse.createByErrorMessage("您的帐户已冻结");
            }
            if (member.getCheckType() == 5) {
                return ServerResponse.createByErrorMessage("您未提交资料审核,请点击【我的】→【我的资料】→完善资料并提交审核！");
            }
            House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
            if (house.getVisitState() == 2) {
                return ServerResponse.createByErrorMessage("该房已休眠");
            }
            if (house.getVisitState() == 3) {
                return ServerResponse.createByErrorMessage("该房已停工");
            }
            if (house.getVisitState() == 4) {
                return ServerResponse.createByErrorMessage("该房已提前结束");
            }
            //通过查看奖罚限制抢单时间限制抢单
            for (RewardPunishRecord record : recordList) {
                example = new Example(RewardPunishCondition.class);
                example.createCriteria().andEqualTo("rewardPunishCorrelationId", record.getRewardPunishCorrelationId());
                List<RewardPunishCondition> conditionList = rewardPunishConditionMapper.selectByExample(example);
                for (RewardPunishCondition rewardPunishCondition : conditionList) {
                    if (rewardPunishCondition.getType() == 3) {
                        Date tt = DateUtil.addDateDays(record.getCreateDate(), rewardPunishCondition.getQuantity().intValue());
//                        Date wraprDate = rewardPunishCondition.getEndTime();
                        Date date = new Date();
                        if (date.getTime() < tt.getTime()) {
                            return ServerResponse.createByErrorMessage("您处于平台处罚期内，" + DateUtil.getDateString2(tt.getTime()) + "以后才能抢单,如有疑问请致电400-168-1231！");
                        }
                    }
                }
            }
            //抢单时间限制
            if (active != null && active.equals("pre")) {
                //新手保护数量
                Integer protectMethodsCount= configRuleUtilService.getProtectMethodsCount();
                example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId());
                Integer hwList = houseWorkerMapper.selectCountByExample(example);//查出自己的所有已抢单
                if(protectMethodsCount<hwList){

                    example = new Example(HouseFlowCountDownTime.class);
                    example.createCriteria().andEqualTo(HouseFlowCountDownTime.WORKER_ID, member.getId()).andEqualTo(HouseFlowCountDownTime.HOUSE_FLOW_ID, hf.getId());
                    List<HouseFlowCountDownTime> houseFlowDownTimeList = houseFlowCountDownTimeMapper.selectByExample(example);
                    if (houseFlowDownTimeList.size() > 0) {
                        HouseFlowCountDownTime houseFlowCountDownTime = houseFlowDownTimeList.get(0);
                        long countDownTime = houseFlowCountDownTime.getCountDownTime().getTime() - new Date().getTime();//获取倒计时
                        if (countDownTime > 0) {//未到时间不能抢单
                            return ServerResponse.createByErrorMessage("您还在排队时间内，请稍后抢单！");
                        }
                    }
                }
            }
            if (member.getWorkerType() > 2) {//其他工人
//                if (hf.getPause() == 1) {
//                    return ServerResponse.createByErrorMessage("该房子已暂停施工！");
//                }
                if (active != null && active.equals("pre")) {
                    //持单数
                    long num = houseWorkerMapper.grabControl(member.getId(), member.getWorkerType());//查询未完工工地
                    Integer methods= configRuleUtilService.getMethodsCount(member.getWorkerTypeId(),member.getEvaluationScore());
                    if (methods > 0 && member.getWorkerType() != 7 && num >= methods) {
                        return ServerResponse.createByErrorMessage("持单已经达到上限,暂不能抢单！");
                    }

                    //暂时注释
                    List<HouseWorker> hwlist = houseWorkerMapper.grabOneDayOneTime(member.getId());
                    if (hwlist.size() > 0) {
                        return ServerResponse.createByErrorMessage("每天只能抢一单哦！");
                    }
                }
            }
            String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.GJPageAddress.AFFIRMGRAB, userToken, cityId, "确认") + "&houseFlowId=" + houseFlowId + "&workerTypeId=" + member.getWorkerTypeId()
                    + "&houseId=" + hf.getHouseId();

            // 抢单详情
            return ServerResponse.createBySuccess("通过验证", url);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("验证出错！");
        }
    }

    /**
     * 工人30分钟自动放弃抢单任务，工人未购买保险或者保险服务剩余天数小于等于60天则自动放弃订单
     *
     * @return
     */
    public ServerResponse autoGiveUpOrder() {
        //找到所有抢单带支付的订单
        Example example = new Example(HouseWorker.class);
        example.createCriteria().andEqualTo(HouseWorker.WORK_TYPE, 1);
        List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);
        for (HouseWorker houseWorker : hwList) {
            if (DateUtil.addDateMinutes(houseWorker.getCreateDate(), 30).getTime() <= (new Date()).getTime()) {
                example = new Example(Insurance.class);
                example.createCriteria().andEqualTo(Insurance.WORKER_ID, houseWorker.getWorkerId()).andIsNotNull(Insurance.END_DATE);
                example.orderBy(Insurance.END_DATE).desc();
                List<Insurance> insurances = insuranceMapper.selectByExample(example);

                //保险服务剩余天数小于等于60天
                Integer daynum = 0;
                if (insurances.size() > 0) {
                    daynum = DateUtil.daysofTwo(new Date(), insurances.get(0).getEndDate());
                }
                //工人未购买保险-不首保，只续保
                if (houseWorker.getWorkerType() > 2 && (insurances.size() > 0 & daynum <= 0)) {
                    Example exampleFlow = new Example(HouseFlow.class);
                    exampleFlow.createCriteria()
                            .andEqualTo(HouseFlow.WORKER_ID, houseWorker.getWorkerId())
                            .andEqualTo(HouseFlow.WORK_TYPE, 3)
                            .andEqualTo(HouseFlow.WORKER_TYPE_ID, houseWorker.getWorkerTypeId())
                            .andEqualTo(HouseFlow.HOUSE_ID, houseWorker.getHouseId());
                    exampleFlow.orderBy(HouseFlow.CREATE_DATE).desc();
                    List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(exampleFlow);
                    if (houseFlowList.size() > 0) {
                        String userToken = redisClient.getCache("role2:" + houseWorker.getWorkerId(), String.class);
                        setGiveUpOrder(userToken, houseFlowList.get(0).getId(),0);
                    }
                }
            }
        }
        return ServerResponse.createBySuccessMessage("ok");
    }

    /**
     * 工匠自动保险单续费
     *
     * @return
     */
    public ServerResponse autoRenewOrder() {
        //找到所有抢单带支付的订单
        List<HouseWorker> hwList = houseWorkerMapper.getWorkerHouse();
        for (HouseWorker houseWorker : hwList) {
            Example example = new Example(Insurance.class);
            example.createCriteria().andEqualTo(Insurance.WORKER_ID, houseWorker.getWorkerId()).andIsNotNull(Insurance.END_DATE);
            example.orderBy(Insurance.END_DATE).desc();
            List<Insurance> insurances = insuranceMapper.selectByExample(example);

            //保险服务剩余天数小于等于60天
            Integer daynum = 0;
            if (insurances.size() > 0) {
                daynum = DateUtil.daysofTwo(new Date(), insurances.get(0).getEndDate());
            }
            //工人未购买保险
            if (houseWorker.getWorkerType() > 2 && ((insurances.size() == 0) || (insurances.size() > 0 & daynum <= 0))) {
                Member operator = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                if (operator != null) {
                    String insuranceMoney = configUtil.getValue(SysConfig.INSURANCE_MONEY, String.class);
                    insuranceMoney = CommonUtil.isEmpty(insuranceMoney) ? "100" : insuranceMoney;
                    Insurance insurance = new Insurance();
                    insurance.setWorkerId(operator.getId());
                    insurance.setWorkerMobile(operator.getMobile());
                    insurance.setWorkerName(operator.getName());
                    insurance.setMoney(new BigDecimal(insuranceMoney));
                    if (insurances.size() == 0) {
                        insurance.setType("0");
                    } else {
                        insurance.setType("1");
                    }
                    if (insurance.getStartDate() == null) {
                        insurance.setStartDate(new Date());
                    }
                    if (insurance.getEndDate() == null) {
                        insurance.setEndDate(new Date());
                    }
                    insurance.setEndDate(DateUtil.addDateYear(insurance.getEndDate(), 1));
                    insuranceMapper.insert(insurance);

                    if (operator != null) {
                        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(operator.getWorkerTypeId());
                        BigDecimal money = new BigDecimal(insuranceMoney);
                        BigDecimal surplusMoney = operator.getSurplusMoney().subtract(money);
                        BigDecimal haveMoney = operator.getHaveMoney().subtract(money);
                        WorkerDetail workerDetail = new WorkerDetail();
                        workerDetail.setName(workerType.getName() + "自动续保");
                        workerDetail.setWorkerId(operator.getId());
                        workerDetail.setWorkerName(operator.getName());
                        workerDetail.setHouseId("");
                        workerDetail.setMoney(money);
                        workerDetail.setWalletMoney(surplusMoney);
                        workerDetail.setHaveMoney(haveMoney);
                        workerDetail.setState(3);
                        workerDetailMapper.insert(workerDetail);
                        operator.setSurplusMoney(surplusMoney);
                        operator.setHaveMoney(haveMoney);
                        memberMapper.updateByPrimaryKeySelective(operator);
                        configMessageService.addConfigMessage(null, AppType.GONGJIANG, operator.getId(), "0",
                                "保险自动续保", "您的保险已到期,为确保您在施工期间的保障,系统已自动续保", "0");
                    }
                }
            }
        }
        return ServerResponse.createBySuccessMessage("ok");
    }

    /**
     * 放弃此单
     *
     * @param userToken   用户登录信息
     * @param houseFlowId 房子ID
     * @return 是否成功
     */
    public ServerResponse setGiveUpOrder(String userToken, String houseFlowId,Integer type) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = memberMapper.selectByPrimaryKey(((Member) object).getId());
            if (member == null) {
                return ServerResponse.createbyUserTokenError();
            }
            String[] amount;
            if(member.getWorkerType()==3){
                amount= configRuleUtilService.getAbandonedCount(1);
            }else{
                amount= configRuleUtilService.getAbandonedCount(0);
            }

            if(type==1){
                Order order =  orderMapper.selectByPrimaryKey(houseFlowId);
                Example example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId()).andEqualTo(HouseWorker.BUSINESS_ID, houseFlowId).andEqualTo(HouseWorker.TYPE, type);
                List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的
                HouseWorker houseWorker = null;
                if(hwList.size()==0){
                    houseWorker = new HouseWorker();
                    houseWorker.setWorkerId(member.getId());
                    houseWorker.setWorkerTypeId(member.getWorkerTypeId());
                    houseWorker.setWorkerType(member.getWorkerType());
                    houseWorker.setWorkType(5);//拒单
                    houseWorker.setIsSelect(0);
                    houseWorker.setPrice(order.getTotalAmount());
                    houseWorker.setType(type);
                    houseWorker.setBusinessId(order.getId());
                    houseWorkerMapper.insert(houseWorker);
                }else{
                    houseWorker = hwList.get(0);
                    houseWorker.setWorkType(7);//抢单状态改为（7抢单后放弃）
                    houseWorker.setIsSelect(0);
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                }
                order.setWorkerId(null);
                order.setWorkerTypeId(null);
                orderMapper.updateByPrimaryKey(order);
                evaluateService.updateMemberIntegral(member.getId(),order.getHouseId(),order.getId(),new BigDecimal(amount[1]),"放弃抢(派)体验单扣分");
            }else if(type==2){
                DjMaintenanceRecord record=djMaintenanceRecordMapper.selectByPrimaryKey(houseFlowId);
                Example example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId()).andEqualTo(HouseWorker.BUSINESS_ID, houseFlowId).andEqualTo(HouseWorker.TYPE, type);
                List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的
                HouseWorker houseWorker;
                if(hwList.size()==0){
                    houseWorker = new HouseWorker();
                    houseWorker.setWorkerId(member.getId());
                    houseWorker.setWorkerTypeId(member.getWorkerTypeId());
                    houseWorker.setWorkerType(member.getWorkerType());
                    houseWorker.setWorkType(5);//拒单
                    houseWorker.setIsSelect(0);
                    //  houseWorker.setPrice(new BigDecimal(record.getSincePurchaseAmount()));
                    houseWorker.setType(type);
                    houseWorker.setBusinessId(record.getId());
                    houseWorkerMapper.insert(houseWorker);
                }else{
                    houseWorker = hwList.get(0);
                    houseWorker.setWorkType(7);//抢单状态改为（7抢单后放弃）
                    houseWorker.setIsSelect(0);
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                }
                if(member.getWorkerType()==3){
                    record.setStewardId(null);
                    // record.setStewardState(null);
                    record.setStewardOrderTime(null);
                    djMaintenanceRecordMapper.updateByPrimaryKey(record);
                }else{
                    record.setWorkerMemberId(null);
                    record.setWorkerTypeId(null);
                    record.setWorkerCreateDate(null);
                    djMaintenanceRecordMapper.updateByPrimaryKey(record);
                }

                evaluateService.updateMemberIntegral(member.getId(),record.getHouseId(),record.getId(),new BigDecimal(amount[1]),"放弃抢(派)维保单扣分");
            }else{

                HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
                Example example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId()).andEqualTo(HouseWorker.HOUSE_ID, hf.getHouseId()).andEqualTo(HouseWorker.TYPE, type);
                List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的
                HouseWorker houseWorker ;
                if(hwList.size()==0){
                    houseWorker = new HouseWorker();
                    houseWorker.setHouseId(hf.getHouseId());
                    houseWorker.setWorkerId(member.getId());
                    houseWorker.setWorkerTypeId(member.getWorkerTypeId());
                    houseWorker.setWorkerType(member.getWorkerType());
                    houseWorker.setPrice(hf.getWorkPrice());
                    houseWorker.setWorkType(5);//拒单
                    houseWorker.setIsSelect(0);
                    houseWorker.setType(type);
                    houseWorker.setBusinessId(houseFlowId);
                    houseWorkerMapper.insert(houseWorker);
                }else{
                    houseWorker = hwList.get(0); //修改此单为放弃
                    houseWorker.setWorkType(7);//抢单状态改为（7抢单后放弃）
                    houseWorker.setIsSelect(0);
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                }

                if (member.getWorkerType() == 3) {//大管家
                    if (hf.getWorkType() == 3 && hf.getSupervisorStart() == 0) {//已抢单待支付，并且未开工(无责取消)
                        hf.setWorkType(2);//抢s单状态更改为待抢单
                        hf.setReleaseTime(new Date());//set发布时间
                        hf.setWorkerId("");
                        houseFlowMapper.updateByPrimaryKeySelective(hf);
                        houseWorker.setWorkType(7);//抢单状态改为（7抢单后放弃）
                        houseWorker.setIsSelect(0);
                        houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                    } else {
                        if (hf.getSupervisorStart() != 0) {//已开工的状态不可放弃
                            return ServerResponse.createBySuccessMessage("您已确认开工，不可放弃！");
                        } else {
                            if (hf.getWorkType() == 4) {//已支付（有责取消）
                                hf.setWorkType(2);//抢单状态更改为待抢单
                                hf.setReleaseTime(new Date());//set发布时间
                                hf.setWorkerId("");
                                houseFlowMapper.updateByPrimaryKeySelective(hf);
                                BigDecimal evaluation = member.getEvaluationScore();
                                if (evaluation == null) {//如果积分为空，默认60分
                                    member.setEvaluationScore(new BigDecimal(60));
                                }
                                BigDecimal evaluation2 = member.getEvaluationScore().subtract(new BigDecimal(2));//积分减2分
                                member.setEvaluationScore(evaluation2);
                                memberMapper.updateByPrimaryKeySelective(member);
                                //修改此单为放弃
                                houseWorker.setWorkType(7);//抢单状态改为（7抢单后放弃）
                                houseWorker.setIsSelect(0);
                                houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                            }
                        }
                    }
                    House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
                    configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "大管家放弃", String.format(DjConstants.PushMessage.STEWARD_ABANDON, house.getHouseName()), "");
                } else {//普通工匠
                    if (hf.getWorkType() == 3) {//已抢单待支付(无责取消)
                        hf.setWorkType(2);//抢单状态更改为待抢单
                        hf.setReleaseTime(new Date());//set发布时间
                        hf.setWorkerId("");
                        houseFlowMapper.updateByPrimaryKeySelective(hf);
                    } else {
                        if (hf.getWorkSteta() != 3 && hf.getWorkSteta() != 0) {//已开工的状态不可放弃
                            return ServerResponse.createByErrorMessage("您已在施工，不可放弃！");
                        } else {
                            if (hf.getWorkType() == 4) {//已支付（有责取消）
                                hf.setWorkType(2);//抢单状态更改为待抢单
                                hf.setReleaseTime(new Date());//set发布时间
                                hf.setWorkerId("");
                                houseFlowMapper.updateByPrimaryKeySelective(hf);
                                BigDecimal evaluation = member.getEvaluationScore();
                                if (evaluation == null) {//如果积分为空，默认60分
                                    member.setEvaluationScore(new BigDecimal(60));
                                }
                                BigDecimal evaluation2 = member.getEvaluationScore().subtract(new BigDecimal(2));//积分减2分
                                member.setEvaluationScore(evaluation2);
                                memberMapper.updateByPrimaryKeySelective(member);
                            }
                        }
                    }
                    if (member.getWorkerType() > 3) {
                        House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
                        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hf.getWorkerTypeId());
                        configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "工匠放弃", String.format(DjConstants.PushMessage.CRAFTSMAN_ABANDON, house.getHouseName(), workerType.getName()), "");
                    }

                    evaluateService.updateMemberIntegral(member.getId(),hf.getHouseId(),hf.getId(),new BigDecimal(amount[1]),"放弃抢(派)装修单扣分");
                }
            }
            return ServerResponse.createBySuccessMessage("放弃成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，放弃失败！");
        }
    }
    /**
     * 业主确认此单
     *
     * @param userToken   用户登录信息
     * @param houseFlowId 房子ID
     * @return 是否成功
     */
    public ServerResponse setConfirm(HttpServletRequest request,String userToken,  String houseFlowId,Integer type) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            if (member == null) {
                return ServerResponse.createbyUserTokenError();
            }
            if(type==2){
                DjMaintenanceRecord record=djMaintenanceRecordMapper.selectByPrimaryKey(houseFlowId);
                Example example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo(HouseWorker.BUSINESS_ID, record.getId())
                        .andEqualTo(HouseWorker.HOUSE_ID, record.getHouseId())
                        .andEqualTo(HouseWorker.WORKER_TYPE_ID, record.getWorkerTypeId())
                        .andEqualTo(HouseWorker.WORK_TYPE, 1);

                HouseWorker houseWorker = houseWorkerMapper.selectOneByExample(example);
                if (houseWorker != null) {
                    houseWorker.setWorkType(6);
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                }

                if(member.getWorkerType()==3){
                    record.setStewardId(member.getId());
                    record.setStewardOrderTime(new Date());
                    djMaintenanceRecordMapper.updateByPrimaryKeySelective(record);
                }else{
                    record.setWorkerMemberId(member.getId());
                    record.setWorkerTypeId(member.getWorkerTypeId());
                    record.setWorkerCreateDate(new Date());
                    djMaintenanceRecordMapper.updateByPrimaryKeySelective(record);
                }
            }else {
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
                House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
//            if(!house.getMemberId().equals(member.getId())){
//                return ServerResponse.createByErrorMessage("非业主本人操作，确认失败！");
//            }
                if (house.getMoney() == null) {
                    house.setMoney(new BigDecimal(0));
                }
                if (houseFlow.getPayStatus() != 1) {
                    Example example = new Example(BusinessOrder.class);
                    example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, houseFlow.getId());
                    List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
                    BusinessOrder businessOrder = null;
                    if (businessOrderList.size() > 0) {
                        businessOrder = businessOrderList.get(0);
                        if (businessOrder.getState() != 3) {
                            return ServerResponse.createByErrorMessage("该工序未支付，请确保已经支付！");
                        }
                    }
                    if (businessOrder == null) {
                        return ServerResponse.createByErrorMessage("该工序未支付，请确保已经支付！");
                    }
                }

                HouseWorker houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 1);
                if (houseWorker != null) {
                    houseWorker.setWorkType(6);
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                }
                /*
                 * 工匠订单
                 */
                HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
                if (hwo != null) {
                    hwo.setWorkerId(houseWorker.getWorkerId());
                    hwo.setPayState(1);
                    houseWorkerOrderMapper.updateByPrimaryKey(hwo);
                }


                houseFlow.setWorkerId(hwo.getWorkerId());
                houseFlow.setWorkType(4);
                houseFlow.setWorkSteta(3);//待交底
                houseFlow.setModifyDate(new Date());
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);

                /*支付完成后将工人拉入激光群组内，方便交流*/
                //售中客服，设计师，精算师无需进群
                if (!("1".equals(houseFlow.getWorkerTypeId()) || "2".equals(houseFlow.getWorkerTypeId()))) {
                    addGroupMember(request, houseFlow.getHouseId(), houseFlow.getWorkerId());
                }
            }
            return ServerResponse.createBySuccessMessage("确认成功！");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createBySuccessMessage("确认异常！");
        }
    }
    /**
     * 审核工序工匠信息界面
     */
    public ServerResponse setCraftsmanInfo(String userToken,  String houseFlowId) {
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        if (houseFlow.getWorkType() != 3) {
            return ServerResponse.createByErrorMessage("该工序订单异常");
        }
        HouseWorker houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 1);
        Member worker = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId()); //查工匠
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
        WorkerDTO workerDTO = new WorkerDTO();
        workerDTO.setHouseWorkerId(houseWorker.getId());//换人参数
        workerDTO.setHead(imageAddress + worker.getHead());
        workerDTO.setWorkerTypeName(workerType.getName());
        workerDTO.setWorkerType(workerType.getType());
        workerDTO.setWorkerTypeId(workerType.getId());
        workerDTO.setWorkerTypeColor(workerType.getColor());
        workerDTO.setWorkerTypeImage(imageAddress + workerType.getImage());
        workerDTO.setWorkerType(workerType.getType());
        workerDTO.setName(worker.getName());
        workerDTO.setWorkerId(houseWorker.getWorkerId());
        workerDTO.setMobile(worker.getMobile());
        workerDTO.setChange(0);//不能换人
        Map map = BeanUtils.beanToMap(houseFlow);
        map.put("workerDTO",workerDTO);//工匠信息
        Example example = new Example(Insurance.class);
        example.createCriteria().andEqualTo(Insurance.WORKER_ID, houseWorker.getWorkerId()).andIsNotNull(Insurance.END_DATE);
        example.orderBy(Insurance.END_DATE).desc();
        List<Insurance> insurances = insuranceMapper.selectByExample(example);
        if(insurances.size()>0){
            map.put("insurances",insurances.get(0));//工匠保险信息
        }

        return ServerResponse.createBySuccess("通过验证", map);
    }
    /**
     * 拉工人进群
     */
    public void addGroupMember(HttpServletRequest request, String houseId, String memberid) {
        try {
            PageDTO pageDTO = new PageDTO();
            pageDTO.setPageNum(1);
            pageDTO.setPageSize(1);
            Group group = new Group();
            group.setHouseId(houseId);
            //获取房子群组
            ServerResponse groups = groupInfoService.getGroups(request, pageDTO, group);
            if (groups.isSuccess()) {
                PageInfo pageInfo = (PageInfo) groups.getResultObj();
                List<GroupDTO> listdto = pageInfo.getList();
                if (listdto != null && listdto.size() > 0) {
                    groupInfoService.editManageGroup(listdto.get(0).getGroupId(), memberid, "");
                }
            }
        } catch (Exception e) {
            System.out.println("建群失败，异常：" + e.getMessage());
        }
    }
    /**
     * 拒绝此单
     *
     * @param userToken   用户登录信息
     * @param houseFlowId 房子ID
     * @return 是否成功
     */
    public ServerResponse setRefuse(String userToken, String cityId, String houseFlowId) {
        try {
            ServerResponse serverResponse = setGrabVerification(userToken, cityId, houseFlowId);
            if (!serverResponse.isSuccess())
                return serverResponse;
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = memberMapper.selectByPrimaryKey(((Member) object).getId());
            if (member == null) {
                return ServerResponse.createbyUserTokenError();
            }
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            //查询排队时间,并修改重排
            Example example = new Example(HouseFlowCountDownTime.class);
            example.createCriteria().andEqualTo("workerId", member.getId()).andEqualTo("houseFlowId", hf.getId());
            List<HouseFlowCountDownTime> houseFlowDownTimeList = houseFlowCountDownTimeMapper.selectByExample(example);
            for (HouseFlowCountDownTime h : houseFlowDownTimeList) {
                BigDecimal evaluation = member.getEvaluationScore();
                if (evaluation == null) {
                    member.setEvaluationScore(new BigDecimal(60));
                    memberMapper.updateByPrimaryKeySelective(member);
                }
                Date date = getCountDownTime(member.getEvaluationScore());
                h.setCountDownTime(date);//可抢单时间
                houseFlowCountDownTimeMapper.updateByPrimaryKeySelective(h);
            }
            return ServerResponse.createBySuccessMessage("拒单成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，拒单失败！");
        }
    }

    /**
     * 抢单列表根据积分设置排队时间
     *
     * @param evaluationScore 积分
     * @return 等待时间
     */
    private Date getCountDownTime(BigDecimal evaluationScore) {
        if (active != null && active.equals("pre")) {
            Calendar now = Calendar.getInstance();
            if (Double.parseDouble(evaluationScore.toString()) < 70) {//积分小于70分，加20分钟
                now.add(Calendar.MINUTE, 20);//当前时间加20分钟
            } else if (Double.parseDouble(evaluationScore.toString()) >= 70 && Double.parseDouble(evaluationScore.toString()) < 80) {
                now.add(Calendar.MINUTE, 10);//当前时间加10分钟
            } else if (Double.parseDouble(evaluationScore.toString()) >= 80 && Double.parseDouble(evaluationScore.toString()) < 90) {
                now.add(Calendar.MINUTE, 5);//当前时间加5分钟
            } else {
                now.add(Calendar.MINUTE, 1);//当前时间加1分钟
            }
            String dateStr = DateUtil.getDateString(now.getTimeInMillis());
            return DateUtil.toDate(dateStr);
        } else {
            return new Date();
        }

    }

    /**
     * 确认开工
     *
     * @param userToken   用户登录信息
     * @param houseFlowId 房子ID
     * @return 是否成功
     */
    public ServerResponse setConfirmStart(HttpServletRequest request, String userToken, String houseFlowId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;

            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询大管家houseFlow
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            if ("0".equals(house.getSchedule())) {
                return ServerResponse.createByErrorMessage("您还没有制作工程日历！");
            }

            house.setConstructionDate(new Date());
            house.setModifyDate(new Date());
            houseMapper.updateByPrimaryKeySelective(house);
            houseFlow.setSupervisorStart(1);//大管家进度改为已开工
            houseFlow.setModifyDate(new Date());
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "大管家开工",String.format( DjConstants.PushMessage.STEWARD_CONSTRUCTION, house.getHouseName()), "");

            //开始建群
            Group group = new Group();
            group.setHouseId(house.getId());
            group.setUserId(house.getMemberId());
            groupInfoService.addGroup(request, group, worker.getId(), "大管家");
            return ServerResponse.createBySuccessMessage("确认开工成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，确认开工失败");
        }
    }

    /**
     * 精算详情需要
     */
    public List<Map<String, String>> getFlowList(String houseId) {
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("state", 0).andGreaterThan("workerType", 2);
        example.orderBy(HouseFlow.SORT).desc();
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        List<Map<String, String>> mapList = new ArrayList<>();
        for (HouseFlow hf : houseFlowList) {
            Map<String, String> map = new HashMap<>();
            map.put("workerTypeId", hf.getWorkerTypeId());
            map.put("name", workerTypeMapper.selectByPrimaryKey(hf.getWorkerTypeId()).getName());
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 根据houseId查询除设计精算外的可用工序
     */
    public List<HouseFlow> getWorkerFlow(String houseId) {
        try {
            return houseFlowMapper.getWorkerFlow(houseId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据houseId和工种类型查询HouseFlow
     */
    public HouseFlow getHouseFlowByHidAndWty(String houseId, Integer workerType) {
        try {
            HouseFlow houseFlow = houseFlowMapper.getHouseFlowByHidAndWty(houseId, workerType);
            return houseFlow;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Description:根据用户id查询[所有房子][正在施工][所有工序]
     * @author: luof
     * @date: 2020-3-11
     */
    public List<Integer> queryWorkerTypeListByMemberId(String memberId){
        return houseFlowMapper.queryWorkerTypeListByMemberId(memberId);
    }
}
