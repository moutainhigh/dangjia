package com.dangjia.acg.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.basics.WorkerGoodsAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.nimserver.NIMPost;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.complain.ComplainInfoDTO;
import com.dangjia.acg.dto.house.HouseChatDTO;
import com.dangjia.acg.dto.house.MyHouseFlowDTO;
import com.dangjia.acg.dto.other.WorkDepositDTO;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.delivery.IOrderMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.engineer.DjSkillCertificationMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.matter.IMasterTechnologyMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.member.IMemberCityMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.reason.ReasonMatchMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.sale.DjRoyaltyMatchMapper;
import com.dangjia.acg.mapper.worker.IInsuranceMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.mapper.worker.IWorkerChoiceCaseMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.engineer.DjSkillCertification;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberCity;
import com.dangjia.acg.modle.reason.ReasonMatchSurface;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.sale.royalty.DjRoyaltyMatch;
import com.dangjia.acg.modle.worker.Insurance;
import com.dangjia.acg.modle.worker.WorkIntegral;
import com.dangjia.acg.modle.worker.WorkerChoiceCase;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.configRule.ConfigRuleService;
import com.dangjia.acg.service.configRule.ConfigRuleUtilService;
import com.dangjia.acg.service.deliver.RepairMendOrderService;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.worker.EvaluateService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 18:58
 */
@Service
public class HouseWorkerService {

    protected static final Logger logger = LoggerFactory.getLogger(HouseWorkerService.class);
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;//小区
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private WorkerGoodsAPI workerGoodsAPI;
    @Autowired
    private IWorkDepositMapper workDepositMapper;
    @Autowired
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ITechnologyRecordMapper technologyRecordMapper;
    @Autowired
    private HouseFlowService houseFlowService;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private HouseFlowApplyService houseFlowApplyService;
    @Autowired
    private HouseService houseService;
    @Autowired
    private IChangeOrderMapper changeOrderMapper;
    @Autowired
    private HouseFlowScheduleService houseFlowScheduleService;
    @Value("${spring.profiles.active}")
    private String active;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IComplainMapper complainMapper;
    @Autowired
    private EvaluateService evaluateService;

    @Autowired
    private IWorkIntegralMapper workIntegralMapper;
    @Autowired
    private ConfigRuleUtilService configRuleUtilService;
    @Autowired
    private IInsuranceMapper insuranceMapper;
    @Autowired
    private IMemberCityMapper memberCityMapper;
    @Autowired
    private DjRoyaltyMatchMapper djRoyaltyMatchMapper;


    @Autowired
    private DjSkillCertificationMapper djSkillCertificationMapper;

    @Autowired
    private RepairMendOrderService repairMendOrderService;
    @Autowired
    private ReasonMatchMapper reasonMatchMapper;
    @Autowired
    private TaskStackService taskStackService;
    @Autowired
    private IMasterTechnologyMapper iMasterTechnologyMapper;

    @Autowired
    private IOrderMapper orderMapper;

    @Autowired
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper;

    @Autowired
    private IWorkerChoiceCaseMapper iWorkerChoiceCaseMapper;

    /**
     * 根据工人id查询所有房子任务
     */
    public ServerResponse queryWorkerHouse(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member operator = (Member) object;
        List list = houseWorkerMapper.queryWorkerHouse(operator.getId());
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", list);
    }

