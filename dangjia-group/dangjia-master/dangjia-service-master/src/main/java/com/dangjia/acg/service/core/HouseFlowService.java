package com.dangjia.acg.service.core;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.AllgrabBean;
import com.dangjia.acg.mapper.core.IHouseFlowCountDownTimeMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.design.IHouseStyleTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishConditionMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowCountDownTime;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.design.HouseStyleType;
import com.dangjia.acg.modle.group.Group;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.RewardPunishCondition;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.member.GroupInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
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
    private ConfigMessageService configMessageService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowCountDownTimeMapper houseFlowCountDownTimeMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper;
    @Autowired
    private IRewardPunishConditionMapper rewardPunishConditionMapper;
    @Autowired
    private IHouseStyleTypeMapper houseStyleTypeMapper;
    @Value("${spring.profiles.active}")
    private String active;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private GroupInfoService groupInfoService;
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
     * 抢单列表
     */
    public ServerResponse getGrabList(String userToken, String cityId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            //工匠没有实名认证不应该展示数据
            if (CommonUtil.isEmpty(member.getWorkerTypeId()) || member.getCheckType() != 2 || member.getRealNameState() != 3) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            request.setAttribute(Constants.CITY_ID, cityId);
            List<AllgrabBean> grabList = new ArrayList<>();//返回的任务list
            String workerTypeId = member.getWorkerTypeId();
            /*待抢单*/
            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo(HouseFlow.WORK_TYPE, 2).andEqualTo(HouseFlow.WORKER_TYPE_ID, workerTypeId)
                    .andEqualTo(HouseFlow.CITY_ID, cityId).andNotEqualTo(HouseFlow.STATE, 2);
            List<HouseFlow> hfList = houseFlowMapper.selectByExample(example);
            if (hfList != null)
                for (HouseFlow houseFlow : hfList) {
                    example = new Example(HouseWorker.class);
                    example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId())
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
                    AllgrabBean allgrabBean = new AllgrabBean();
                    example = new Example(HouseFlowCountDownTime.class);
                    example.createCriteria().andEqualTo(HouseFlowCountDownTime.WORKER_ID, member.getId()).andEqualTo(HouseFlowCountDownTime.HOUSE_FLOW_ID, houseFlow.getId());
                    List<HouseFlowCountDownTime> houseFlowDownTimeList = houseFlowCountDownTimeMapper.selectByExample(example);
                    HouseFlowCountDownTime houseFlowCountDownTime = new HouseFlowCountDownTime();
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
                        Date date = this.getCountDownTime(member.getEvaluationScore());
                        houseFlowCountDownTime.setCountDownTime(date);//可抢单时间
                        List<HouseFlowCountDownTime> houseFlowCountDownTimes = houseFlowCountDownTimeMapper.selectByExample(example);
                        if (houseFlowCountDownTimes == null || houseFlowCountDownTimes.size() == 0) {//新增此数据前查询是否已存在，避免重复插入
                            houseFlowCountDownTimeMapper.insert(houseFlowCountDownTime);
                        }
                    }
                    Member mem = memberMapper.selectByPrimaryKey(house.getMemberId());
                    if (mem == null) {
                        continue;
                    }
                    allgrabBean.setWorkerTypeId(workerTypeId);
                    allgrabBean.setHouseFlowId(houseFlow.getId());
                    allgrabBean.setHouseName(house.getHouseName());
                    allgrabBean.setSquare("面积 " + (house.getSquare() == null ? "***" : house.getSquare()) + "m²");//面积
                    allgrabBean.setHouseMember("业主 " + (mem.getNickName() == null ? mem.getName() : mem.getNickName()));//业主名称
                    allgrabBean.setWorkertotal("¥0");//工钱
                    double totalPrice = 0;
                    if (houseFlow.getWorkerType() == 1 && !CommonUtil.isEmpty(house.getStyle())) {//设计师
                        HouseStyleType houseStyleType = houseStyleTypeMapper.getStyleByName(house.getStyle());
                        BigDecimal workPrice = house.getSquare().multiply(houseStyleType.getPrice());//设计工钱
                        allgrabBean.setWorkertotal("¥" + String.format("%.2f", workPrice.doubleValue()));//工钱
                    } else if (houseFlow.getWorkerType() == 2) {
                        allgrabBean.setWorkertotal("¥" + String.format("%.2f", houseFlow.getWorkPrice().doubleValue()));
                    } else {
                        ServerResponse serverResponse = budgetWorkerAPI.getWorkerTotalPrice(request, houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
                        if (serverResponse.isSuccess()) {
                            if (serverResponse.getResultObj() != null) {
                                JSONObject obj = JSONObject.parseObject(serverResponse.getResultObj().toString());
                                totalPrice = Double.parseDouble(obj.getString("totalPrice"));
                            }
                        }
                        allgrabBean.setWorkertotal("¥" + String.format("%.2f", totalPrice));//工钱
                    }

                    allgrabBean.setReleaseTime("时间 " + (houseFlow.getReleaseTime() == null ? "" :
                            DateUtil.getDateString(houseFlow.getReleaseTime().getTime())));//发布时间
                    long countDownTime = houseFlowCountDownTime.getCountDownTime().getTime() - new Date().getTime();//获取倒计时
                    allgrabBean.setCountDownTime(countDownTime);//可接单时间
                    grabList.add(allgrabBean);
                }
            if (grabList.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", grabList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,查询失败");
        }
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
            example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("workerTypeId", workerTypeId);
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
            Member member = (Member) object;
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
                        Date wraprDate = rewardPunishCondition.getEndTime();
                        DateFormat longDateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
                        Date date = new Date();
                        if (date.getTime() < wraprDate.getTime()) {
                            return ServerResponse.createByErrorMessage("您处于平台处罚期内，" + longDateFormat.format(wraprDate) + "以后才能抢单,如有疑问请致电400-168-1231！");
                        }
                    }
                }
            }
            //抢单时间限制
            if (active != null && active.equals("pre")) {
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
            if (member.getWorkerType() > 3) {//其他工人
                if (hf.getPause() == 1) {
                    return ServerResponse.createByErrorMessage("该房子已暂停施工！");
                }
                //持单数
                long num = houseWorkerMapper.grabControl(member.getId());//查询未完工工地
                WorkerType wt = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                if (member.getWorkerType() != 7 && num >= wt.getMethods()) {
                    return ServerResponse.createByErrorMessage("您有工地还未完工,暂不能抢单！");
                }

                //暂时注释
                List<HouseWorker> hwlist = houseWorkerMapper.grabOneDayOneTime(member.getId());
                if (hwlist.size() > 0) {
                    return ServerResponse.createByErrorMessage("每天只能抢一单哦！");
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
     * 放弃此单
     *
     * @param userToken   用户登录信息
     * @param houseFlowId 房子ID
     * @return 是否成功
     */
    public ServerResponse setGiveUpOrder(String userToken, String houseFlowId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId()).andEqualTo(HouseWorker.HOUSE_ID, hf.getHouseId());
            List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的
            HouseWorker houseWorker = hwList.get(0);
            if (member.getWorkerType() == 3) {//大管家
                if (hf.getWorkType() == 3 && hf.getSupervisorStart() == 0) {//已抢单待支付，并且未开工(无责取消)
                    hf.setWorkType(2);//抢s单状态更改为待抢单
                    hf.setReleaseTime(new Date());//set发布时间
                    houseFlowMapper.updateByPrimaryKeySelective(hf);
                    houseWorker.setWorkType(7);//抢单状态改为（7抢单后放弃）
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                } else {
                    if (hf.getSupervisorStart() != 0) {//已开工的状态不可放弃
                        return ServerResponse.createBySuccessMessage("您已确认开工，不可放弃！");
                    } else {
                        if (hf.getWorkType() == 4) {//已支付（有责取消）
                            hf.setWorkType(2);//抢单状态更改为待抢单
                            hf.setReleaseTime(new Date());//set发布时间
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
                            houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                        }
                    }
                }
                House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
                configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "大管家放弃", String.format(DjConstants.PushMessage.STEWARD_ABANDON, house.getHouseName()), "");
            } else {//普通工匠
                if (hf.getWorkType() == 3) {//已抢单待支付(无责取消)
                    hf.setWorkType(2);//抢单状态更改为待抢单
                    hf.setReleaseTime(new Date());//set发布时间
                    houseFlowMapper.updateByPrimaryKeySelective(hf);
                } else {
                    if (hf.getWorkSteta() != 3 || hf.getWorkSteta() != 0) {//已开工的状态不可放弃
                        return ServerResponse.createByErrorMessage("您已在施工，不可放弃！");
                    } else {
                        if (hf.getWorkType() == 4) {//已支付（有责取消）
                            hf.setWorkType(2);//抢单状态更改为待抢单
                            hf.setReleaseTime(new Date());//set发布时间
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
                //修改此单为放弃
                houseWorker.setWorkType(7);//抢单状态改为（7抢单后放弃）
                houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hf.getWorkerTypeId());
                configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "工匠放弃", String.format(DjConstants.PushMessage.CRAFTSMAN_ABANDON, house.getHouseName(), workerType.getName()), "");


                HouseFlow houseFlowDgj = houseFlowMapper.getHouseFlowByHidAndWty(hf.getHouseId(), 3);
                configMessageService.addConfigMessage(null, "gj", houseFlowDgj.getWorkerId(), "0", "工匠放弃",
                        String.format(DjConstants.PushMessage.STEWARD_CRAFTSMAN_TWO_ABANDON, house.getHouseName()), "5");

            }
            return ServerResponse.createBySuccessMessage("放弃成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，放弃失败！");
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
            Member member = (Member) object;
            member = memberMapper.selectByPrimaryKey(member.getId());
            if (member == null) {
                return ServerResponse.createByErrorMessage("用户不存在");
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
            house.setModifyDate(new Date());
            houseMapper.updateByPrimaryKeySelective(house);
            houseFlow.setSupervisorStart(1);//大管家进度改为已开工
            houseFlow.setModifyDate(new Date());
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
//            HouseFlow nextHF = houseFlowMapper.getNextHouseFlow(houseFlow.getHouseId());//根据当前工序查下一工序
//            if (nextHF != null) {
//                nextHF.setWorkType(2);//把下一个工种弄成待抢单
//                nextHF.setReleaseTime(new Date());//发布时间
//                houseFlowMapper.updateByPrimaryKeySelective(nextHF);
//                //通知下一个工种 抢单
//                configMessageService.addConfigMessage(null, "gj", "wtId" + nextHF.getWorkerTypeId() + nextHF.getCityId(), "0", "新的装修订单", DjConstants.PushMessage.SNAP_UP_ORDER, "4");
//            }
            configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "大管家开工", DjConstants.PushMessage.STEWARD_CONSTRUCTION, "");

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
}
