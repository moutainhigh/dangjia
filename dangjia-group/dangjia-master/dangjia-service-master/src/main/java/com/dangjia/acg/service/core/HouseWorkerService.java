package com.dangjia.acg.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.MessageAPI;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.basics.WorkerGoodsAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HomePageBean;
import com.dangjia.acg.dto.house.HouseChatDTO;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.menu.IMenuConfigurationMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.group.Group;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.menu.MenuConfiguration;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.member.GroupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private RedisClient redisClient;
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
    private MessageAPI messageAPI;
    @Value("${spring.profiles.active}")
    private String active;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private GroupInfoService groupInfoService;

    /**
     * 根据工人id查询所有房子任务
     */
    public ServerResponse queryWorkerHouse(String userToken) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        List list = houseWorkerMapper.queryWorkerHouse(accessToken.getMemberId());
        return ServerResponse.createBySuccess("ok", list);
    }

    /**
     * 换人
     */
    public ServerResponse setChangeWorker(String userToken, String houseWorkerId) {
        try {
            HouseWorker houseWorker = houseWorkerMapper.selectByPrimaryKey(houseWorkerId);
            if (houseWorker.getWorkType() == 6) {
                return ServerResponse.createByErrorMessage("已支付不能换人,请联系当家装修");
            }
            houseWorker.setWorkType(2);//被业主换
            houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);

            HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
            String workerId = houseWorker.getWorkerId();
            houseFlow.setWorkerId("");
            houseFlow.setWorkType(2);
            houseFlow.setReleaseTime(new Date());//重新发布
            houseFlow.setRefuseNumber(houseFlow.getRefuseNumber() + 1);
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            if (!CommonUtil.isEmpty(workerId)) {
                House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
                configMessageService.addConfigMessage(null, "gj", workerId, "0", "业主换人提醒",
                        String.format(DjConstants.PushMessage.STEWARD_REPLACE, house.getHouseName()), "5");

                HouseFlow houseFlowDgj = houseFlowMapper.getHouseFlowByHidAndWty(houseFlow.getHouseId(), 3);
                if (houseFlowDgj != null && !CommonUtil.isEmpty(houseFlowDgj.getWorkerId())) {
                    configMessageService.addConfigMessage(null, "gj", houseFlowDgj.getWorkerId(), "0", "业主换人提醒",
                            String.format(DjConstants.PushMessage.STEWARD_CRAFTSMAN_TWO_REPLACE, house.getHouseName()), "5");
                }
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("换人失败");
        }
    }

    /**
     * 抢单
     */
    public ServerResponse setWorkerGrab(HttpServletRequest request,String userToken, String cityId, String houseFlowId) {
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
                //开始建群
                Group group = new Group();
                group.setHouseId(house.getId());
                group.setUserId(house.getMemberId());
                groupInfoService.addGroup(request, group, worker.getId(), "大管家");

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
//            String houseId=houseFlowMapper.selectByPrimaryKey(houseFlowId).getHouseId();
//            String memberId = houseMapper.selectByPrimaryKey(houseId).getMemberId();
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
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), EventStatus.USER_TOKEN_ERROR.getDesc());
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
            return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "获取我的界面信息失败！");
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
     * applyType   0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
     */
    public ServerResponse setHouseFlowApply(String userToken, Integer applyType, String houseFlowId, Integer suspendDay,
                                            String applyDec, String imageList, String houseFlowId2) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        Example example = new Example(HouseFlowApply.class);
        example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, houseFlowId).andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                .andCondition(" member_check in (1,3)").andEqualTo(HouseFlowApply.PAY_STATE, 1);
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.selectByExample(example);
        if (houseFlowApplyList.size() > 0) {
            if (applyType == 4) {
                for (HouseFlowApply hfa : houseFlowApplyList) {
                    hfa.setMemberCheck(2);//不通过不通过
                    houseFlowApplyMapper.updateByPrimaryKeySelective(hfa);
                }
            } else {
                HouseFlowApply houseFlowApply = houseFlowApplyList.get(0);
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
                if (houseFlowApply.getStartDate().before(new Date()) && houseFlowApply.getEndDate().after(new Date())) {
                    return ServerResponse.createByErrorMessage("工序(" + workerType.getName() + ")处于停工期间!");
                }
            }

        }

        //暂停施工
        if (applyType != 4) {//每日开工不需要验证
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
            if (house != null) {
                if (hf.getPause() == 1) {
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hf.getWorkerTypeId());
                    return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "该工序（" + workerType.getName() + "）已暂停施工,请勿提交申请！");
                }
                if (house.getPause() != null) {

                    if (house.getPause() == 1) {
                        return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "该房子已暂停施工,请勿提交申请！");
                    }
                }
            }
        }
        if (applyType == 5) {   //大管家有人巡查放工序id 其它巡查放管家自己工序id
            HouseFlow hf2 = houseFlowMapper.selectByPrimaryKey(houseFlowId2);
            return this.setHouseFlowApply(applyType, houseFlowId2, hf2.getWorkerId(), suspendDay, applyDec,
                    imageList);
        } else {
            return this.setHouseFlowApply(applyType, houseFlowId, worker.getId(), suspendDay, applyDec,
                    imageList);
        }
    }

    /**
     * 0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4每日开工,5有效巡查,6无人巡查,7追加巡查
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setHouseFlowApply(Integer applyType, String houseFlowId, String workerId, Integer suspendDay, String applyDec,
                                            String imageList) {

        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);//工序
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
        try {
            if (applyType == 3) {
                if (houseFlow.getPause() == 1) {
                    return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）已暂停施工,请勿重复申请");
                }
                if (houseFlow.getWorkSteta() == 3) {
                    return ServerResponse.createByErrorMessage("该工序（" + workerType.getName() + "）待交底请勿发起停工申请");
                }
            }
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());//查询房子
            HouseFlow supervisorHF = houseFlowMapper.getHouseFlowByHidAndWty(houseFlow.getHouseId(), 3);//大管家的hf
            //****针对老工地管家兼容巡查拿钱和验收拿钱***//
            if (supervisorHF.getPatrolMoney() == null || supervisorHF.getCheckMoney() == null ||
                    supervisorHF.getPatrolMoney().compareTo(new BigDecimal(0)) == 0
                    || supervisorHF.getCheckMoney().compareTo(new BigDecimal(0)) == 0) {
                this.calculateSup(supervisorHF);
            }
            /*提交申请进行控制*/