    /**
     * 换人
     */
    public ServerResponse setChangeWorker(String userToken, String houseWorkerId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        HouseWorker houseWorker = houseWorkerMapper.selectByPrimaryKey(houseWorkerId);
        if (houseWorker == null) {
            return ServerResponse.createByErrorMessage("该工匠订单不存在");
        }
        houseWorker.setWorkType(2);//被业主换
        HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
        if (houseFlow == null) {
            return ServerResponse.createByErrorMessage("该工序不存在");
        }
        String workerId = houseWorker.getWorkerId();
        houseFlow.setWorkerId("");
        houseFlow.setWorkType(2);
        houseFlow.setReleaseTime(new Date());//重新发布
        houseFlow.setRefuseNumber(houseFlow.getRefuseNumber() + 1);
        if (!CommonUtil.isEmpty(workerId)) {
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            if (house != null) {
                //提醒原工匠
                configMessageService.addConfigMessage(null, AppType.GONGJIANG, workerId, "0", "业主换人提醒",
                        String.format(DjConstants.PushMessage.STEWARD_REPLACE, workerType.getName(),house.getHouseName()), "5");

                //提醒业主
                configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "换人提醒",
                        String.format(DjConstants.PushMessage.CRAFTSMAN_NEW_REPLACE, workerType.getName(),workerType.getName()), "5");


            }
        }
        houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    public ServerResponse getHouseWorker(String userToken, String houseFlowId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        if (houseFlow == null) {
            return ServerResponse.createByErrorMessage("该工序不存在");
        }
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
        HouseWorker houseWorker = null;
        if (houseFlow.getWorkType() == 3) {//待支付
            houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 1);
        } else if (houseFlow.getWorkType() == 4) {//已支付
            houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 6);
        }
        Map<String, Object> mapData = new HashMap<>();
        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        if (houseWorker == null) {
            mapData.put("houseWorker", null);
        } else {
            Member member1 = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
            member1.setPassword(null);
            member1.initPath(address);
            Map<String, Object> map = new HashMap<>();
            map.put("id", member1.getId());
            map.put("targetId", member1.getId());
            map.put("targetAppKey", NIMPost.APPKEY);
            map.put("nickName", member1.getNickName());
            map.put("name", member1.getName());
            map.put("mobile", member1.getMobile());
            map.put("head", member1.getHead());
            map.put("workerTypeId", member1.getWorkerTypeId());
            map.put("workerName", workerType.getName());
            map.put("houseFlowId", houseFlow.getId());
            map.put("houseWorkerId", houseWorker.getId());
            if (workerType.getType() == 1 || workerType.getType() == 2) {//设计精算不展示换人按钮
                map.put("isSubstitution", 2);//0:审核中 1：申请换人 2：不显示
            } else {
                Example example = new Example(Complain.class);
                example.createCriteria().andEqualTo(Complain.MEMBER_ID, houseFlow.getWorkerId())
                        .andEqualTo(Complain.HOUSE_ID, houseFlow.getHouseId())
                        .andEqualTo(Complain.STATUS, 0)
                        .andEqualTo(Complain.COMPLAIN_TYPE, 6);
                List<Complain> complains = complainMapper.selectByExample(example);
                if (houseWorker.getWorkType() == 6) {
                    map.put("isSubstitution", complains.size() > 0 ? 0 : 1);//判断换人申请状态
                } else {
                    map.put("isSubstitution", 2);
                }
            }
            mapData.put("houseWorker", map);
        }
        Example example = new Example(HouseWorker.class);
        example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseFlow.getHouseId())
                .andEqualTo(HouseWorker.WORKER_TYPE_ID, houseFlow.getWorkerTypeId())
                .andNotEqualTo(HouseWorker.WORK_TYPE, 6)
                .andNotEqualTo(HouseWorker.WORK_TYPE, 1);
        example.orderBy(HouseWorker.MODIFY_DATE).desc();
        List<HouseWorker> houseWorkers = houseWorkerMapper.selectByExample(example);
        List<Map<String, Object>> historyWorkerList = new ArrayList<>();
        if (houseWorkers.size() > 0) {
            for (HouseWorker worker : houseWorkers) {
                Member member1 = memberMapper.selectByPrimaryKey(worker.getWorkerId());
                member1.setPassword(null);
                member1.initPath(address);
                Map<String, Object> map = new HashMap<>();
                map.put("id", member1.getId());
                map.put("targetId", member1.getId());
                map.put("targetAppKey", NIMPost.APPKEY);
                map.put("nickName", member1.getNickName());
                map.put("name", member1.getName());
                map.put("mobile", member1.getMobile());
                map.put("head", member1.getHead());
                map.put("workerTypeId", member1.getWorkerTypeId());
                map.put("workerName", workerType.getName());
                map.put("houseFlowId", houseFlow.getId());
                map.put("houseWorkerId", worker.getId());
                map.put("isSubstitution", 2);
                map.put("houseId", worker.getId());
                historyWorkerList.add(map);
            }
        }
        mapData.put("historyWorkerList", historyWorkerList);
        return ServerResponse.createBySuccess("查询成功", mapData);
    }


    /**
     * 新获取工匠详情
     * @param userToken
     * @param orderId
     * @return
     */
    public ServerResponse getWorkerInFo(String userToken, String workerId,String orderId) {
        try{
            Order order=orderMapper.selectByPrimaryKey(orderId);
            if(order==null){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Map<String, Object> mapData = new HashMap<>();
            Member member1 = memberMapper.selectByPrimaryKey(workerId);
            member1.setPassword(null);
            member1.initPath(address);
            Map<String, Object> map = new HashMap<>();
            map.put("id", member1.getId());
            map.put("memberId", member1.getId());
            map.put("targetId", member1.getId());
            map.put("targetAppKey", NIMPost.APPKEY);
            map.put("nickName", member1.getNickName());
            map.put("name", member1.getName());
            map.put("mobile", member1.getMobile());
            map.put("head", member1.getHead());
            map.put("workerTypeId", member1.getWorkerTypeId());
            Example example=new Example(HouseFlow.class);
            example.createCriteria().andEqualTo(HouseFlow.WORKER_ID,workerId)
                    .andEqualTo(HouseFlow.HOUSE_ID,order.getHouseId());
            HouseFlow houseFlow = houseFlowMapper.selectOneByExample(example);
            if (houseFlow != null) {
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
                HouseWorker houseWorker = null;
                if (houseFlow.getWorkType() == 3) {//待支付
                    houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 1);
                } else if (houseFlow.getWorkType() == 4) {//已支付
                    houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 6);
                }
                map.put("workerName", workerType.getName());
                map.put("houseFlowId", houseFlow.getId());
                map.put("houseId", houseFlow.getHouseId());
                map.put("houseWorkerId", houseWorker.getId());
                if (workerType.getType() == 1 || workerType.getType() == 2) {//设计精算不展示换人按钮
                    map.put("isSubstitution", 2);//0:审核中 1：申请换人 2：不显示
                } else {
                    mapData.put("isChangeButton",1);//是否显示更换按钮，1是，0否
                    example = new Example(Complain.class);
                    example.createCriteria().andEqualTo(Complain.MEMBER_ID, houseFlow.getWorkerId())
                            .andEqualTo(Complain.HOUSE_ID, houseFlow.getHouseId())
                            .andEqualTo(Complain.STATUS, 0)
                            .andEqualTo(Complain.COMPLAIN_TYPE, 6);
                    example.orderBy(Complain.CREATE_DATE).desc();
                    List<Complain> complains = complainMapper.selectByExample(example);
                    if(complains !=null && complains.size()> 0){
                        map.put("complainId",complains.get(0).getId());
                        if(complains.get(0).getStatus()==0){
                            mapData.put("isChangeButton",0);//是否显示更换按钮，1是，0否
                        }
                    }
                    if (houseWorker.getWorkType() == 6) {
                        map.put("isSubstitution", complains.size() > 0 ? 0 : 1);//判断换人申请状态
                    } else {
                        map.put("isSubstitution", 2);
                    }
                }

            }else{
                mapData.put("isChangeButton",0);//是否显示更换按钮，1是，0否
            }
            mapData.put("houseWorker", map);

            //查询保险徽章
            example = new Example(Insurance.class);
            example.createCriteria().andEqualTo(Insurance.WORKER_ID, houseFlow.getWorkerId())
                    .andEqualTo(Insurance.DATA_STATUS, 0);
            example.orderBy(Insurance.CREATE_DATE).desc();
            List<Insurance> insurance = insuranceMapper.selectByExample(example);
            List<Map<String, Object>> list = new ArrayList<>();
             map = new HashMap<>();
            if(insurance != null && insurance.size() >0){
                if (new Date().getTime() >insurance.get(0).getEndDate().getTime()) {
                    map.put("name","保险详情");
                    map.put("code","H001");
                    map.put("head", address + "iconWork/shqd_icon_bx@3x.png");
                    map.put("id",insurance.get(0).getId());
                    list.add(map);
                }
            }
            //查询技能徽章
            example = new Example(DjSkillCertification.class);
            example.createCriteria().andEqualTo(DjSkillCertification.SKILL_CERTIFICATION_ID, houseFlow.getWorkerId())
                    .andEqualTo(DjSkillCertification.DATA_STATUS, 0);
            List<DjSkillCertification> djSkillCertifications = djSkillCertificationMapper.selectByExample(example);
            if(djSkillCertifications != null && djSkillCertifications.size() >0){
                map = new HashMap<>();
                map.put("name","技能培训");
                map.put("head", address + "iconWork/shqd_icon_jn@3x.png");
                map.put("id",houseFlow.getWorkerId());
                map.put("code","H002");
                list.add(map);
            }
            //他的徽章
            mapData.put("lists",list);

            //查询我的精选案列
            List<Map<String, Object>> workerCasesList = new ArrayList<>();
            Map<String, Object> workerMap = new HashMap<>();
            example = new Example(WorkerChoiceCase.class);
            example.createCriteria().andEqualTo(WorkerChoiceCase.WORKER_ID,houseFlow.getWorkerId() )
                    .andEqualTo(WorkerChoiceCase.DATA_STATUS, 0);
            example.orderBy(WorkerChoiceCase.CREATE_DATE).desc();
            List<WorkerChoiceCase> workerChoiceCases = iWorkerChoiceCaseMapper.selectByExample(example);
            if(workerChoiceCases != null && workerChoiceCases.size() > 0){
                workerMap.put("imageUrl", StringTool.getImage(workerChoiceCases.get(0).getImage(),address));
                workerMap.put("remark",workerChoiceCases.get(0).getRemark());
            }
            workerCasesList.add(workerMap);
            mapData.put("workerCasesList",workerCasesList);

            return ServerResponse.createBySuccess("查询成功", mapData);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }


    /**
     * 查询申换工匠详情
     * @param houseFlowId
     * @return
     */
    public ServerResponse getWorkerComplainInFo(String userToken,
                                        String houseFlowId) {

        //0:审核中 1：申请换人
        ComplainInfoDTO complainInfoDTO = new ComplainInfoDTO();
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        Example example =new Example(Complain.class);
        example.createCriteria()
                .andIn(Complain.COMPLAIN_TYPE,Arrays.asList(3,6))
                .andEqualTo(Complain.HOUSE_ID,houseFlow.getHouseId())
                .andEqualTo(Complain.MEMBER_ID,houseFlow.getWorkerId())
                .andNotEqualTo(Complain.STATUS,2);
        List<Complain> complains = complainMapper.selectByExample(example);
        if(complains != null&&complains.size()>0){
            Complain complain =complains.get(0);
            if(complain.getStatus()==0) {
                List<String> list = Arrays.asList(complain.getChangeReason().split("@"));
                complainInfoDTO.setChangeList(list);
            }else{
                //查询更换原因
                example = new Example(ReasonMatchSurface.class);
                example.orderBy(ReasonMatchSurface.CREATE_DATE);
                List<ReasonMatchSurface> reasonMatchSurface =  reasonMatchMapper.selectByExample(example);
                List<String> list = new ArrayList<>();
                reasonMatchSurface.forEach(a ->{
                    list.add(a.getRemark());
                });
                complainInfoDTO.setChangeList(list);
            }
            complainInfoDTO.setStatus(complain.getStatus());
            complainInfoDTO.setImageList(Arrays.asList(complain.getImage()));
            complainInfoDTO.setImageURLList(getImage(complain.getImage()));
            complainInfoDTO.setRejectReason(complain.getRejectReason());
        }else{
            //查询更换原因
            example = new Example(ReasonMatchSurface.class);
            example.orderBy(ReasonMatchSurface.CREATE_DATE);
            List<ReasonMatchSurface> reasonMatchSurface =  reasonMatchMapper.selectByExample(example);
            List<String> list = new ArrayList<>();
            reasonMatchSurface.forEach(a ->{
                list.add(a.getRemark());
            });
            complainInfoDTO.setStatus(-1);
            complainInfoDTO.setChangeList(list);
        }
        return ServerResponse.createBySuccess("查询成功", complainInfoDTO);
    }

    private List<String> getImage(String image){
        List<String> strList = new ArrayList<>();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<String> result = Arrays.asList(image.split(","));
        for (int i = 0; i < result.size(); i++) {
            String str = imageAddress + result.get(i);
            strList.add(str);
        }
        return strList;
    }


    /**
     * 抢单
     */
    public ServerResponse setWorkerGrab(String userToken, String cityId, String houseFlowId,Integer type) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;

            String[] amount;
            if(worker.getWorkerType()==3){
                amount= configRuleUtilService.getAbandonedCount(1);
            }else{
                amount= configRuleUtilService.getAbandonedCount(0);
            }

            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, worker.getId()).andEqualTo(HouseWorker.WORK_TYPE, 7)
                    .andGreaterThan(HouseWorker.CREATE_DATE, DateUtil.delDateDays(new Date(),Integer.parseInt(amount[0])));
            example.orderBy(HouseWorker.CREATE_DATE).desc();
            List<HouseWorker> houseWorkers = houseWorkerMapper.selectByExample(example);//查出自己的所有已抢单
            if(houseWorkers.size()>0){
                return ServerResponse.createByErrorMessage("因存在放弃，在"+DateUtil.getDateString(DateUtil.addDateDays(houseWorkers.get(0).getCreateDate(),Integer.parseInt(amount[0])).getTime())+"前您无法接单！");
            }
            if(type==1){
                Order order =  orderMapper.selectByPrimaryKey(houseFlowId);
                example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo(HouseWorker.WORK_TYPE,1).andEqualTo(HouseWorker.TYPE,type).andEqualTo(HouseWorker.BUSINESS_ID,order.getId());
                Integer ordernum= houseWorkerMapper.selectCountByExample(example);
                if (ordernum>0) {
                    return ServerResponse.createByErrorMessage("该订单已被抢");
                }
                houseWorkerMapper.doModifyAllByWorkerId(worker.getId());//将所有houseWorker的选中状态IsSelect改为0未选中
                HouseWorker houseWorker = new HouseWorker();
                houseWorker.setWorkerId(worker.getId());
                houseWorker.setOrderId(order.getId());
                houseWorker.setWorkerTypeId(worker.getWorkerTypeId());
                houseWorker.setWorkerType(worker.getWorkerType());
                houseWorker.setWorkType(1);//已抢单
                houseWorker.setIsSelect(1);
                houseWorker.setPrice(order.getTotalAmount());
                houseWorker.setType(type);
                houseWorker.setBusinessId(order.getId());
                houseWorkerMapper.insert(houseWorker);

                order.setWorkerId(worker.getId());
                order.setWorkerTypeId(order.getWorkerTypeId());
                orderMapper.updateByPrimaryKeySelective(order);

                String text = "业主您好,我是体验师" +  worker.getName() + "，已成功抢单";
                HouseChatDTO h = new HouseChatDTO();
                h.setTargetId(order.getMemberId());
                h.setTargetAppKey(NIMPost.APPKEY);
                h.setText(text);
                return ServerResponse.createBySuccess("抢单成功", h);
            }else if(type==2){
                DjMaintenanceRecord record=djMaintenanceRecordMapper.selectByPrimaryKey(houseFlowId);
                example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo(HouseWorker.WORK_TYPE,1).andEqualTo(HouseWorker.TYPE,type).andEqualTo(HouseWorker.BUSINESS_ID,record.getId());
                Integer ordernum= houseWorkerMapper.selectCountByExample(example);
                if (ordernum>0) {
                    return ServerResponse.createByErrorMessage("该订单已被抢");
                }
                houseWorkerMapper.doModifyAllByWorkerId(worker.getId());//将所有houseWorker的选中状态IsSelect改为0未选中
                HouseWorker houseWorker = new HouseWorker();
                houseWorker.setWorkerId(worker.getId());
                houseWorker.setWorkerTypeId(worker.getWorkerTypeId());
                houseWorker.setWorkerType(worker.getWorkerType());
                houseWorker.setWorkType(1);//已抢单
                houseWorker.setHouseId(record.getHouseId());
                houseWorker.setIsSelect(1);
             //   houseWorker.setPrice(new BigDecimal(record.getSincePurchaseAmount()));
                houseWorker.setType(type);
                houseWorker.setBusinessId(record.getId());
                houseWorkerMapper.insert(houseWorker);

                if(worker.getWorkerType()==3){
                    record.setStewardId(worker.getId());
                   // record.setStewardState(1);
                    record.setStewardOrderTime(new Date());
                    djMaintenanceRecordMapper.updateByPrimaryKeySelective(record);
                }else{
                    record.setWorkerMemberId(worker.getId());
                    record.setWorkerTypeId(worker.getWorkerTypeId());
                    record.setWorkerCreateDate(new Date());
                    djMaintenanceRecordMapper.updateByPrimaryKeySelective(record);
                }
                String text = "业主您好,我是"+ workerTypeMapper.getName(worker.getWorkerType())+"维保" +  worker.getName() + "，已成功抢单";
                HouseChatDTO h = new HouseChatDTO();
                h.setTargetId(record.getMemberId());
                h.setTargetAppKey(NIMPost.APPKEY);
                h.setText(text);
                return ServerResponse.createBySuccess("抢单成功");
            }else {
                ServerResponse serverResponse = houseFlowService.setGrabVerification(userToken, cityId, houseFlowId);
                if (!serverResponse.isSuccess())
                    return serverResponse;
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
                if (houseFlow.getWorkType() == 3) {
                    return ServerResponse.createByErrorMessage("该订单已被抢");
                }
                House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
                houseFlow.setGrabNumber(houseFlow.getGrabNumber() + 1);
                if(worker.getWorkerType()==3){
                    houseFlow.setWorkType(4);//等待支付
                    houseFlow.setWorkSteta(3);//待交底
                }else {
                    houseFlow.setWorkType(3);//等待支付
                }
                houseFlow.setModifyDate(new Date());
                houseFlow.setWorkerId(worker.getId());
                grabSheet(worker, house, houseFlow);
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                houseWorkerMapper.doModifyAllByWorkerId(worker.getId());//将所有houseWorker的选中状态IsSelect改为0未选中
                HouseWorker houseWorker = new HouseWorker();
                houseWorker.setHouseId(house.getId());
                houseWorker.setWorkerId(worker.getId());
                houseWorker.setWorkerTypeId(houseFlow.getWorkerTypeId());
                houseWorker.setWorkerType(houseFlow.getWorkerType());
                if(houseFlow.getWorkType()==4){
                    houseWorker.setWorkType(6);//大管家自动采纳
                }else {
                    houseWorker.setWorkType(1);//已抢单
                }
                houseWorker.setIsSelect(1);
                houseWorker.setPrice(houseFlow.getWorkPrice());
                houseWorker.setType(0);
                houseWorker.setBusinessId(houseFlow.getId());
                houseWorkerMapper.insert(houseWorker);
                /*
                 * 工匠订单
                 */
                HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
                if (hwo != null) {
                    hwo.setWorkerId(houseWorker.getWorkerId());
                    houseWorkerOrderMapper.updateByPrimaryKey(hwo);
                }
                example = new Example(MemberCity.class);
                example.createCriteria()
                        .andEqualTo(MemberCity.MEMBER_ID, worker.getId())
                        .andEqualTo(MemberCity.CITY_ID, cityId);
                List list = memberCityMapper.selectByExample(example);
                if (list.size() == 0) {
                    MemberCity userCity = new MemberCity();
                    userCity.setMemberId(worker.getId());
                    userCity.setCityId(cityId);
                    userCity.setCityName(house.getCityName());
                    memberCityMapper.insert(userCity);
                }
                example = new Example(Insurance.class);
                example.createCriteria().andEqualTo(Insurance.WORKER_ID, houseWorker.getWorkerId()).andIsNotNull(Insurance.END_DATE);
                example.orderBy(Insurance.END_DATE).desc();
                List<Insurance> insurances = insuranceMapper.selectByExample(example);

                //保险服务剩余天数小于等于60天
                Integer daynum = 0;
                if (insurances.size() > 0) {
                    daynum = DateUtil.daysofTwo(new Date(), insurances.get(0).getEndDate());
                }
                //工人未购买保险
                if (houseFlow.getWorkerType() > 2 && ((insurances.size() == 0) || (insurances.size() > 0 & daynum <= 60))) {
                    //满足则不提醒业主
                } else {
//            3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工
                    //通知业主设计师抢单成功
                    if (worker.getWorkerType() == 1) {//设计师
                        configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "设计师抢单提醒",
                                String.format(DjConstants.PushMessage.DESIGNER_GRABS_THE_BILL, house.getHouseName()), "");
                    }
                    //通知业主精算师抢单成功
                    if (worker.getWorkerType() == 2) {//精算师
                        configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "精算师抢单提醒",
                                String.format(DjConstants.PushMessage.BUDGET_GRABS_THE_BILL, house.getHouseName()), "");
                    }
                    //通知业主大管家抢单成功
                    if (worker.getWorkerType() == 3) {//大管家
                        configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "大管家抢单提醒",
                                String.format(DjConstants.PushMessage.STEWARD_RUSH_TO_PURCHASE, house.getHouseName()), "");
                    }
                    if (worker.getWorkerType() > 3) {//其他工匠
                        configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "工匠抢单提醒",
                                String.format(DjConstants.PushMessage.CRAFTSMAN_RUSH_TO_PURCHASE, house.getHouseName()), "4");
                        //通知大管家已有工匠抢单
                        //通知大管家抢单
                        HouseFlow houseFlowDgj = houseFlowMapper.getHouseFlowByHidAndWty(houseFlow.getHouseId(), 3);
                        configMessageService.addConfigMessage(null, AppType.GONGJIANG, houseFlowDgj.getWorkerId(), "0", "工匠抢单提醒",
                                String.format(DjConstants.PushMessage.STEWARD_TWO_RUSH_TO_PURCHASE, house.getHouseName()), "4");
                    }
                }
                example = new Example(WorkerType.class);
                example.createCriteria().andEqualTo(WorkerType.TYPE, worker.getWorkerType());
                List<WorkerType> workerType = workerTypeMapper.selectByExample(example);
                String text = "业主您好,我是" + workerType.get(0).getName() + worker.getName() + "，已成功抢单";
                HouseChatDTO h = new HouseChatDTO();
                h.setTargetId(house.getMemberId());
                h.setTargetAppKey(NIMPost.APPKEY);
                h.setText(text);
                return ServerResponse.createBySuccess("抢单成功", h);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("抢单失败");
        }
    }

    /**
     * 设置设计和精算的一些状态
     *
     * @param worker      工匠
     * @param house       房子
     * @param houseFlow   工序
     */
    public void grabSheet(Member worker, House house, HouseFlow houseFlow) {
        if (houseFlow.getWorkerType() == 1) {//设计师
            house.setDesignerOk(1);//有设计抢单待业主支付
            houseFlow.setWorkType(3);
            houseMapper.updateByPrimaryKeySelective(house);
        } else if (houseFlow.getWorkerType() == 2) {
            house.setBudgetOk(1);//有精算抢单待业主支付
            houseMapper.updateByPrimaryKeySelective(house);
        }else if (houseFlow.getWorkerType() == 3) {
            //在这里算出大管家每次巡查拿的钱 和 每次验收拿的钱 记录到大管家的 houseflow里 houseflow,新增两个字段.
            WorkDepositDTO workDepositDTO=configRuleUtilService.getSupervisorTakeMoney(house.getId(),houseFlow.getWorkerTypeId(),worker.getEvaluationScore());
            JSONObject patrolCfg=configRuleUtilService.getAllTowValue(ConfigRuleService.MK022);
            List<HouseFlow> houseFlowList = houseFlowMapper.getForCheckMoney(house.getId());  //拿到这个大管家工钱
            int check =  (int)((houseFlow.getWorkPrice().doubleValue()*(workDepositDTO.getPatrol().doubleValue()/100))/patrolCfg.getDouble("patrolMoney"));//累计大管家总巡查次数
            int time = 0;//累计管家总阶段验收和完工验收次数
            for (HouseFlow hf : houseFlowList) {
                //累计总验收
                if (hf.getWorkerType() == 4) {
                    time++;
                } else {
                    time += 2;
                }
            }
            //算管家每次验收钱
            BigDecimal checkMoney = new BigDecimal(0);
            if (time > 0) {
                checkMoney=workDepositDTO.getTested().divide(new BigDecimal(100));
                checkMoney=checkMoney.multiply(houseFlow.getWorkPrice());
                checkMoney=checkMoney.divide(new BigDecimal(time));
            }
            //保存到大管家的houseFlow
            houseFlow.setPatrol(check);
            houseFlow.setPatrolMoney(patrolCfg.getBigDecimal("patrolMoney"));
            houseFlow.setCheckMoney(checkMoney);
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);

        }
    }

    /**
     * 获取申请单明细
     */
    public ServerResponse getHouseFlowApply(String houseFlowApplyId) {
        HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
        return ServerResponse.createBySuccess(houseFlowApply);
    }

    /**
     * 是否超过期限没有施工，则需要重新培训
     */
    public ServerResponse getHouseDetectionTimeout(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        Example example =new Example(HouseFlowApply.class);
        example.createCriteria().andEqualTo(HouseFlowApply.WORKER_ID,worker.getId());
        example.orderBy(HouseFlowApply.CREATE_DATE).desc();
        PageHelper.startPage(1, 1);
        List<HouseFlowApply> houseFlowApplys = houseFlowApplyMapper.selectByExample(example);
        if(houseFlowApplys.size()>0){
            HouseFlowApply houseFlowApply=houseFlowApplys.get(0);
            Integer day = configRuleUtilService.getGrabLimitDay();
            Date maxtime=DateUtil.addDateDays(houseFlowApply.getCreateDate(),day);
            if(maxtime.getTime()<new Date().getTime()){
                ServerResponse.createByErrorMessage("已超过"+day+"天未接单");
            }
        }
        return ServerResponse.createBySuccess("OK");
    }



    /**
     * 提交审核、停工
     * applyType   0每日完工申请，1阶段完工申请，2整体完工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
     * returnableMaterial是否有可退材料（1是，0否）
     * materialProductArr 可退材料列表
     */
    public ServerResponse setHouseFlowApply(String userToken, Integer applyType, String houseFlowId,
                                            String applyDec, String imageList,
                                            String latitude, String longitude,String returnableMaterial,
                                            String materialProductArr) {
        if (CommonUtil.isEmpty(applyType)) {
            return ServerResponse.createByErrorMessage("请选择提交审核类别");
        }
        if (CommonUtil.isEmpty(applyDec)) {
            applyDec = "";
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = memberMapper.selectByPrimaryKey(((Member) object).getId());
        HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        if (hf == null) {
            return ServerResponse.createByErrorMessage("未找到该工序");
        }
        House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
        if (house == null) {
            return ServerResponse.createByErrorMessage("未找到该房产");
        }
        switch (applyType) {
            case 0:
                return setCompletedToday(worker, hf, house, applyDec, imageList);
            case 1:
                return setPhaseCompletion(worker, hf, house, imageList,returnableMaterial,materialProductArr);
            case 2:
                return setWholeCompletion(worker, hf, house, imageList);
            case 4:
//                ServerResponse serverResponse = evaluateService.getUserToHouseDistance(latitude, longitude, house.getVillageId());
//                if (!serverResponse.isSuccess())
//                    return serverResponse;
                return setStartDaily(worker, hf, house, imageList);
            case 5:
            case 6:
            case 7:
                return setPatrol(worker, hf, house, applyType, applyDec, imageList);
        }
        return ServerResponse.createByErrorMessage("未找审核类型");
    }

    /**
     * 今日完工
     */
    private ServerResponse setCompletedToday(Member worker, HouseFlow hf, House house, String applyDec, String imageList) {
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
        if (hf.getPause() == 1) {
            return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）已暂停施工,请勿提交申请！");
        }
        if (house.getPause() != null && house.getPause() == 1) {
            return ServerResponse.createByErrorMessage("该房子已暂停施工,请勿提交申请！");
        }
        List<HouseFlowApply> houseFlowApplyList = getLeave(hf);
        if (houseFlowApplyList.size() > 0) {
            HouseFlowApply houseFlowApply = houseFlowApplyList.get(0);
            if (houseFlowApply.getStartDate().before(new Date()) && houseFlowApply.getEndDate().after(new Date())) {
                return ServerResponse.createByErrorMessage("工序(" + workerType.getName() + ")处于停工期间！");
            }
        }
        houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 4, worker.getId(), new Date());
        if (houseFlowApplyList.size() > 0) {
            if (active != null && (active.equals("pre"))) {
                HouseFlowApply houseFlowApply = houseFlowApplyList.get(0);
                if (new Date().getTime() < DateUtil.addDateHours(houseFlowApply.getCreateDate(), 3).getTime()) {
                    return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）开工后3小时才能申请完工！");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）未开工，无法申请完工！");
        }
        if (active != null && active.equals("pre")) {
            //包括所有申请 和 巡查
            houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 0, worker.getId(), new Date());
            if (houseFlowApplyList.size() > 0) {
                return ServerResponse.createByErrorMessage("您今日已提交过此申请,请勿重复提交！");
            }
        }
        /*待审核申请*/
        List<HouseFlowApply> hfaList = houseFlowApplyMapper.checkPendingApply(hf.getId(), worker.getId());
        if (hfaList.size() > 0) {
            return ServerResponse.createByErrorMessage("您有待审核的申请,请联系管家业主审核后再提交");
        }
        WorkDepositDTO workDeposit = configRuleUtilService.getWerkerTakeMoney(house.getId(),worker.getWorkerTypeId(),worker.getEvaluationScore());//结算比例表
        HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hf.getHouseId(), worker.getWorkerTypeId());
        HouseFlow supervisorHF = houseFlowMapper.getHouseFlowByHidAndWty(hf.getHouseId(), 3);//大管家的hf
        HouseFlowApply hfa = getHouseFlowApply(hf, 0, supervisorHF);
        //********************发申请，计算可得钱和积分等*****************//
        BigDecimal workPrice = new BigDecimal(0);
        BigDecimal haveMoney = new BigDecimal(0);
        if (hwo != null) {
            workPrice = hwo.getWorkPrice();//工钱
            haveMoney = hwo.getHaveMoney();
        }
        BigDecimal limitpay = workPrice.multiply(workDeposit.getLimitPay());//每日完工得到钱的上限
        if (workDeposit.getEverydayPay().compareTo(limitpay.subtract(haveMoney)) <= 0) {//每日上限减已获 大于等于 100
            hfa.setApplyMoney(workDeposit.getEverydayPay());
        } else {
            hfa.setApplyMoney(new BigDecimal(0));
        }
        hfa.setOtherMoney((workPrice).subtract(haveMoney).subtract(hfa.getApplyMoney()));
