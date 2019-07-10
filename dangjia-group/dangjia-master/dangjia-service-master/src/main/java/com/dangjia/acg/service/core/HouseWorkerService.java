package com.dangjia.acg.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.MessageAPI;
import com.dangjia.acg.api.basics.WorkerGoodsAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HomePageBean;
import com.dangjia.acg.dto.house.HouseChatDTO;
import com.dangjia.acg.dto.house.MyHouseFlowDTO;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.menu.IMenuConfigurationMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.menu.MenuConfiguration;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.complain.ComplainService;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.util.LocationUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 18:58
 */
@Service
public class HouseWorkerService {

    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
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
    private ForMasterAPI forMasterAPI;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private HouseFlowApplyService houseFlowApplyService;
    @Autowired
    private IMenuConfigurationMapper iMenuConfigurationMapper;
    @Autowired
    private HouseService houseService;
    @Autowired
    private IChangeOrderMapper changeOrderMapper;
    @Autowired
    private HouseFlowScheduleService houseFlowScheduleService;
    @Autowired
    private ComplainService complainService;
    @Autowired
    private MessageAPI messageAPI;
    @Value("${spring.profiles.active}")
    private String active;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IComplainMapper complainMapper;
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;

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
        if (houseWorker.getWorkType() == 6) {
            return ServerResponse.createByErrorMessage("已支付不能换人,请联系当家装修");
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
            if (house != null) {
                configMessageService.addConfigMessage(null, "gj", workerId, "0", "业主换人提醒",
                        String.format(DjConstants.PushMessage.STEWARD_REPLACE, house.getHouseName()), "5");
                HouseFlow houseFlowDgj = houseFlowMapper.getHouseFlowByHidAndWty(houseFlow.getHouseId(), 3);
                if (houseFlowDgj != null && !CommonUtil.isEmpty(houseFlowDgj.getWorkerId())) {
                    configMessageService.addConfigMessage(null, "gj", houseFlowDgj.getWorkerId(), "0",
                            "业主换人提醒", String.format(DjConstants.PushMessage.STEWARD_CRAFTSMAN_TWO_REPLACE,
                                    house.getHouseName()), "5");
                }
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
            map.put("targetAppKey", "49957e786a91f9c55b223d58");
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
                map.put("targetAppKey", "49957e786a91f9c55b223d58");
                map.put("nickName", member1.getNickName());
                map.put("name", member1.getName());
                map.put("mobile", member1.getMobile());
                map.put("head", member1.getHead());
                map.put("workerTypeId", member1.getWorkerTypeId());
                map.put("workerName", workerType.getName());
                map.put("houseFlowId", houseFlow.getId());
                map.put("houseWorkerId", worker.getId());
                map.put("isSubstitution", 2);
                historyWorkerList.add(map);
            }
        }
        mapData.put("historyWorkerList", historyWorkerList);
        return ServerResponse.createBySuccess("查询成功", mapData);
    }

    /**
     * 抢单
     */
    public ServerResponse setWorkerGrab(HttpServletRequest request, String userToken, String cityId, String houseFlowId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            ServerResponse serverResponse = houseFlowService.setGrabVerification(userToken, cityId, houseFlowId);
            if (!serverResponse.isSuccess())
                return serverResponse;
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            if (houseFlow.getWorkType() == 3) {
                return ServerResponse.createByErrorMessage("该订单已被抢");
            }
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            houseFlow.setGrabNumber(houseFlow.getGrabNumber() + 1);
            houseFlow.setWorkType(3);//等待支付
            houseFlow.setWorkerId(worker.getId());
            grabSheet(worker, house, houseFlow, houseMapper);
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            houseWorkerMapper.doModifyAllByWorkerId(worker.getId());//将所有houseWorker的选中状态IsSelect改为0未选中
            HouseWorker houseWorker = new HouseWorker();
            houseWorker.setHouseId(house.getId());
            houseWorker.setWorkerId(worker.getId());
            houseWorker.setWorkerTypeId(houseFlow.getWorkerTypeId());
            houseWorker.setWorkerType(houseFlow.getWorkerType());
            houseWorker.setWorkType(1);//已抢单
            houseWorker.setIsSelect(1);
            houseWorkerMapper.insert(houseWorker);
//            3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工
            //通知业主设计师抢单成功
            if (worker.getWorkerType() == 1) {//设计师
                configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "设计师抢单提醒",
                        String.format(DjConstants.PushMessage.DESIGNER_GRABS_THE_BILL, house.getHouseName()), "");
            }
            //通知业主精算师抢单成功
            if (worker.getWorkerType() == 2) {//精算师
                configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "精算师抢单提醒",
                        String.format(DjConstants.PushMessage.BUDGET_GRABS_THE_BILL, house.getHouseName()), "");
            }
            //通知业主大管家抢单成功
            if (worker.getWorkerType() == 3) {//大管家

                configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "大管家抢单提醒",
                        String.format(DjConstants.PushMessage.STEWARD_RUSH_TO_PURCHASE, house.getHouseName()), "");
            }
            if (worker.getWorkerType() > 3) {//其他工匠
                configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "工匠抢单提醒",
                        String.format(DjConstants.PushMessage.CRAFTSMAN_RUSH_TO_PURCHASE, house.getHouseName()), "4");
                //通知大管家已有工匠抢单
                //通知大管家抢单
                HouseFlow houseFlowDgj = houseFlowMapper.getHouseFlowByHidAndWty(houseFlow.getHouseId(), 3);
                configMessageService.addConfigMessage(null, "gj", houseFlowDgj.getWorkerId(), "0", "工匠抢单提醒",
                        String.format(DjConstants.PushMessage.STEWARD_TWO_RUSH_TO_PURCHASE, house.getHouseName()), "4");
            }
            Example example = new Example(WorkerType.class);
            example.createCriteria().andEqualTo(WorkerType.TYPE, worker.getWorkerType());
            List<WorkerType> workerType = workerTypeMapper.selectByExample(example);
            String text = "业主您好,我是" + workerType.get(0).getName() + worker.getName() + "已成功抢单";
            HouseChatDTO h = new HouseChatDTO();
            h.setTargetId(house.getMemberId());
            h.setTargetAppKey(messageAPI.getAppKey("zx"));
            h.setText(text);
            return ServerResponse.createBySuccess("抢单成功", h);
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
     * @param houseMapper 房子表mapper
     */
    public void grabSheet(Member worker, House house, HouseFlow houseFlow, IHouseMapper houseMapper) {
        if (worker.getWorkerType() == 1) {//设计师
            house.setDesignerOk(house.getDecorationType() == 2 ? 1 : 4);//有设计抢单待业主支付
            houseFlow.setWorkType(house.getDecorationType() == 2 ? 4 : 3);
            houseMapper.updateByPrimaryKeySelective(house);
        } else if (worker.getWorkerType() == 2) {
            house.setBudgetOk(5);//有精算抢单待业主支付
            houseMapper.updateByPrimaryKeySelective(house);
        }
    }

    /**
     * 获取我的界面
     *
     * @param userToken 用户登录信息
     * @return 我的页面
     */
    public ServerResponse getMyHomePage(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
            worker = memberMapper.selectByPrimaryKey(worker.getId());
            if (worker == null) {
                return ServerResponse.createbyUserTokenError();
            }
            HomePageBean homePageBean = new HomePageBean();
            homePageBean.setWorkerId(worker.getId());
            homePageBean.setIoflow(CommonUtil.isEmpty(worker.getHead()) ? null : imageAddress + worker.getHead());
            homePageBean.setWorkerName(CommonUtil.isEmpty(worker.getName()) ? worker.getNickName() : worker.getName());
            homePageBean.setEvaluation(worker.getEvaluationScore() == null ? new BigDecimal(60) : worker.getEvaluationScore());
            homePageBean.setFavorable(worker.getPraiseRate() == null ? "0.00%" : worker.getPraiseRate().multiply(new BigDecimal(100)) + "%");
            StringBuilder stringBuffer = new StringBuilder();
            if (worker.getIsCrowned() == null || worker.getIsCrowned() != 1) {
                if (worker.getEvaluationScore() == null) {
                    stringBuffer.append("普通");
                } else if (Double.parseDouble(worker.getEvaluationScore().toString()) > 90) {
                    stringBuffer.append("金牌");
                } else if (Double.parseDouble(worker.getEvaluationScore().toString()) > 80) {
                    stringBuffer.append("银牌");
                } else if (Double.parseDouble(worker.getEvaluationScore().toString()) > 70) {
                    stringBuffer.append("铜牌");
                } else {
                    stringBuffer.append("普通");
                }
            } else {
                stringBuffer.append("皇冠");
            }
            stringBuffer.append(worker.getWorkerType() != null && worker.getWorkerType() == 3 ? "大管家" : "工匠");
            homePageBean.setGradeName(stringBuffer.toString());
            Example example = new Example(MenuConfiguration.class);
            Example.Criteria criteria = example.createCriteria()
                    .andEqualTo(MenuConfiguration.DATA_STATUS, 0)
                    .andEqualTo(MenuConfiguration.MENU_TYPE, 1);
            if (worker.getWorkerType() == null) {
                criteria.andEqualTo(MenuConfiguration.SHOW_CRAFTSMAN, 1);
            } else {
                switch (worker.getWorkerType()) {
                    case 1://设计师
                        criteria.andEqualTo(MenuConfiguration.SHOW_DESIGNER, 1);
                        break;
                    case 2://精算师
                        criteria.andEqualTo(MenuConfiguration.SHOW_ACTUARIES, 1);
                        break;
                    case 3://大管家
                        criteria.andEqualTo(MenuConfiguration.SHOW_HOUSEKEEPER, 1);
                        break;
                    default://工匠
                        criteria.andEqualTo(MenuConfiguration.SHOW_CRAFTSMAN, 1);
                        break;
                }
            }
            example.orderBy(MenuConfiguration.SORT).asc();
            List<MenuConfiguration> menuConfigurations = iMenuConfigurationMapper.selectByExample(example);
            List<HomePageBean.ListBean> list = new ArrayList<>();
            for (MenuConfiguration configuration : menuConfigurations) {
                configuration.initPath(imageAddress, webAddress);
                HomePageBean.ListBean listBean = new HomePageBean.ListBean();
                listBean.setName(configuration.getName());
                listBean.setUrl(configuration.getUrl());
                listBean.setImageUrl(configuration.getImage());
                listBean.setType(configuration.getType());
                list.add(listBean);
            }
            homePageBean.setList(list);
            return ServerResponse.createBySuccess("获取我的界面成功！", homePageBean);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取我的界面信息失败！");
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
     * 提交审核、停工
     * applyType   0每日完工申请，1阶段完工申请，2整体完工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
     */
    public ServerResponse setHouseFlowApply(String userToken, Integer applyType, String houseFlowId,
                                            String applyDec, String imageList, String houseFlowId2,
                                            String latitude, String longitude) {
        if (CommonUtil.isEmpty(applyType)) {
            return ServerResponse.createByErrorMessage("请选择提交审核类别");
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
                return setCompletedToday(worker, hf, house, imageList);
            case 1:
                return setPhaseCompletion(worker, hf, house, imageList);
            case 2:
                return setWholeCompletion(worker, hf, house, imageList);
            case 4:
                return setStartDaily(worker, hf, house, latitude, longitude);
            case 5:
            case 6:
            case 7:
                return setPatrol(worker, hf, house, applyType, applyDec, imageList, houseFlowId2);
        }
        return ServerResponse.createByErrorMessage("未找审核类型");
    }

    /**
     * 今日完工
     */
    private ServerResponse setCompletedToday(Member worker, HouseFlow hf, House house, String imageList) {
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
        houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 4, worker.getId(), new Date());
        if (houseFlowApplyList.size() > 0) {
            if(active!=null&&(active.equals("pre"))) {
                HouseFlowApply houseFlowApply = houseFlowApplyList.get(0);
                if (new Date().getTime() < DateUtil.addDateHours(houseFlowApply.getCreateDate(), 3).getTime()) {
                    return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）开工后3小时才能申请完工！");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）未开工，无法申请完工！");
        }
        //包括所有申请 和 巡查
        houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 0, worker.getId(), new Date());
        if (houseFlowApplyList.size() > 0) {
            return ServerResponse.createByErrorMessage("您今日已提交过此申请,请勿重复提交！");
        }
        /*待审核申请*/
        List<HouseFlowApply> hfaList = houseFlowApplyMapper.checkPendingApply(hf.getId(), worker.getId());
        if (hfaList.size() > 0) {
            return ServerResponse.createByErrorMessage("您有待审核的申请,请联系管家业主审核后再提交");
        }
        WorkDeposit workDeposit = workDepositMapper.selectByPrimaryKey(house.getWorkDepositId());//结算比例表
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
        hfa.setApplyDec("我是" + workerType.getName() + ",我今天已经完工了");//描述
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        setHouseFlowApplyImage(hfa, house, imageList);
        //每日完工
        houseFlowApplyService.checkWorker(hfa.getId(), false);
        return ServerResponse.createBySuccessMessage("工序（" + workerType.getName() + "）每日完工申请成功");
    }

    /**
     * 阶段完工
     */
    private ServerResponse setPhaseCompletion(Member worker, HouseFlow hf, House house, String imageList) {
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
        WorkDeposit workDeposit = workDepositMapper.selectByPrimaryKey(house.getWorkDepositId());//结算比例表
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
        hfa.setApplyDec("我是" + workerType.getName() + ",我已申请了阶段完工");//描述
        hfa.setSupervisorMoney(supervisorHF.getCheckMoney());//管家得相应验收收入
        //增加倒计时系统自动审核时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 3);//管家倒计时
        hfa.setStartDate(calendar.getTime());
        // 阶段完工,管家审核通过工匠完工申请 @link checkOk()
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        configMessageService.addConfigMessage(null, "gj", supervisorHF.getWorkerId(), "0", "阶段完工申请",
                String.format(DjConstants.PushMessage.STEWARD_APPLY_FINISHED, house.getHouseName(), workerType.getName()), "5");
        setHouseFlowApplyImage(hfa, house, imageList);
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
        hfa.setApplyDec("我是" + workerType.getName() + ",我已申请了整体完工");//描述
        hfa.setSupervisorMoney(supervisorHF.getCheckMoney());//管家得相应验收收入
        //增加倒计时系统自动审核时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 3);//管家倒计时
        hfa.setStartDate(calendar.getTime());
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        configMessageService.addConfigMessage(null, "gj", supervisorHF.getWorkerId(), "0", "整体完工申请",
                String.format(DjConstants.PushMessage.STEWARD_APPLY_FINISHED, house.getHouseName(), workerType.getName()), "5");
        setHouseFlowApplyImage(hfa, house, imageList);
        return ServerResponse.createBySuccessMessage("工序（" + workerType.getName() + "）整体完工申请成功");
    }

    /**
     * 每日开工
     */
    private ServerResponse setStartDaily(Member worker, HouseFlow hf, House house, String latitude, String longitude) {
        if(active!=null&&(active.equals("pre"))) {
            ModelingVillage village = modelingVillageMapper.selectByPrimaryKey(house.getVillageId());//小区
            if (village != null && village.getLocationx() != null && village.getLocationy() != null
                    && latitude != null && longitude != null) {
                try {
                    double longitude1 = Double.valueOf(longitude);
                    double latitude1 = Double.valueOf(latitude);
                    double longitude2 = Double.valueOf(village.getLocationx());
                    double latitude2 = Double.valueOf(village.getLocationy());
                    double distance = LocationUtils.getDistance(latitude1, longitude1, latitude2, longitude2);//计算距离
                    if (distance > 1000) {
                        return ServerResponse.createByErrorMessage("请确认您是否在小区范围内");
                    }
                } catch (Exception ignored) {
                }
            }
        }
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
        if (hf.getWorkSteta() == 2) {
            return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）已经整体完工，无法开工");
        }
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(null, 4, worker.getId(), new Date());
        for (HouseFlowApply houseFlowApply : houseFlowApplyList) {
            Example example = new Example(HouseFlowApply.class);
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
        houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 4, worker.getId(), new Date());
        if (houseFlowApplyList.size() > 0) {
            return ServerResponse.createByErrorMessage("您今日已提交过此申请,请勿重复提交！");
        }
        houseFlowApplyList = getLeave(hf);
        if (houseFlowApplyList.size() > 0) {
            for (HouseFlowApply hfa : houseFlowApplyList) {
                hfa.setMemberCheck(2);//不通过不通过
                hfa.setModifyDate(new Date());
                houseFlowApplyMapper.updateByPrimaryKeySelective(hfa);
            }
        }
        HouseFlowApply hfa = getHouseFlowApply(hf, 4, null);
        hfa.setPayState(0);//是否付款
        hfa.setApplyDec("我是" + workerType.getName() + ",我今天已经开工了");//描述
        hfa.setMemberCheck(1);//默认业主审核状态通过
        hfa.setSupervisorCheck(1);//默认大管家审核状态通过
        hf.setPause(0);//0:正常；1暂停；
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        //已经停工的工序，若工匠提前复工，则复工日期以及之后的停工全部取消，
        // 原来被停工推后了的计划完工日期往前推，推的天数等于被取消的停工天数
        Date start = DateUtil.toDate(DateUtil.dateToString(new Date(),null));
        Date end = start;

        Example example = new Example(HouseFlowApply.class);
        example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, hf.getId())
                .andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                .andEqualTo(HouseFlowApply.MEMBER_CHECK, 1)