//            List<ChangeOrder> changeOrderList = changeOrderMapper.unCheckOrder(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
//            if (changeOrderList.size() > 0) {
//                return ServerResponse.createByErrorMessage("该工种（" + workerType.getName() + "）有未处理变更单,通知管家处理");
//            }
            //包括所有申请 和 巡查
            List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(houseFlowId, applyType, workerId, new Date());
            if (applyType != 6 && applyType != 7 && houseFlowApplyList.size() > 0) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "您今日已提交过此申请,请勿重复提交！");
            }

            /*待审核申请*/
            if (applyType < 4) {
                List<HouseFlowApply> hfaList = houseFlowApplyMapper.checkPendingApply(houseFlowId, workerId);
                if (hfaList.size() > 0) {
                    return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "您有待审核的申请,请联系管家业主审核后再提交");
                }
            }

            Member worker = memberMapper.selectByPrimaryKey(workerId);//查询对应的工人
            WorkDeposit workDeposit = workDepositMapper.selectByPrimaryKey(house.getWorkDepositId());//结算比例表

            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());

            HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
            hfa.setHouseFlowId(houseFlowId);//工序id
            hfa.setWorkerId(workerId);//工人id
            hfa.setWorkerTypeId(worker.getWorkerTypeId());//工种id
            hfa.setWorkerType(worker.getWorkerType());//工种类型
            hfa.setHouseId(houseFlow.getHouseId());//房子id
            hfa.setApplyType(applyType);//申请类型0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5巡查,6无人巡查
            hfa.setApplyDec(applyDec);//描述
            hfa.setSuspendDay(suspendDay);//申请停工天数
            hfa.setApplyMoney(new BigDecimal(0));//申请得钱
            hfa.setSupervisorMoney(new BigDecimal(0));
            hfa.setOtherMoney(new BigDecimal(0));
            hfa.setMemberCheck(0);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
            hfa.setSupervisorCheck(0);//大管家审核状态0未审核，1审核通过，2审核不通过
            hfa.setPayState(0);//是否付款

            //********************发申请，计算可得钱和积分等*****************//
            BigDecimal workPrice = new BigDecimal(0);
            BigDecimal haveMoney = new BigDecimal(0);
            if (hwo != null) {
                workPrice = hwo.getWorkPrice();//工钱
                haveMoney = hwo.getHaveMoney();
            }
            BigDecimal limitpay = workPrice.multiply(workDeposit.getLimitPay());//每日完工得到钱的上限

            //***每日完工申请***//
            if (applyType == 0) {//每日完工申请
                if (workDeposit.getEverydayPay().compareTo(limitpay.subtract(haveMoney)) <= 0) {//每日上限减已获 大于等于 100
                    hfa.setApplyMoney(workDeposit.getEverydayPay());
                } else {
                    hfa.setApplyMoney(new BigDecimal(0));
                }
                hfa.setOtherMoney((workPrice).subtract(haveMoney).subtract(hfa.getApplyMoney()));
                hfa.setApplyDec("我是" + workerType.getName() + ",我今天已经完工了");//描述
                houseFlowApplyMapper.insert(hfa);
                houseService.insertConstructionRecord(hfa);

                //***阶段完工申请***//
            } else if (applyType == 1) {
                //算阶段完工所有的钱
                BigDecimal stage = workPrice.multiply((workDeposit.getStagePay().add(workDeposit.getLimitPay()))).subtract(haveMoney);
                if (stage.compareTo(new BigDecimal(0)) > 0) {
                    hfa.setApplyMoney(stage); //stagePay阶段完工比例百分比
                } else {
                    hfa.setApplyMoney(new BigDecimal(0));
                }
                hfa.setOtherMoney(workPrice.subtract(haveMoney).subtract(hfa.getApplyMoney()));
//                hfa.setApplyDec("业主您好，我是大管家，我已验收了" + workerType.getName() + "的阶段完工");//描述
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
                //***整体完工申请***//
            } else if (applyType == 2) {
                BigDecimal retentionMoney =hwo.getRetentionMoney() == null ? new BigDecimal(0) : hwo.getRetentionMoney();//滞留金
                BigDecimal deductPrice =hwo.getDeductPrice() == null ? new BigDecimal(0) : hwo.getDeductPrice();//评价积分扣除的钱
                BigDecimal alsoMoney = new BigDecimal(workPrice.doubleValue()-haveMoney.doubleValue()-retentionMoney.doubleValue()-deductPrice.doubleValue());
                hfa.setApplyMoney(alsoMoney);
//                hfa.setApplyDec("业主您好，我是大管家，我已验收了" + workerType.getName() + "的整体完工");//描述
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
                //***停工申请***//*
            } else if (applyType == 3) {
                Date today = new Date();
                Date end = new Date(today.getTime() + 24 * 60 * 60 * 1000 * suspendDay);
                hfa.setStartDate(today);
                hfa.setEndDate(end);
                hfa.setMemberCheck(0);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
                hfa.setPayState(1);//标记为新停工申请
                houseFlowApplyMapper.insert(hfa);
                houseService.insertConstructionRecord(hfa);
                houseFlow.setPause(1);//0:正常；1暂停；
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);//发停工申请默认修改施工状态为暂停

                configMessageService.addConfigMessage(null, "gj", supervisorHF.getWorkerId(), "0", "工匠申请停工",
                        String.format(DjConstants.PushMessage.STEWARD_CRAFTSMEN_APPLY_FOR_STOPPAGE, house.getHouseName()), "5");
                return ServerResponse.createBySuccessMessage("工匠申请停工（" + workerType.getName() + "）操作成功");

            } else if (applyType == 4) {
                if (active != null && active.equals("pre")) {
                    String s2 = DateUtil.getDateString2(new Date().getTime()) + " 12:00:00";//当天12点
                    Date lateDate = DateUtil.toDate(s2);
                    Date newDate2 = new Date();//当前时间
                    long downTime = newDate2.getTime() - lateDate.getTime();//对比12点
                    if (downTime > 0) {
                        return ServerResponse.createByErrorMessage("请在当天12点之前开工,您已超过开工时间！");
                    }
                }
                hfa.setApplyDec("我是" + workerType.getName() + ",我今天已经开工了");//描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                houseFlow.setPause(0);//0:正常；1暂停；
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);//发每日开工将暂停状态改为正常
                houseFlowApplyMapper.insert(hfa);
                houseService.insertConstructionRecord(hfa);
                return ServerResponse.createBySuccessMessage("操作成功");
            } else if (applyType == 5) {//有人巡
                hfa.setApplyDec("业主您好,我是" + workerType.getName() + ",大管家已经巡查了");//描述
                //描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                Example example2 = new Example(HouseFlowApply.class);
                example2.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, houseFlow.getHouseId())
                        .andEqualTo(HouseFlowApply.WORKER_TYPE_ID, worker.getWorkerTypeId()).andEqualTo(HouseFlowApply.APPLY_TYPE, 5);
                List<HouseFlowApply> hfalist = houseFlowApplyMapper.selectByExample(example2);
                //工人houseflow
                if (hfalist.size() < houseFlow.getPatrol()) {//该工种没有巡查够，每次要拿钱
                    Member supervisor = memberMapper.selectByPrimaryKey(supervisorHF.getWorkerId());//找出大管家
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
                    houseFlowApplyMapper.insert(hfa);
                    houseService.insertConstructionRecord(hfa);
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
                } else {
                    houseFlowApplyMapper.insert(hfa);
                    houseService.insertConstructionRecord(hfa);
//                    return ServerResponse.createBySuccessMessage("工序（" + workerType.getName() + "）巡查成功");
                }

            } else if (applyType == 6) {//无人巡查
                hfa.setApplyDec("业主您好，我是您的大管家，我已经巡查了工地，现场情况如下");//描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                houseFlowApplyMapper.insert(hfa);
                houseService.insertConstructionRecord(hfa);
            } else if (applyType == 7) {//追加巡查
                hfa.setApplyDec("业主您好，我是您的大管家，我已经巡查了工地，现场情况如下");//描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                houseFlowApplyMapper.insert(hfa);
                houseService.insertConstructionRecord(hfa);

            }
            //保存巡查图片,验收节点图片等信息
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
                            houseFlowApplyImage.setImageUrl(anImageArr);
                            houseFlowApplyImage.setImageType(imageType);//图片类型 0：材料照片；1：进度照片；2:现场照片；3:其他
                            houseFlowApplyImage.setImageTypeName(imageObj.getString("imageTypeName"));//图片类型名称 例如：材料照片；进度照片
                            houseFlowApplyImageMapper.insert(houseFlowApplyImage);
                        }
                    }
                }
            }
            //* 0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4每日开工,5有效巡查,6无人巡查,7追加巡查
            String msg;
            switch (applyType) {
                case 0:
                    msg = "每日完工申请成功";
                    //每日完工
                    houseFlowApplyService.checkWorker(hfa.getId(), false);
                    break;
                case 1:
                    msg = "阶段完工申请成功";
                    break;
                case 2:
                    msg = "整体完工申请成功";
                    break;
                case 3:
                    msg = "停工申请成功";
                    break;
                case 4:
                    msg = "工作开始啦！";
                    break;
                default:
                    msg = "巡查成功";
                    break;

            }
            return ServerResponse.createBySuccessMessage("工序（" + workerType.getName() + "）" + msg);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("工序（" + workerType.getName() + "）巡查失败");
        }
    }


    /*算管家巡查验收拿钱*/
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
            /*// 重新调整施工顺序
            int sort = 4;
            // 先确定已开工顺序
            List<HouseFlow> hflist = houseFlowMapper.getForCheckMoney(houseFlow.getHouseId());
            for (HouseFlow hf : hflist) {
                if (hf.getWorkType() > 1) {
                    hf.setSort(sort);
                    sort++;
                    houseFlowMapper.updateByPrimaryKeySelective(hf);
                }
            }
            houseFlow.setSort(sort);// 本次提前位置
            sort++;*/
            houseFlow.setWorkType(2);
            houseFlow.setReleaseTime(new Date());//发布时间
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            /*
            for (HouseFlow hf : hflist) {
                if (hf.getWorkType() < 2) {
                    hf.setSort(sort);
                    sort++;
                    houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                }
            }
            */
            return ServerResponse.createBySuccessMessage("提前进场成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，提前进场失败");
        }
    }

    /**
     * 根据工匠id查询施工列表
     */
    public ServerResponse getHouseFlowList(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            List<HouseWorker> listHouseWorker = houseWorkerMapper.getAllHouseWorker(worker.getId());
            List<Map<String, Object>> listMap = new ArrayList<>();//返回通讯录list
            if (listHouseWorker != null) {
                for (HouseWorker houseWorker : listHouseWorker) {
                    HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
                    if (houseFlow == null || (worker.getWorkerType() != null && worker.getWorkerType() != 3 && houseFlow.getWorkType() == 2))
                        continue;
                    Map<String, Object> map = new HashMap<>();
                    map.put("houseFlowId", houseFlow.getId());//任务id
                    House house = houseMapper.selectByPrimaryKey(houseWorker.getHouseId());//查询房产信息.
                    if (house == null) continue;
                    Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                    if (member == null) continue;
                    //房产信息
                    map.put("price", "¥0.00");//价格
                    HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
                    if(houseWorkerOrder!=null){
                        if(houseWorkerOrder.getWorkPrice()==null){houseWorkerOrder.setWorkPrice(new BigDecimal(0));}
                        if(houseWorkerOrder.getRepairPrice()==null){houseWorkerOrder.setRepairPrice(new BigDecimal(0));}
                        BigDecimal remain = houseWorkerOrder.getWorkPrice().add(houseWorkerOrder.getRepairPrice());
                        map.put("price", "¥" + (String.format("%.2f", remain.doubleValue())));//价格
                    }
                    map.put("houseName", house.getHouseName());//地址
                    map.put("releaseTime", houseFlow.getReleaseTime() == null ? "" : houseFlow.getReleaseTime().getTime());//发布时间
                    map.put("square", (house.getSquare() == null ? "0" : house.getSquare()) + "m²");//面积
                    map.put("memberName", member.getName());//业主姓名
                    if (houseFlow.getPause() == 0) {//正常施工
                        map.put("isItNormal", "正常施工");
                    } else {
                        map.put("isItNormal", "暂停施工");//暂停施工
                    }
                    List<HouseFlowApply> todayStartList = houseFlowApplyMapper.getTodayStartByHouseId(house.getId(), new Date());//查询今日开工记录
                    if (todayStartList == null || todayStartList.size() == 0) {//没有今日开工记录
                        map.put("houseIsStart", "今日未开工");//是否正常施工
                    } else {
                        map.put("houseIsStart", "今日已开工");//是否正常施工
                    }
                    List<HouseFlowApply> supervisorCheckList = houseFlowApplyMapper.getSupervisorCheckList(house.getId());//查询所有待大管家审核
                    map.put("taskNumber", supervisorCheckList.size());//任务数量
                    listMap.add(map);
                }
            }
            if (listMap.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), EventStatus.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("获取施工列表成功", listMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，获取施工列表失败！");
        }
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
            Member worker = (Member) object;
            List<HouseWorker> listHouseWorker = houseWorkerMapper.getAllHouseWorker(worker.getId());
            for (HouseWorker houseWorker : listHouseWorker) {
                HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
                if (houseFlow != null && houseFlow.getId().equals(houseFlowId)) {//选中的任务isSelect改为1
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