//        hfa.setApplyDec("我是" + workerType.getName() + ",我今天已经完工了");//描述
        String dec = setHouseFlowApplyImage(hfa, house, imageList);
        if (!CommonUtil.isEmpty(dec)) {
            applyDec = dec;
        }
        hfa.setApplyDec("尊敬的业主，您好！当家工匠【" + worker.getName() + "】为您新家施工，今日实际施工为:<br/>" +
                applyDec +
                "<br/>现向您发送完成情况，请您查收。");//描述
        hfa.setIsReadType(0);
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        //每日完工
        houseFlowApplyService.checkWorker(hfa.getId(), false);
        return ServerResponse.createBySuccessMessage("工序（" + workerType.getName() + "）每日完工申请成功");
    }

    /**
     * 阶段完工
     */
    private ServerResponse setPhaseCompletion(Member worker, HouseFlow hf, House house, String imageList,String returnableMaterial,
                                              String materialProductArr) {
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
        if (hf.getPause() == 1) {
            return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）已暂停施工,请勿提交申请！");
        }
        if (house.getPause() != null && house.getPause() == 1) {
            return ServerResponse.createByErrorMessage("该房子已暂停施工,请勿提交申请！");
        }
        List<HouseFlowApply> houseFlowApplyList = getLeave(hf);
        if (houseFlowApplyList.size() > 0) {
            HouseFlowApply houseFlowApply = houseFlowApplyList.get(0);
            if (houseFlowApply.getStartDate().before(new Date()) && houseFlowApply.getEndDate().after(new Date())) {
                return ServerResponse.createByErrorMessage("工序(" + workerType.getName() + ")处于停工期间!");
            }
        }
        houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 1, worker.getId(), new Date());
        if (houseFlowApplyList.size() > 0) {
            return ServerResponse.createByErrorMessage("您今日已提交过此申请,请勿重复提交！");
        }
        List<HouseFlowApply> hfaList = houseFlowApplyMapper.checkPendingApply(hf.getId(), worker.getId());
        if (hfaList.size() > 0) {
            return ServerResponse.createByErrorMessage("您有待审核的申请,请联系管家业主审核后再提交");
        }
        WorkDepositDTO workDeposit = configRuleUtilService.getWerkerTakeMoney(house.getId(),worker.getWorkerTypeId(),worker.getEvaluationScore());//结算比例表
        HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hf.getHouseId(), hf.getWorkerTypeId());
        HouseFlow supervisorHF = houseFlowMapper.getHouseFlowByHidAndWty(hf.getHouseId(), 3);//大管家的hf
        HouseFlowApply hfa = getHouseFlowApply(hf, 1, supervisorHF);
        //********************发申请，计算可得钱和积分等*****************//
        BigDecimal workPrice = new BigDecimal(0);
        BigDecimal haveMoney = new BigDecimal(0);
        if (hwo != null) {
            workPrice = hwo.getWorkPrice();//工钱
            haveMoney = hwo.getHaveMoney();
        }
        //算阶段完工所有的钱
        BigDecimal stage = workPrice.multiply((workDeposit.getStagePay().add(workDeposit.getLimitPay()))).subtract(haveMoney);
        if (stage.compareTo(new BigDecimal(0)) > 0) {
            hfa.setApplyMoney(stage); //stagePay阶段完工比例百分比
        } else {
            hfa.setApplyMoney(new BigDecimal(0));
        }
        hfa.setOtherMoney(workPrice.subtract(haveMoney).subtract(hfa.getApplyMoney()));