//                .andCondition("( start_date >= '" + DateUtil.getDateString2(new Date().getTime()) + "' and end_date <= '" + DateUtil.getDateString2(new Date().getTime()) + "')");
                .andCondition(" ('" + DateUtil.getDateString2(new Date().getTime()) + "' BETWEEN start_date and end_date)   ");
        List<HouseFlowApply> houseFlowList = houseFlowApplyMapper.selectByExample(example);
        for (HouseFlowApply houseFlowApply : houseFlowList) {
            if (houseFlowApply.getEndDate().getTime() > end.getTime()) {
                end = houseFlowApply.getEndDate();
                //更新实际停工天数
                houseFlowApply.setEndDate(DateUtil.delDateDays(start, 1));
                if (houseFlowApply.getStartDate().getTime() <= houseFlowApply.getEndDate().getTime()) {
                    houseFlowApply.setEndDate(houseFlowApply.getStartDate());
                    houseFlowApply.setSuspendDay(0);
                    houseFlowApply.setDataStatus(1);
                }
                houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
            }
        }
        int suspendDay = DateUtil.daysofTwo(start, end);
        if (suspendDay > 0) {
            //计划提前
            houseFlowScheduleService.updateFlowSchedule(hf.getHouseId(), hf.getWorkerTypeId(), null, suspendDay);
        }
        //若未进场的工序比计划开工日期提早开工，则计划开工日期修改为实际开工日期，（施工天数不变）完工日期随之提早
        if (hf.getStartDate() != null && hf.getStartDate().getTime() > start.getTime()) {
            suspendDay = DateUtil.daysofTwo(start, hf.getStartDate());
            hf.setStartDate(DateUtil.delDateDays(hf.getStartDate(), suspendDay));
            hf.setEndDate(DateUtil.delDateDays(hf.getEndDate(), suspendDay));
        }
        houseFlowMapper.updateByPrimaryKeySelective(hf);//发每日开工将暂停状态改为正常
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 巡查
     */
    private ServerResponse setPatrol(Member supervisor, HouseFlow supervisorHF, House house, Integer applyType, String applyDec, String imageList, String houseFlowId2) {
        if (house.getPause() != null && house.getPause() == 1) {
            return ServerResponse.createByErrorMessage("该房子已暂停施工,请勿提交申请！");
        }
        HouseFlowApply hfa;
        if (applyType == 5) {//有人巡
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId2);
            if (hf == null) {
                return ServerResponse.createByErrorMessage("被巡查的工序不存在");
            }
            Member worker = memberMapper.selectByPrimaryKey(hf.getWorkerId());//查询对应的工人
            if (worker == null) {
                return ServerResponse.createByErrorMessage("被巡查的工序暂无工匠");
            }
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
            if (hf.getPause() == 1) {
                return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）已暂停施工,请勿提交申请！");
            }
            List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), applyType, worker.getId(), new Date());
            if (houseFlowApplyList.size() > 0) {
                return ServerResponse.createByErrorMessage("您今日已提交过此申请,请勿重复提交！");
            }
            hfa = getHouseFlowApply(supervisorHF, applyType, supervisorHF);
            hfa.setApplyDec("业主您好，我已巡查了" + workerType.getName() + "，现场情况如下：" + applyDec);//描述
            //描述
            hfa.setMemberCheck(1);//默认业主审核状态通过
            hfa.setSupervisorCheck(1);//默认大管家审核状态通过
            Example example2 = new Example(HouseFlowApply.class);
            example2.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, house.getId())
                    .andEqualTo(HouseFlowApply.WORKER_TYPE_ID, worker.getWorkerTypeId())
                    .andEqualTo(HouseFlowApply.APPLY_TYPE, 5);
            List<HouseFlowApply> hfalist = houseFlowApplyMapper.selectByExample(example2);
            //工人houseflow
            if (hfalist.size() < hf.getPatrol()) {//该工种没有巡查够，每次要拿钱
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
                workerDetail.setName(workerType.getName() + "巡查收入");
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
        } else {
            hfa = getHouseFlowApply(supervisorHF, applyType, supervisorHF);
            hfa.setApplyDec("业主您好，我已巡查了工地，现场情况如下：" + applyDec);//描述
            hfa.setMemberCheck(1);//默认业主审核状态通过
            hfa.setSupervisorCheck(1);//默认大管家审核状态通过
        }
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        //推送消息给业主大管家巡查完成
        configMessageService.addConfigMessage(null, "zx", house.getMemberId(),
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
            this.calculateSup(supervisorHF);
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
     * 保存巡查图片,验收节点图片等信息
     */
    private void setHouseFlowApplyImage(HouseFlowApply hfa, House house, String imageList) {
        if (StringUtil.isNotEmpty(imageList)) {
            JSONArray imageObjArr = JSON.parseArray(imageList);
            for (int i = 0; i < imageObjArr.size(); i++) {//上传材料照片
                JSONObject imageObj = imageObjArr.getJSONObject(i);
                int imageType = Integer.parseInt(imageObj.getString("imageType"));
                String imageUrl = imageObj.getString("imageUrl"); //图片,拼接
                if (imageType == 3) {//节点图
                    String imageTypeId = imageObj.getString("imageTypeId");
                    String imageTypeName = imageObj.getString("imageTypeName");
                    Technology technology = forMasterAPI.byTechnologyId(house.getCityId(), imageTypeId);
                    TechnologyRecord technologyRecord = new TechnologyRecord();
                    technologyRecord.setHouseId(house.getId());
                    technologyRecord.setHouseFlowApplyId(hfa.getId());
                    technologyRecord.setTechnologyId(technology.getId());
                    technologyRecord.setName(imageTypeName);//工艺节点名
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
                } else {
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
        }
    }

    /**
     * 算管家巡查验收拿钱
     */
    private void calculateSup(HouseFlow supervisorHF) {
        try {
            House house = houseMapper.selectByPrimaryKey(supervisorHF.getHouseId());
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            List<HouseFlow> houseFlowList = houseFlowMapper.getForCheckMoney(supervisorHF.getHouseId());
            WorkDeposit workDeposit = workDepositMapper.selectByPrimaryKey(house.getWorkDepositId());//结算比例表
            int check = 0;//累计大管家总巡查次数
            int time = 0;//累计管家总阶段验收和完工验收次数
            for (HouseFlow houseflow : houseFlowList) {
                //查出该工种工钱
                Double workerTotal = 0.0;
                request.setAttribute(Constants.CITY_ID, house.getCityId());
                ServerResponse serverResponse = workerGoodsAPI.getWorkertoCheck(request, supervisorHF.getHouseId(), houseflow.getId());
                if (serverResponse.isSuccess()) {
                    JSONObject obj = JSONObject.parseObject(serverResponse.getResultObj().toString());
                    workerTotal = obj.getDouble("totalPrice");
                }
                int inspectNumber = workerTypeMapper.selectByPrimaryKey(houseflow.getWorkerTypeId()).getInspectNumber();//该工种配置默认巡查次数
                int thisCheck = workerTotal.intValue() / workDeposit.getPatrolPrice().intValue();//该工种钱算出来的巡查次数
                if (thisCheck > inspectNumber) {
                    thisCheck = inspectNumber;
                }
                houseflow.setPatrol(thisCheck);//保存巡查次数
                houseFlowMapper.updateByPrimaryKeySelective(houseflow);
                //累计总巡查
                check += thisCheck;
                //累计总验收
                if (houseflow.getWorkerType() == 4) {
                    time++;
                } else {
                    time += 2;
                }
            }
            //拿到这个大管家已支付工钱
            BigDecimal moneySup = new BigDecimal(0);
            if (supervisorHF.getWorkPrice().compareTo(new BigDecimal(0)) == 0) {
                request.setAttribute(Constants.CITY_ID, house.getCityId());
                ServerResponse serverResponse = workerGoodsAPI.getWorkertoCheck(request, supervisorHF.getHouseId(), supervisorHF.getId());
                if (serverResponse.isSuccess()) {
                    JSONObject obj = JSONObject.parseObject(serverResponse.getResultObj().toString());
                    moneySup = BigDecimal.valueOf(obj.getDouble("totalPrice"));
                }
            } else {
                moneySup = supervisorHF.getWorkPrice();
            }
            //算管家每次巡查钱
            BigDecimal patrolMoney = new BigDecimal(0);
            if (check > 0) {
                patrolMoney = moneySup.multiply(new BigDecimal(0.2)).divide(new BigDecimal(check), 2, BigDecimal.ROUND_HALF_UP);
            }
            //算管家每次验收钱
            BigDecimal checkMoney = new BigDecimal(0);
            if (time > 0) {
                checkMoney = moneySup.multiply(new BigDecimal(0.3)).divide(new BigDecimal(time), 2, BigDecimal.ROUND_HALF_UP);
            }
            //保存到大管家的houseflow
            supervisorHF.setPatrolMoney(patrolMoney);//巡查钱
            supervisorHF.setCheckMoney(checkMoney);//验收钱
            houseFlowMapper.updateByPrimaryKeySelective(supervisorHF);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            houseFlow.setWorkType(2);
            houseFlow.setReleaseTime(new Date());//发布时间
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            return ServerResponse.createBySuccessMessage("提前进场成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，提前进场失败");
        }
    }

    /**
     * 根据工匠id查询施工列表
     * TODO 1.4.0后删除此接口
     */
    public ServerResponse getHouseFlowList(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            List<MyHouseFlowDTO> listHouseWorker = houseWorkerMapper.getMyHouseFlowList(worker.getId(), worker.getWorkerType());
            if (listHouseWorker == null || listHouseWorker.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            for (MyHouseFlowDTO myHouseFlowDTO : listHouseWorker) {
                HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(myHouseFlowDTO.getHouseId(), myHouseFlowDTO.getWorkerTypeId());
                if (houseWorkerOrder != null) {
                    if (houseWorkerOrder.getRepairPrice() == null) {
                        houseWorkerOrder.setRepairPrice(new BigDecimal(0));
                    }
                    if (houseWorkerOrder.getWorkPrice() == null) {
                        houseWorkerOrder.setWorkPrice(new BigDecimal(0));
                    }
                    BigDecimal remain = houseWorkerOrder.getWorkPrice().add(houseWorkerOrder.getRepairPrice());
                    myHouseFlowDTO.setPrice("¥" + (String.format("%.2f", remain.doubleValue())));
                }
                Member member = memberMapper.selectByPrimaryKey(myHouseFlowDTO.getMemberId());
                if (member != null) {
                    myHouseFlowDTO.setMemberName(member.getNickName());
                }
                List<HouseFlowApply> supervisorCheckList = houseFlowApplyMapper.getSupervisorCheckList(myHouseFlowDTO.getHouseId());//查询所有待大管家审核
                myHouseFlowDTO.setTaskNumber(supervisorCheckList == null ? 0 : supervisorCheckList.size());//任务数量
                List<HouseFlowApply> todayStartList = houseFlowApplyMapper.getTodayStartByHouseId(myHouseFlowDTO.getHouseId(), new Date());//查询今日开工记录
                if (todayStartList == null || todayStartList.size() == 0) {//没有今日开工记录
                    myHouseFlowDTO.setHouseIsStart("今日未开工");//是否正常施工
                } else {
                    myHouseFlowDTO.setHouseIsStart("今日已开工");//是否正常施工
                }
            }
            return ServerResponse.createBySuccess("获取施工列表成功", listHouseWorker);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，获取施工列表失败！");
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
    public ServerResponse setSwitchHouseFlow(String userToken, String houseFlowId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            Member worker = (Member) object;
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andCondition("  work_type IN ( 1, 6 ) ")
                    .andEqualTo(HouseWorker.WORKER_ID, worker.getId())
                    .andEqualTo(HouseWorker.WORKER_TYPE, worker.getWorkerType());
            List<HouseWorker> listHouseWorker = houseWorkerMapper.selectByExample(example);
            for (HouseWorker houseWorker : listHouseWorker) {
                if (houseWorker.getHouseId().equals(houseFlow.getHouseId())) {//选中的任务isSelect改为1
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
            }
            //新生成大管家hfa
            HouseWorkerOrder supervisor = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hw.getHouseId(), hw.getWorkerTypeId());
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
            hfa.setApplyMoney(supervisor.getWorkPrice().multiply(new BigDecimal(0.5)));//通过后拿剩下百分之50减押金
            hfa.setOtherMoney(new BigDecimal(0.0));
            hfa.setSuspendDay(0);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 7);//业主倒计时
            hfa.setEndDate(calendar.getTime());
            houseFlowApplyMapper.insert(hfa);
            houseService.insertConstructionRecord(hfa);
            House house = houseMapper.selectByPrimaryKey(hfa.getHouseId());
            house.setTaskNumber(house.getTaskNumber() + 1);
            houseMapper.updateByPrimaryKeySelective(house);

            configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "竣工验收申请",
                    String.format(DjConstants.PushMessage.CRAFTSMAN_ALL_FINISHED, house.getHouseName()), "");
            return ServerResponse.createBySuccessMessage("申请验收成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，申请验收失败");
        }
    }
}