//        hfa.setApplyDec("我是" + workerType.getName() + ",我已申请了阶段完工");//描述
        hfa.setApplyDec("尊敬的业主，您好！<br/>" +
                "当家工匠【" + worker.getName() + "】为您新家施工，工地【" + workerType.getName() + "】已阶段完工，剩余部分待其他工种完成后继续进行，严格按照平台施工验收标准进行施工，请您查收。<br/>");//描述
        hfa.setSupervisorMoney(supervisorHF.getCheckMoney());//管家得相应验收收入
        //增加倒计时系统自动审核时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 3);//管家倒计时
        hfa.setStartDate(calendar.getTime());
        // 阶段完工,管家审核通过工匠完工申请 @link checkOk()
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        configMessageService.addConfigMessage(null, AppType.GONGJIANG, supervisorHF.getWorkerId(), "0", "阶段完工申请",
                String.format(DjConstants.PushMessage.STEWARD_APPLY_FINISHED, house.getHouseName(), workerType.getName()), "5");
        setHouseFlowApplyImage(hfa, house, imageList);
        /**
         * 2019/12/26 FZH 增加可退材料到业主审核
         */
        if(returnableMaterial!=null&&"1".equals(returnableMaterial)){
            JSONArray jsonArray=JSONObject.parseArray(materialProductArr);
            if(jsonArray!=null&&jsonArray.size()>0){
               String mendOrderId = repairMendOrderService.workerApplyReturnMaterial( worker,house.getCityId(), house.getId(), materialProductArr);
                //生成工匠退材料任务
                taskStackService.insertTaskStackInfo(house.getId(),house.getMemberId(),workerType.getName()+"发起退材料", workerType.getImage(),11,mendOrderId);

            }
        }
        return ServerResponse.createBySuccessMessage("工序（" + workerType.getName() + "）阶段完工申请成功");

    }

    /**
     * 整体完工
     */
    private ServerResponse setWholeCompletion(Member worker, HouseFlow hf, House house, String imageList) {
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
        if (hf.getPause() == 1) {
            return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）已暂停施工,请勿提交申请！");
        }
        if (house.getPause() != null && house.getPause() == 1) {
            return ServerResponse.createByErrorMessage("该房子已暂停施工,请勿提交申请！");
        }
        List<HouseFlowApply> houseFlowApplyList = getLeave(hf);
        if (houseFlowApplyList.size() > 0) {
            HouseFlowApply houseFlowApply = houseFlowApplyList.get(0);
            if (houseFlowApply.getStartDate().before(new Date()) && houseFlowApply.getEndDate().after(new Date())) {
                return ServerResponse.createByErrorMessage("工序(" + workerType.getName() + ")处于停工期间!");
            }
        }
        /*提交整体完工申请检测是否存在待处理的变跟单进行控制*/
        List<ChangeOrder> changeOrderList = changeOrderMapper.unCheckOrder(hf.getHouseId(), workerType.getId());
        if (changeOrderList.size() > 0) {
            return ServerResponse.createByErrorMessage("该工种（" + workerType.getName() + "）有未处理变更单,通知管家处理");
        }
        //包括所有申请 和 巡查
        houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 2, worker.getId(), new Date());
        if (houseFlowApplyList.size() > 0) {
            return ServerResponse.createByErrorMessage("您今日已提交过此申请,请勿重复提交！");
        }
        /*待审核申请*/
        List<HouseFlowApply> hfaList = houseFlowApplyMapper.checkPendingApply(hf.getId(), worker.getId());
        if (hfaList.size() > 0) {
            return ServerResponse.createByErrorMessage("您有待审核的申请,请联系管家业主审核后再提交");
        }
        HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hf.getHouseId(), hf.getWorkerTypeId());
        HouseFlow supervisorHF = houseFlowMapper.getHouseFlowByHidAndWty(hf.getHouseId(), 3);//大管家的hf
        HouseFlowApply hfa = getHouseFlowApply(hf, 2, supervisorHF);
        //********************发申请，计算可得钱和积分等*****************//
        BigDecimal workPrice = new BigDecimal(0);
        BigDecimal haveMoney = new BigDecimal(0);
        BigDecimal retentionMoney = new BigDecimal(0);
        BigDecimal deductPrice = new BigDecimal(0);
        if (hwo != null) {
            workPrice = hwo.getWorkPrice();//工钱
            haveMoney = hwo.getHaveMoney();
            retentionMoney = hwo.getRetentionMoney() == null ? new BigDecimal(0) : hwo.getRetentionMoney();//滞留金
            deductPrice = hwo.getDeductPrice() == null ? new BigDecimal(0) : hwo.getDeductPrice();//评价积分扣除的钱
        }
        BigDecimal alsoMoney = new BigDecimal(workPrice.doubleValue() - haveMoney.doubleValue() - retentionMoney.doubleValue() - deductPrice.doubleValue());
        hfa.setApplyMoney(alsoMoney);
//        hfa.setApplyDec("我是" + workerType.getName() + ",我已申请了整体完工");//描述
        hfa.setApplyDec("尊敬的业主，您好！<br/>" +
                "当家工匠【" + worker.getName() + "】为您新家施工，工地【" + workerType.getName() + "】已全部完工，严格按照平台施工验收标准进行施工，请您查收。<br/>");//描述

        hfa.setSupervisorMoney(supervisorHF.getCheckMoney());//管家得相应验收收入
        //增加倒计时系统自动审核时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 3);//管家倒计时
        hfa.setStartDate(calendar.getTime());
        hfa.setIsReadType(0);
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        configMessageService.addConfigMessage(null, AppType.GONGJIANG, supervisorHF.getWorkerId(), "0", "整体完工申请",
                String.format(DjConstants.PushMessage.STEWARD_APPLY_FINISHED, house.getHouseName(), workerType.getName()), "5");
        setHouseFlowApplyImage(hfa, house, imageList);
        return ServerResponse.createBySuccessMessage("工序（" + workerType.getName() + "）整体完工申请成功");
    }

    /**
     * 每日开工
     */
    private ServerResponse setStartDaily(Member worker, HouseFlow hf, House house, String imageList) {
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
        if (hf.getWorkSteta() == 2) {
            return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）已经整体完工，无法开工");
        }
        Example example = new Example(DjRoyaltyMatch.class);
        example.createCriteria().andEqualTo(DjRoyaltyMatch.HOUSE_ID, house.getId())
                .andEqualTo(DjRoyaltyMatch.DATA_STATUS, 1);
        if (djRoyaltyMatchMapper.selectByExample(example).size() > 0) {//第一个工匠开工销售拿提成
            example = new Example(DjRoyaltyMatch.class);
            example.createCriteria().andEqualTo(DjRoyaltyMatch.HOUSE_ID, house.getId());
            DjRoyaltyMatch djRoyaltyMatch = new DjRoyaltyMatch();
            djRoyaltyMatch.setId(null);
            djRoyaltyMatch.setDataStatus(0);
            djRoyaltyMatchMapper.updateByExampleSelective(djRoyaltyMatch, example);
        }
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(null, 4, worker.getId(), new Date());
        for (HouseFlowApply houseFlowApply : houseFlowApplyList) {
            example = new Example(HouseFlowApply.class);
            example.createCriteria().andCondition("   apply_type in (0,1,2)  and   to_days(create_date) = to_days('"
                    + DateUtil.getDateString(new Date().getTime()) + "') ")
                    .andNotEqualTo(HouseFlowApply.SUPERVISOR_CHECK, 2)
                    .andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, houseFlowApply.getHouseFlowId());
            List<HouseFlowApply> houseFlowApplyList1 = houseFlowApplyMapper.selectByExample(example);
            if (houseFlowApplyList1.size() == 0) {
                House house1 = houseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());//工序
                if (house1 != null && house1.getVisitState() == 1) {
                    return ServerResponse.createByErrorMessage("工地[" + house1.getHouseName() + "]今日还未完工，无法开工");
                }
            }
        }
        if (active != null && active.equals("pre")) {
            houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 4, worker.getId(), new Date());
            if (houseFlowApplyList.size() > 0) {
                return ServerResponse.createByErrorMessage("您今日已提交过此申请,请勿重复提交！");
            }
        }
        HouseFlowApply hfa = getHouseFlowApply(hf, 4, null);
        hfa.setPayState(0);//是否付款

        hfa.setApplyDec("尊敬的业主，您好！<br/>" +
                "好工匠在当家，当家工匠" + workerType.getName() + "【" + worker.getName() + "】已到吉屋准备开工");

        hfa.setMemberCheck(1);//默认业主审核状态通过
        hfa.setSupervisorCheck(1);//默认大管家审核状态通过
        hfa.setIsReadType(0);
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        setHouseFlowApplyImage(hfa, house, imageList);
        //已经停工的工序，若工匠提前复工，则复工日期以及之后的停工全部取消，
        // 原来被停工推后了的计划完工日期往前推，推的天数等于被取消的停工天数
        Date start = DateUtil.toDate(DateUtil.dateToString(new Date(), null));
        Date end = start;

        example = new Example(HouseFlowApply.class);
        example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, hf.getId())
                .andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                .andEqualTo(HouseFlowApply.MEMBER_CHECK, 1)
                .andEqualTo(HouseFlowApply.DATA_STATUS, 0)
//                .andCondition("( start_date >= '" + DateUtil.getDateString2(new Date().getTime()) + "' and end_date <= '" + DateUtil.getDateString2(new Date().getTime()) + "')");
                .andCondition(" ('" + DateUtil.getDateString2(new Date().getTime()) + "' BETWEEN start_date and end_date)   ");
        List<HouseFlowApply> houseFlowList = houseFlowApplyMapper.selectByExample(example);
        for (HouseFlowApply houseFlowApply : houseFlowList) {
            if (houseFlowApply.getEndDate().getTime() > end.getTime()) {
                end = houseFlowApply.getEndDate();
                //更新实际停工天数
                houseFlowApply.setEndDate(start);
                if (houseFlowApply.getStartDate().getTime() >= houseFlowApply.getEndDate().getTime()) {
                    houseFlowApply.setEndDate(houseFlowApply.getStartDate());
                    houseFlowApply.setSuspendDay(0);
                    houseFlowApply.setDataStatus(1);
                }
                houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
            }
        }
        int suspendDay = 1 + DateUtil.daysofTwo(start, end);
        if (suspendDay > 0) {
            //计划提前
            houseFlowScheduleService.updateFlowSchedule(hf.getHouseId(), hf.getWorkerTypeId(), null, suspendDay);
        }
        //重新获取最新信息,防止计划时间变更后还原
        hf = houseFlowMapper.selectByPrimaryKey(hf.getId());
        //若未进场的工序比计划开工日期提早开工，则计划开工日期修改为实际开工日期，（施工天数不变）完工日期随之提早
        if (hf.getStartDate() != null && hf.getStartDate().getTime() > start.getTime()) {
            suspendDay = DateUtil.daysofTwo(start, hf.getStartDate());
            hf.setStartDate(DateUtil.delDateDays(hf.getStartDate(), suspendDay));
            hf.setEndDate(DateUtil.delDateDays(hf.getEndDate(), suspendDay));
        }
        hf.setPause(0);//0:正常；1暂停；
        houseFlowMapper.updateByPrimaryKeySelective(hf);//发每日开工将暂停状态改为正常

        //延期扣积分（每天）
        if(hf.getEndDate()!=null&&hf.getEndDate().getTime()<new Date().getTime()){
           Double evaluation= configRuleUtilService.getDelayCount(1);
           evaluateService.updateMemberIntegral(hf.getWorkerId(),hf.getHouseId(),hf.getId(),new BigDecimal(evaluation),"延期扣积分");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 巡查
     */
    private ServerResponse setPatrol(Member supervisor, HouseFlow supervisorHF, House house, Integer applyType, String applyDec, String imageList) {
        if (house.getPause() != null && house.getPause() == 1) {
            return ServerResponse.createByErrorMessage("该房子已暂停施工,请勿提交申请！");
        }

        HouseFlowApply hfa;
        if (applyType == 5) {//有人巡
            if (active != null && active.equals("pre")) {
                List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(supervisorHF.getId(), applyType, supervisor.getId(), new Date());
                if (houseFlowApplyList.size() > 0) {
                    return ServerResponse.createByErrorMessage("您今日已提交过此申请,请勿重复提交！");
                }
            }
            hfa = getHouseFlowApply(supervisorHF, applyType, supervisorHF);

            hfa.setApplyDec("尊敬的业主，您好！\n" +
                    "当家大管家【" + supervisor.getName() + "】为您新家质量保驾护航，今日巡查房屋现场情况如下：" + applyDec + "，未发现施工不合格情况，请您查收。");//描述
            //描述
            hfa.setMemberCheck(1);//默认业主审核状态通过
            hfa.setSupervisorCheck(1);//默认大管家审核状态通过
            Example example2 = new Example(HouseFlowApply.class);
            example2.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, house.getId())
                    .andEqualTo(HouseFlowApply.WORKER_TYPE_ID, supervisorHF.getWorkerTypeId())
                    .andEqualTo(HouseFlowApply.APPLY_TYPE, 5);
            List<HouseFlowApply> hfalist = houseFlowApplyMapper.selectByExample(example2);

            example2 = new Example(HouseFlowApply.class);
            example2.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, house.getId())
                    .andEqualTo(HouseFlowApply.WORKER_TYPE_ID, supervisorHF.getWorkerTypeId())
                    .andEqualTo(HouseFlowApply.APPLY_TYPE, 5)
                    .andCondition(" DATE_FORMAT(create_date, '%x年-第%v周' ) = DATE_FORMAT( SYSDATE(), '%x年-第%v周' ) ");
            Integer patrolNum = houseFlowApplyMapper.selectCountByExample(example2);
            JSONObject patrolCfg=configRuleUtilService.getAllTowValue(ConfigRuleService.MK022);
            //工人houseflow
            if (supervisorHF.getPatrol()!=null&&hfalist.size() < supervisorHF.getPatrol()&&patrolNum<patrolCfg.getInteger("patrolNum")) {//该工种没有巡查够，每次要拿钱
                HouseWorkerOrder supervisorHWO = houseWorkerOrderMapper.getHouseWorkerOrder(supervisorHF.getHouseId(), supervisor.getId(), supervisorHF.getWorkerTypeId());
                if (supervisorHWO.getCheckMoney() == null) {
                    supervisorHWO.setCheckMoney(new BigDecimal(0));
                }
                if (supervisorHWO.getHaveMoney() == null) {
                    supervisorHWO.setHaveMoney(new BigDecimal(0));
                }
                //累计大管家拿到的钱
                supervisorHWO.setHaveMoney(supervisorHWO.getHaveMoney().add(supervisorHF.getPatrolMoney()));
                //累计大管家订单巡查得到的钱
                supervisorHWO.setCheckMoney(supervisorHWO.getCheckMoney().add(supervisorHF.getPatrolMoney()));
                houseWorkerOrderMapper.updateByPrimaryKeySelective(supervisorHWO);
                //申请中记录大管家钱
                hfa.setSupervisorMoney(supervisorHF.getPatrolMoney());//本次大管家得到的钱
                //大管家剩余
                hfa.setOtherMoney(supervisorHWO.getWorkPrice().subtract(supervisorHWO.getHaveMoney()));
                if (supervisor.getHaveMoney() == null) {
                    supervisor.setHaveMoney(new BigDecimal(0));
                }
                if (supervisor.getSurplusMoney() == null) {
                    supervisor.setSurplusMoney(new BigDecimal(0));
                }
                BigDecimal haveMoneys = supervisor.getHaveMoney().add(supervisorHF.getPatrolMoney());
                BigDecimal surplusMoneys = supervisor.getSurplusMoney().add(supervisorHF.getPatrolMoney());
                supervisor.setHaveMoney(haveMoneys);
                supervisor.setSurplusMoney(surplusMoneys);
                memberMapper.updateByPrimaryKeySelective(supervisor);
                //记录到管家流水
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("大管家-巡查收入");
                workerDetail.setWorkerId(supervisor.getId());
                workerDetail.setWorkerName(supervisor.getName());
                workerDetail.setHouseId(hfa.getHouseId());
                workerDetail.setMoney(supervisorHF.getPatrolMoney());
                workerDetail.setState(0);//进钱
                workerDetail.setHaveMoney(supervisorHWO.getHaveMoney());
                workerDetail.setHouseWorkerOrderId(supervisorHWO.getId());
                workerDetail.setApplyMoney(haveMoneys);
                workerDetail.setWalletMoney(supervisor.getSurplusMoney());
                workerDetailMapper.insert(workerDetail);
            }
            Double  integral= configRuleUtilService.getWerkerIntegral(hfa.getHouseId(), ConfigRuleService.SG002, supervisor.getEvaluationScore(),0);
            if (integral>0) {
                WorkIntegral workIntegral = new WorkIntegral();
                workIntegral.setWorkerId(supervisor.getId());
                workIntegral.setHouseId(hfa.getHouseId());
                workIntegral.setStatus(0);
                workIntegral.setIntegral(new BigDecimal(integral));
                workIntegral.setBriefed("巡查获取积分！");
                workIntegral.setAnyBusinessId(hfa.getId());
                workIntegralMapper.insert(workIntegral);
            }
        } else {
            hfa = getHouseFlowApply(supervisorHF, applyType, supervisorHF);
            hfa.setApplyDec("业主您好，我已巡查了工地，现场情况如下：" + applyDec);//描述
            hfa.setMemberCheck(1);//默认业主审核状态通过
            hfa.setSupervisorCheck(1);//默认大管家审核状态通过
        }
        hfa.setIsReadType(0);
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        //推送消息给业主大管家巡查完成
        configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(),
                "0", "大管家巡查", String.format(DjConstants.PushMessage.DAGUANGJIAXUNCHAWANGCHENG,
                        house.getHouseName()), "5");
        setHouseFlowApplyImage(hfa, house, imageList);
        return ServerResponse.createBySuccessMessage("巡查成功");
    }

    /**
     * 获取请假
     */
    private List<HouseFlowApply> getLeave(HouseFlow hf) {
        //查询有没有请假
        Example example = new Example(HouseFlowApply.class);
        example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, hf.getId())
                .andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                .andCondition(" member_check in (1,3)")
                .andEqualTo(HouseFlowApply.PAY_STATE, 1);
        return houseFlowApplyMapper.selectByExample(example);
    }

    /**
     * 初始化审核对象
     */
    private HouseFlowApply getHouseFlowApply(HouseFlow hf, Integer applyType, HouseFlow supervisorHF) {
        //****针对老工地管家兼容巡查拿钱和验收拿钱***//
        if (supervisorHF != null && (supervisorHF.getPatrolMoney() == null || supervisorHF.getCheckMoney() == null ||
                supervisorHF.getPatrolMoney().compareTo(new BigDecimal(0)) == 0
                || supervisorHF.getCheckMoney().compareTo(new BigDecimal(0)) == 0)) {
            House house = houseMapper.selectByPrimaryKey(supervisorHF.getHouseId());

            Member worker = memberMapper.selectByPrimaryKey(supervisorHF.getWorkerId());
            grabSheet(worker, house, supervisorHF);
        }
        HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
        hfa.setHouseFlowId(hf.getId());//工序id
        hfa.setWorkerId(hf.getWorkerId());//工人id
        hfa.setOperator(hf.getWorkerId());//提交人ID
        hfa.setWorkerTypeId(hf.getWorkerTypeId());//工种id
        hfa.setWorkerType(hf.getWorkerType());//工种类型
        hfa.setHouseId(hf.getHouseId());//房子id
        hfa.setApplyType(applyType);//申请类型0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5巡查,6无人巡查
        hfa.setApplyDec("");//描述
        hfa.setApplyMoney(new BigDecimal(0));//申请得钱
        hfa.setSupervisorMoney(new BigDecimal(0));
        hfa.setOtherMoney(new BigDecimal(0));
        hfa.setMemberCheck(0);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
        hfa.setSupervisorCheck(0);//大管家审核状态0未审核，1审核通过，2审核不通过
        hfa.setPayState(0);//是否付款
        return hfa;
    }

    /**
     * 保存水电管路图图片
     */
    public ServerResponse setHouseFlowImage(String houseId, String imageList) {
        StringBuilder strbfr = new StringBuilder();
        if (StringUtil.isNotEmpty(imageList)) {
            JSONArray imageObjArr = JSON.parseArray(imageList);
            for (int i = 0; i < imageObjArr.size(); i++) {//上传材料照片
                JSONObject imageObj = imageObjArr.getJSONObject(i);
                int imageType = Integer.parseInt(imageObj.getString("imageType"));
                String imageUrl = imageObj.getString("imageUrl"); //图片,拼接
                String[] imageArr = imageUrl.split(",");
                for (String anImageArr : imageArr) {
                    HouseFlowApplyImage houseFlowApplyImage = new HouseFlowApplyImage();
                    houseFlowApplyImage.setHouseId(houseId);
                    houseFlowApplyImage.setImageUrl(anImageArr);
                    houseFlowApplyImage.setImageType(imageType);//图片类型 0：材料照片；1：进度照片；2:现场照片；3:其他
                    houseFlowApplyImage.setImageTypeName(imageObj.getString("imageTypeName"));//图片类型名称 例如：材料照片；进度照片
                    houseFlowApplyImageMapper.insert(houseFlowApplyImage);
                }
            }
        }
        return ServerResponse.createBySuccessMessage("上传成功");
    }
    /**
     * 保存巡查图片,验收节点图片等信息
     */
    public String setHouseFlowApplyImage(HouseFlowApply hfa, House house, String imageList) {
        StringBuilder strbfr = new StringBuilder();
        if (StringUtil.isNotEmpty(imageList)) {
            JSONArray imageObjArr = JSON.parseArray(imageList);
            for (int i = 0; i < imageObjArr.size(); i++) {//上传材料照片
                JSONObject imageObj = imageObjArr.getJSONObject(i);
                int imageType = Integer.parseInt(imageObj.getString("imageType"));
                String imageUrl = imageObj.getString("imageUrl"); //图片,拼接
                if (hfa.getApplyType()==5 && imageType == 3) {//节点图
                    String imageTypeId = imageObj.getString("imageTypeId");
                    String imageState = imageObj.getString("imageState");
                    TechnologyRecord technologyRecord = technologyRecordMapper.selectByPrimaryKey(imageTypeId);
                    if (technologyRecord == null) continue;
                    technologyRecord.setImage(imageUrl);
                    technologyRecord.setStewardCheckTime(new Date());
                    technologyRecord.setStewardHouseFlowApplyId(hfa.getId());
                    technologyRecord.setState(Integer.parseInt(imageState));//未验收
                    technologyRecord.setModifyDate(new Date());
                    technologyRecordMapper.updateByPrimaryKey(technologyRecord);
                    strbfr.append(technologyRecord.getName());
                    strbfr.append("<br/>");
                }
                if (hfa.getApplyType()!=5 && imageType == 3) {//节点图
                    String imageTypeId = imageObj.getString("imageTypeId");
                    String productId = imageObj.getString("productId");
                    Technology technology = iMasterTechnologyMapper.selectByPrimaryKey(imageTypeId);
                    // forMasterAPI.byTechnologyId(house.getCityId(), imageTypeId);
                    if (technology == null) continue;
                    TechnologyRecord technologyRecord = new TechnologyRecord();
                    technologyRecord.setHouseId(house.getId());
                    technologyRecord.setHouseFlowApplyId(hfa.getId());
                    technologyRecord.setTechnologyId(technology.getId());
                    technologyRecord.setProductId(productId);
                    technologyRecord.setName(technology.getName());//工艺节点名
                    technologyRecord.setMaterialOrWorker(technology.getMaterialOrWorker());
                    if (technology.getMaterialOrWorker() == 0) {
                        technologyRecord.setWorkerTypeId("3");//暂时放管家那里看这些节点
                    } else {
                        technologyRecord.setWorkerTypeId(technology.getWorkerTypeId());
                    }
                    technologyRecord.setImage(imageUrl);
                    technologyRecord.setState(0);//未验收
                    technologyRecord.setModifyDate(new Date());
                    technologyRecordMapper.insert(technologyRecord);
                    strbfr.append(technology.getName());
                    strbfr.append("<br/>");
                }

                String[] imageArr = imageUrl.split(",");
                for (String anImageArr : imageArr) {
                    HouseFlowApplyImage houseFlowApplyImage = new HouseFlowApplyImage();
                    houseFlowApplyImage.setHouseFlowApplyId(hfa.getId());
                    houseFlowApplyImage.setHouseId(house.getId());
                    houseFlowApplyImage.setImageUrl(anImageArr);
                    houseFlowApplyImage.setImageType(imageType);//图片类型 0：材料照片；1：进度照片；2:现场照片；3:其他
                    houseFlowApplyImage.setImageTypeName(imageObj.getString("imageTypeName"));//图片类型名称 例如：材料照片；进度照片
                    houseFlowApplyImageMapper.insert(houseFlowApplyImage);
                }

            }
        }
        return strbfr.toString();
    }


    /**
     * 提前入场
     */
    public ServerResponse getAdvanceInAdvance(String houseFlowId) {
        try {
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            if (houseFlow.getWorkType() > 1) {
                return ServerResponse.createByErrorMessage("提前进场失败");
            } else if (houseFlow.getWorkType() == null) {
                houseFlow.setWorkType(0);//
            }
            houseFlow.setWorkType(5);
            houseFlow.setModifyDate(new Date());
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);


            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            String msg="业主您好！您的美宅"+house+","+workerType.getType()+"即将进场，请即时支付相关费用。";
            configMessageService.addConfigMessage(AppType.ZHUANGXIU,house.getMemberId(),
                    workerType.getType()+"工序进场支付", msg, 4, houseFlow.getId(), msg);



            return ServerResponse.createBySuccessMessage("提前进场成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，提前进场失败");
        }
    }


    public ServerResponse getMyHouseFlowList(PageDTO pageDTO, String userToken) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        List<MyHouseFlowDTO> listHouseWorker = houseWorkerMapper.getMyHouseFlowList(worker.getId(), worker.getWorkerType());
        if (listHouseWorker == null || listHouseWorker.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(listHouseWorker);
        for (MyHouseFlowDTO myHouseFlowDTO : listHouseWorker) {
            Member member = memberMapper.selectByPrimaryKey(myHouseFlowDTO.getMemberId());
            if (member != null) {
                myHouseFlowDTO.setMemberName(member.getNickName());
            }
            HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(myHouseFlowDTO.getHouseId(), myHouseFlowDTO.getWorkerTypeId());
            if (houseWorkerOrder != null) {
                if (houseWorkerOrder.getWorkPrice() == null) {
                    houseWorkerOrder.setWorkPrice(new BigDecimal(0));
                }
                if (houseWorkerOrder.getRepairPrice() == null) {
                    houseWorkerOrder.setRepairPrice(new BigDecimal(0));
                }
                BigDecimal remain = houseWorkerOrder.getWorkPrice().add(houseWorkerOrder.getRepairPrice());
                myHouseFlowDTO.setPrice("¥" + (String.format("%.2f", remain.doubleValue())));
            }

            List<HouseFlowApply> todayStartList = houseFlowApplyMapper.getTodayStartByHouseId(myHouseFlowDTO.getHouseId(), new Date());//查询今日开工记录
            if (todayStartList == null || todayStartList.size() == 0) {//没有今日开工记录
                myHouseFlowDTO.setHouseIsStart("今日未开工");//是否正常施工
            } else {
                myHouseFlowDTO.setHouseIsStart("今日已开工");//是否正常施工
            }
            List<HouseFlowApply> supervisorCheckList = houseFlowApplyMapper.getSupervisorCheckList(myHouseFlowDTO.getHouseId());//查询所有待大管家审核
            myHouseFlowDTO.setTaskNumber(supervisorCheckList == null ? 0 : supervisorCheckList.size());//任务数量
        }
        pageResult.setList(listHouseWorker);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 切换工地
     */
    public ServerResponse setSwitchHouseFlow(String userToken, String houseWorkerId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andCondition("  work_type IN ( 1, 6 ) ")
                    .andEqualTo(HouseWorker.WORKER_ID, worker.getId())
                    .andEqualTo(HouseWorker.WORKER_TYPE, worker.getWorkerType());
            List<HouseWorker> listHouseWorker = houseWorkerMapper.selectByExample(example);
            for (HouseWorker houseWorker : listHouseWorker) {
                if (houseWorker.getId().equals(houseWorkerId)) {//选中的任务isSelect改为1
                    houseWorker.setIsSelect(1);
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                } else {//其他改为0
                    houseWorker.setIsSelect(0);
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                }
            }
            return ServerResponse.createBySuccessMessage("切换工地成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，切换工地失败！");
        }
    }

    /**
     * 大管家申请验收
     */
    public ServerResponse setSupervisorApply(String userToken, String houseFlowId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询houseFlow
            HouseWorker hw = houseWorkerMapper.getHwByHidAndWtype(houseFlow.getHouseId(), worker.getWorkerType());//这是查的大管家houseworker
            HouseFlowApply houseFlowApp = houseFlowApplyMapper.checkSupervisorApply(houseFlow.getId(), hw.getWorkerId());//查询大管家是否有验收申请
            if (houseFlowApp != null) {
                return ServerResponse.createByErrorMessage("您已申请验收，重复申请！");
            }   //新生成大管家hfa
            HouseWorkerOrder supervisor = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hw.getHouseId(), hw.getWorkerTypeId());

            BigDecimal workPrice = supervisor.getWorkPrice() == null ? new BigDecimal(0) : supervisor.getWorkPrice();//总共钱
            BigDecimal haveMoney = supervisor.getHaveMoney() == null ? new BigDecimal(0) : supervisor.getHaveMoney();//已得到的钱
            BigDecimal repairPrice = supervisor.getRepairPrice() == null ? new BigDecimal(0) : supervisor.getRepairPrice();//当前阶段补人工钱
            BigDecimal retentionMoney = supervisor.getRetentionMoney() == null ? new BigDecimal(0) : supervisor.getRetentionMoney();//滞留金
            BigDecimal deductPrice = supervisor.getDeductPrice() == null ? new BigDecimal(0) : supervisor.getDeductPrice();//评价积分扣除的钱
            //总共钱-已得到的钱+补人工钱-滞留金-评价扣的钱=还可得钱
            BigDecimal alsoMoney = new BigDecimal(workPrice.doubleValue() - haveMoney.doubleValue() + repairPrice.doubleValue() - retentionMoney.doubleValue() - deductPrice.doubleValue());
            if (alsoMoney.doubleValue() < 0) {
                alsoMoney = new BigDecimal(0);
            }
            HouseFlowApply hfa = new HouseFlowApply();
            hfa.setHouseFlowId(houseFlow.getId());
            hfa.setWorkerId(hw.getWorkerId());
            hfa.setOperator(houseFlow.getWorkerId());
            hfa.setWorkerTypeId(hw.getWorkerTypeId());
            hfa.setWorkerType(hw.getWorkerType());
            hfa.setHouseId(hw.getHouseId());
            hfa.setPayState(0);
            hfa.setApplyType(2);//大管家没有阶段完工，直接整体完工
            hfa.setApplyDec("亲爱的业主，您的房子已经全部完工，大吉大利!");
            hfa.setSupervisorCheck(1);
            hfa.setMemberCheck(0);
            hfa.setApplyMoney(alsoMoney);//通过后拿剩下百分之50减押金
            hfa.setOtherMoney(new BigDecimal(0.0));
            hfa.setSuspendDay(0);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 7);//业主倒计时
            hfa.setEndDate(calendar.getTime());
            hfa.setIsReadType(0);
            houseFlowApplyMapper.insert(hfa);
            houseService.insertConstructionRecord(hfa);
            House house = houseMapper.selectByPrimaryKey(hfa.getHouseId());
            house.setTaskNumber(house.getTaskNumber() + 1);
            houseMapper.updateByPrimaryKeySelective(house);

            configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "竣工验收申请",
                    String.format(DjConstants.PushMessage.CRAFTSMAN_ALL_FINISHED, house.getHouseName()), "");
            return ServerResponse.createBySuccessMessage("申请验收成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，申请验收失败");
        }
    }

    public ServerResponse autoDistributeHandle(String houseFlowId) {
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        return autoDistributeHandle(houseFlow);
    }
    /**
     * 管家自动派单分值计算
     * @param houseFlow
     * @return
     */
    public ServerResponse autoDistributeHandle(HouseFlow houseFlow) {
        /*大管家所有待抢单*/
        Example example = new Example(HouseFlow.class);
        example.createCriteria()
                .andEqualTo(HouseFlow.WORK_TYPE, 2)
                .andEqualTo(HouseFlow.WORKER_TYPE_ID, 3)
                .andNotEqualTo(HouseFlow.STATE, 2);
        if(houseFlow.getWorkType()!=2){
            return ServerResponse.createByErrorMessage("状态错误，无法自动派单！");
        }
        if(houseFlow.getWorkerType()!=3){
            return ServerResponse.createByErrorMessage("非大管家，无法自动派单！");
        }
        House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());//查询房子
        ModelingVillage modelingVillage =modelingVillageMapper.selectByPrimaryKey(house.getVillageId());
        List<Map<String, Object>> list = houseWorkerMapper.getSupWorkerConfInfo(modelingVillage.getLocationx(),modelingVillage.getLocationy());
        for (Map<String, Object> stringObjectMap : list) {
            Double  score = configRuleUtilService.getautoDistributeHandleConfig((Double) stringObjectMap.get("juli"),(BigDecimal) stringObjectMap.get(Member.EVALUATION_SCORE),(Long) stringObjectMap.get(Member.METHODS));
            stringObjectMap.put("score",score);
        }
        //重新排序
        Collections.sort(list, new Comparator<Map<String, Object>>(){
            public int compare(Map<String, Object> o1, Map<String, Object> o2)
            {
                Double score1 = (Double)o1.get("score");
                Double score2 = (Double)o2.get("score");
                return score2.compareTo(score1);
            }
        });
        for (Map<String, Object> stringObjectMap : list) {
            String userToken = String.valueOf(stringObjectMap.get(HouseWorker.WORKER_ID));
            ServerResponse serverResponse = setWorkerGrab(userToken,  houseFlow.getCityId(), houseFlow.getId(),0);
            //如果已指派则无需继续遍历
            if(serverResponse.isSuccess()){
                break;
            }
        }
        return ServerResponse.createBySuccess("派单成功");
    }

}
