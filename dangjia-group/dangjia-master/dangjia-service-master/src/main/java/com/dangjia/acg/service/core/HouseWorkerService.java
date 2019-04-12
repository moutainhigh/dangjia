package com.dangjia.acg.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.dangjia.acg.dto.core.ConstructionByWorkerIdBean;
import com.dangjia.acg.dto.core.HomePageBean;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.matter.IWorkerEverydayMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.modle.matter.WorkerEveryday;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
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
    private IWorkerEverydayMapper workerEverydayMapper;
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
    private IChangeOrderMapper changeOrderMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private HouseFlowApplyService houseFlowApplyService;

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
    public ServerResponse setWorkerGrab(String userToken, String cityId, String houseFlowId) {
        try {
            ServerResponse serverResponse = houseFlowService.setGrabVerification(userToken, cityId, houseFlowId);
            if (!serverResponse.isSuccess())
                return serverResponse;
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            if (houseFlow.getWorkType() == 3) {
                return ServerResponse.createByErrorMessage("该订单已被抢");
            }
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            houseFlow.setGrabNumber(houseFlow.getGrabNumber() + 1);
            houseFlow.setWorkType(3);//等待支付
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            if (worker.getWorkerType() == 1) {//设计师
                house.setDesignerOk(4);//有设计抢单待业主支付
                houseMapper.updateByPrimaryKeySelective(house);
            }
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
            return ServerResponse.createBySuccessMessage("抢单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("抢单失败");
        }
    }

    /**
     * 根据工人id查询自己的施工界面
     */
    public ServerResponse getConstructionByWorkerId(String userToken, String cityId) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            if (worker == null) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), EventStatus.USER_TOKEN_ERROR.getDesc());
            }
            if (worker.getWorkerType() == null) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "请上传资料");
            }
            if (worker.getWorkerType() == 1 || worker.getWorkerType() == 2) {//设计师/精算师不支持直接返回
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "设计师/精算师请在后台管理中查看施工详情");
            }

            HouseWorker hw;
            List<HouseWorker> houseWorkerList = houseWorkerMapper.getAllHouseWorker(worker.getId());//查询所有已抢待支付和已支付
            if (houseWorkerList.size() == 0) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "您暂无施工中的记录,快去接单吧！");
            }
            List<HouseWorker> selectList = houseWorkerMapper.getDetailHouseWorker(worker.getId());//查询选中

            if (selectList.size() == 0) {//没有选中的任务
                hw = houseWorkerList.get(0);
                hw.setIsSelect(1);//设置成默认
                houseWorkerMapper.updateByPrimaryKeySelective(hw);
            } else {
                hw = selectList.get(0);
            }

            List<HouseFlow> hfList = houseFlowMapper.getAllFlowByHouseId(hw.getHouseId());
            House house = houseMapper.selectByPrimaryKey(hw.getHouseId());//查询房产信息
            HouseFlow hf = houseFlowMapper.getByWorkerTypeId(hw.getHouseId(), hw.getWorkerTypeId());//查询自己的任务状态
            ConstructionByWorkerIdBean bean = new ConstructionByWorkerIdBean();
            bean.setWorkerType(worker.getWorkerType() == 3 ? 0 : 1);
            bean.setHouseFlowId(hf.getId());
            bean.setIfBackOut(1);
            if (hf.getPause() == 1) {//已暂停
                bean.setIfBackOut(2);
            }
            String houseName = house.getHouseName();
            bean.setHouseName(houseName);
            HouseWorkerOrder hwo = houseWorkerOrderMapper.getHouseWorkerOrder(hw.getHouseId(), worker.getId(), hw.getWorkerTypeId());
            if (hwo == null) {
                bean.setAlreadyMoney(new BigDecimal(0));//已得钱
                bean.setAlsoMoney(new BigDecimal(0));//还可得钱
            } else {
                BigDecimal alsoMoney = (hwo.getWorkPrice() == null ? new BigDecimal(0) :
                        hwo.getWorkPrice()).subtract(hwo.getHaveMoney() == null ? new BigDecimal(0) : hwo.getHaveMoney());//还可得钱
                bean.setAlreadyMoney(hwo.getHaveMoney());//已得钱
                bean.setAlsoMoney(alsoMoney);//还可得钱
            }
            bean.setBigList(getBigList(userToken, cityId, house, worker, hf));//添加菜单到返回体中
            List<String> promptList = new ArrayList<>();//消息提示list
            List<ConstructionByWorkerIdBean.ButtonListBean> buttonList = new ArrayList<>();
            if (worker.getWorkerType() == 3) {//大管家
                Member houseMember = memberMapper.selectByPrimaryKey(house.getMemberId());//业主
                if (houseMember != null) {
                    bean.setHouseMemberName(houseMember.getNickName());//业主名称
                    bean.setHouseMemberPhone(houseMember.getMobile());//业主电话
                    bean.setUserId(houseMember.getId());//
                } else {
                    bean.setHouseMemberName("");
                    bean.setHouseMemberPhone("");
                    bean.setUserId("");//
                }
                Long allPatrol = houseFlowApplyMapper.getCountValidPatrolByHouseId(house.getId(), null);
                bean.setAllPatrol("总巡查次数" + (allPatrol == null ? 0 : allPatrol));
                List<ConstructionByWorkerIdBean.WokerFlowListBean> workerFlowList = new ArrayList<>();
                boolean houseIsStart = false;
                if (hf.getWorkerType() == 3 && hf.getWorkType() == 4 && hf.getSupervisorStart() == 1) {//当业主支付大管家费用并且确认开工之后之后才出现
                    for (HouseFlow hfl : hfList) {
                        if ((hfl.getWorkerType() == 1 || hfl.getWorkerType() == 2 || hfl.getWorkerType() == 3)) {
                            //当业主支付大管家费用并且确认开工之后之后才出现
                            continue;
                        }
                        ConstructionByWorkerIdBean.WokerFlowListBean wfr = new ConstructionByWorkerIdBean.WokerFlowListBean();
                        Example example = new Example(HouseWorker.class);
                        example.createCriteria().andEqualTo("houseId",
                                house.getId()).andEqualTo("workerTypeId",
                                hfl.getWorkerTypeId()).andEqualTo("workType", 6);
                        List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//根据房子id和工匠type查询房子对应的工人
                        HouseWorker houseWorker = new HouseWorker();
                        if (hwList.size() > 0) {
                            houseWorker = hwList.get(0);
                        }
                        Member worker2 = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hfl.getWorkerTypeId());
                        wfr.setHouseFlowId(hfl.getId());//进程id
                        wfr.setHouseFlowtype(hfl.getWorkerType());//进程类型
                        wfr.setHouseFlowName(workerType == null ? "" : workerType.getName());//大进程名
                        wfr.setWorkerName(worker2 == null ? "" : worker2.getName());//工人名称
                        wfr.setWorkerId(worker2 == null ? "" : worker2.getId());//工人id
                        wfr.setWorkerPhone(worker2 == null ? "" : worker2.getMobile());//工人手机
                        wfr.setPatrolSecond("巡查次数" +
                                houseFlowApplyMapper.getCountValidPatrolByHouseId(house.getId(), worker2 == null ? "0" : worker2.getId()));//巡查次数
                        wfr.setPatrolStandard("巡查标准" + hfl.getPatrol());//巡查标准
                        HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(house.getId(), worker2 == null ? "" : worker2.getId(), new Date());//查询今日开工记录
                        if (todayStart == null) {//没有今日开工记录
                            wfr.setIsStart(0);//今日是否开工0:否；1：是；
                        } else {
                            wfr.setIsStart(1);//今日是否开工0:否；1：是；
                            houseIsStart = true;
                        }
                        String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                                String.format(DjConstants.GJPageAddress.GJMANAGERISOK, userToken, cityId, houseName) + "&houseId=" + house.getId() + "&houseFlowId=" + hfl.getId();
                        wfr.setDetailUrl(url);//进程详情链接
                        HouseFlowApply houseFlowApp = houseFlowApplyMapper.checkHouseFlowApply(hfl.getId(), worker2 == null ? "" : worker2.getId());//根据工种任务id和工人id查询此工人待审核
                        if (houseFlowApp != null && houseFlowApp.getApplyType() == 1) {//阶段完工申请
                            wfr.setButtonTitle("阶段完工申请");//按钮提示
                            promptList.add("我是" + (workerType == null ? "" : workerType.getName()) + "工" + (worker2 == null ? "" : worker2.getName() + ",我提交了阶段完工申请"));
                            wfr.setState(4);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                        } else if (houseFlowApp != null && houseFlowApp.getApplyType() == 2) {
                            wfr.setButtonTitle("整体完工申请");//按钮提示
                            wfr.setState(6);
                            promptList.add("我是" + (workerType == null ? "" : workerType.getName()) + "工" + (worker2 == null ? "" : worker2.getName() + ",我提交了整体完工申请"));
                        } else if (hfl.getWorkType() < 2) {//未发布工种抢单
                            wfr.setButtonTitle("提前进场");//按钮提示
                            wfr.setState(0);
                        } else if (hfl.getWorkType() < 4) {//待抢单和已抢单
                            wfr.setButtonTitle("正在进场");//按钮提示
                            wfr.setState(1);
                        } else if (hfl.getWorkSteta() == 3) {
                            wfr.setButtonTitle("去交底");
                            wfr.setState(2);
                        } else if ((hfl.getWorkType() == 4 && hfl.getWorkSteta() == 0) || hfl.getWorkSteta() == 4) {
                            wfr.setButtonTitle("施工中");
                            wfr.setState(3);
                        } else if (hfl.getWorkSteta() == 1) {
                            wfr.setButtonTitle("已阶段完工");
                            wfr.setState(4);
                        } else if (hfl.getWorkSteta() == 5) {
                            wfr.setButtonTitle("收尾施工中");
                            wfr.setState(5);
                        } else if (hfl.getWorkSteta() == 2) {
                            wfr.setButtonTitle("已整体完工");
                            wfr.setState(6);
                        }
                        if (houseFlowApp != null && houseFlowApp.getApplyType() == 3) {
                            wfr.setButtonTitle("停工申请");//按钮提示
                            promptList.add("我是" + (workerType == null ? "" : workerType.getName()) + "工" + (worker2 == null ? "" : worker2.getName() + ",我提交了停工申请"));
                        }
                        if (hfl.getPause() == 1) {
                            wfr.setButtonTitle("已停工");//按钮提示
                        }
                        workerFlowList.add(wfr);
                    }
                }
                bean.setHouseIsStart(houseIsStart ? "今日已开工" : "今日未开工");
                bean.setWokerFlowList(workerFlowList);
                int count = 0;
                for (HouseWorker houseWorker : houseWorkerList) {//循环所有订单任务
                    List<HouseFlowApply> supervisorCheckList = houseFlowApplyMapper.getSupervisorCheckList(houseWorker.getHouseId());//查询所有待大管家审核
                    count += supervisorCheckList.size();
                }
                bean.setTaskNumber(count);//总任务数量
                bean.setIfDisclose(0);
                if (hf.getWorkType() == 3) {//如果是已抢单待支付。则提醒业主支付
                    bean.setIfBackOut(0);
                    promptList.add("请联系业主支付您的大管家费用");
                }
                //查询是否全部整体完工
                List<HouseFlow> checkFinishList = houseFlowMapper.checkAllFinish(hf.getHouseId(), hf.getId());
                //查询是否今天已经上传过巡查
                List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApplyBy56(hf.getHouseId(), new Date());
                if (hf.getSupervisorStart() == 0) {//已开工之后都是巡查工地；1：巡查工地2：申请业主验收；3:确认开工
                    List<HouseFlow> listStart = houseFlowMapper.getHouseIsStart(hf.getHouseId());
                    if (listStart.size() > 0) {
                        hf.setSupervisorStart(1);//改为开工状态(兼容老数据)
                        houseFlowMapper.updateByPrimaryKeySelective(hf);
                        buttonList.add(getButton("巡查工地", 1));
                        bean.setIfBackOut(1);
                    } else if (hf.getWorkType() == 4) {//支付之后显示按钮
                        buttonList.add(getButton("确认开工", 3));
                        bean.setIfBackOut(1);
                    }
                } else if (checkFinishList.size() == 0) {//所有工种都整体完工，申请业主验收
                    if (house.getHaveComplete() == 1) {
                        promptList.add("该房子已竣工!");
                    } else {
                        HouseFlowApply houseFlowApp = houseFlowApplyMapper.checkSupervisorApply(hf.getId(), worker.getId());//查询大管家是否有验收申请
                        if (houseFlowApp == null) {//没有发验收申请
                            buttonList.add(getButton("申请业主验收", 2));
                        } else {
                            promptList.add("您已提交业主验收申请，请耐心等待业主审核！");
                        }
                    }
                } else if (houseFlowApplyList != null && houseFlowApplyList.size() != 0) {//今日已提交过巡查
                    List<HouseFlowApply> hfalistApp7 = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 7, worker.getId(), new Date());
                    if (hfalistApp7 == null || hfalistApp7.size() == 0) {
                        buttonList.add(getButton("追加巡查", 4));
                    } else {
                        promptList.add("今日已巡查");
                    }
                } else {
                    buttonList.add(getButton("巡查工地", 1));
                    bean.setIfBackOut(1);
                }
            } else {
                List<HouseFlowApply> earliestTimeList = houseFlowApplyMapper.getEarliestTimeHouseApply(house.getId(), worker.getId());
                HouseFlowApply earliestTime = null;
                if (earliestTimeList.size() > 0) {
                    earliestTime = earliestTimeList.get(0);
                }
                HouseFlowApply checkFlowApp = houseFlowApplyMapper.checkHouseFlowApply(hf.getId(), worker.getId());//根据工种任务id和工人id查询此工人待审核
                Long suspendDay = houseFlowApplyMapper.getSuspendApply(house.getId(), worker.getId());//根据房子id和工人id查询暂停天数
                Long everyEndDay = houseFlowApplyMapper.getEveryDayApply(house.getId(), worker.getId());//根据房子id和工人id查询每日完工申请天数
                long totalDay = 0;
                if (earliestTime != null) {
                    Date EarliestDay = earliestTime.getCreateDate();//最早开工时间
                    Date newDate = new Date();
                    totalDay = DateUtil.daysofTwo(EarliestDay, newDate);//计算当前时间隔最早开工时间相差多少天
                    if (suspendDay != null) {
                        totalDay = totalDay - suspendDay;
                        if (totalDay <= 0) totalDay = 0;
                    }
                }
                bean.setTotalDay("总开工天数" + totalDay);
                bean.setEveryDay("每日完工天数" + (everyEndDay == null ? "0" : everyEndDay));
                bean.setSuspendDay("暂停天数" + (suspendDay == null ? "0" : suspendDay));
                if (hw.getWorkType() == 1) {
                    bean.setIfDisclose(0);
                } else if (hf.getWorkSteta() == 3) {
                    bean.setIfDisclose(1);
                } else {
                    bean.setIfDisclose(2);
                }
                //房产信息
                HouseWorker supervisorWorker = houseWorkerMapper.getHwByHidAndWtype(hf.getHouseId(), 3);//查询大管家的
                Member workerSup = memberMapper.selectByPrimaryKey(supervisorWorker == null ? "" : supervisorWorker.getWorkerId());//查询大管家
                bean.setSupervisorName(workerSup == null ? "无" : workerSup.getName());//大管家名字
                bean.setSupervisorPhone(workerSup == null ? "无" : workerSup.getMobile());
                bean.setUserId(workerSup == null ? "无" : workerSup.getId());
                bean.setSupervisorEvation("积分 " + (workerSup == null ? "0.00" : workerSup.getEvaluationScore()));//大管家积分

                Long supervisorCountOrder = houseWorkerMapper.getCountOrderByWorkerId(workerSup == null ? "" : workerSup.getId());

                bean.setSupervisorCountOrder("总单数 " + (supervisorCountOrder == null ? "0" : supervisorCountOrder));//大管家总单数
                bean.setSupervisorPraiseRate("好评率 " + ((workerSup != null ? workerSup.getPraiseRate().multiply(new BigDecimal(100)) : 0) + "%"));//大管家好评率
                if (hf.getWorkType() == 3) {//如果是已抢单待支付。则提醒业主支付
                    promptList.add("请联系业主支付您的工匠费用");
                    bean.setIfBackOut(0);
                } else if (hf.getPause() == 1) {
                    promptList.add("您已停工");
                    bean.setIfBackOut(2);
                } else if (hf.getWorkSteta() == 1) {
                    promptList.add("您已阶段完工");
                    bean.setIfBackOut(2);
                }

                if (hf.getWorkSteta() == 2) {
                    promptList.add("您已整体完工");
                    bean.setIfBackOut(2);
                } else if (hf.getWorkType() == 4) {
                    if (hf.getWorkSteta() == 3) {//待交底
                        buttonList.add(getButton("找大管家交底", 1));
                        bean.setIfBackOut(1);
                    } else if (worker.getWorkerType() == 4) {//如果是拆除，只有整体完工
                        setDisplayState(hf, promptList, buttonList, checkFlowApp, true);
                    } else {//已交底
                        bean.setFootMessageTitle("");//每日开工事项
                        bean.setFootMessageDescribe("");//每日开工事项
                        HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(house.getId(), worker.getId(), new Date());//查询今日开工记录
                        List<ConstructionByWorkerIdBean.BigListBean.ListMapBean> workerEverydayList = new ArrayList<>();
                        if (todayStart == null) {//没有今日开工记录
                            buttonList.add(getButton("今日开工", 2));
                            List<WorkerEveryday> listWorDay = workerEverydayMapper.getWorkerEverydayList(1);//事项类型  1 开工事项 2 完工事项
                            for (WorkerEveryday day : listWorDay) {
                                ConstructionByWorkerIdBean.BigListBean.ListMapBean listMapBean = new ConstructionByWorkerIdBean.BigListBean.ListMapBean();
                                listMapBean.setName(day.getName());
                                workerEverydayList.add(listMapBean);
                            }
                            bean.setFootMessageTitle("今日开工任务");//每日开工事项
                            bean.setFootMessageDescribe("（每日十二点前今日开工）");//每日开工事项
                        } else {
                            List<HouseFlowApply> allAppList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 2, worker.getId(), new Date());//查询今天是否已提交整体完工
                            List<HouseFlowApply> stageAppList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 1, worker.getId(), new Date());//查询今天是否已提交阶段完工
                            List<HouseFlowApply> flowAppList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 0, worker.getId(), new Date());//查询是否已提交今日完工
                            if (allAppList.size() > 0) {
                                promptList.add("今日已申请整体完工");
                            } else if (stageAppList.size() > 0) {
                                promptList.add("今日已申请阶段完工");
                            } else if (flowAppList != null && flowAppList.size() > 0) {//已提交今日完工
                                promptList.add("今日已完工");
                            } else {
                                buttonList.add(getButton("今日完工", 3));
                                List<WorkerEveryday> listWorDay = workerEverydayMapper.getWorkerEverydayList(2);//事项类型  1 开工事项 2 完工事项
                                for (WorkerEveryday day : listWorDay) {
                                    ConstructionByWorkerIdBean.BigListBean.ListMapBean listMapBean = new ConstructionByWorkerIdBean.BigListBean.ListMapBean();
                                    listMapBean.setName(day.getName());
                                    workerEverydayList.add(listMapBean);
                                }
                                bean.setFootMessageTitle("今日完工任务");//每日开工事项
                                bean.setFootMessageDescribe("");//每日开工事项
                            }

                            if (hf.getWorkSteta() == 1) {
                                setDisplayState(hf, promptList, buttonList, checkFlowApp, true);
                            } else {
                                setDisplayState(hf, promptList, buttonList, checkFlowApp, false);
                            }
                        }
                        bean.setWorkerEverydayList(workerEverydayList);//每日完工事项
                    }
                }
            }
            bean.setPromptList(promptList);
            bean.setButtonList(buttonList);
            return ServerResponse.createBySuccess("获取施工列表成功！", bean);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取施工出错！");
        }
    }

    /**
     * 获取施工页面菜单
     *
     * @param userToken userToken
     * @param cityId    cityId
     * @param house     当前房子
     * @param worker    当前用户
     * @return 菜单列表
     */
    private List<ConstructionByWorkerIdBean.BigListBean> getBigList(String userToken, String cityId, House house, Member worker, HouseFlow hf) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<ConstructionByWorkerIdBean.BigListBean> bigList = new ArrayList<>();
        //添加工程资料菜单
        String[] edNames = {"施工图", "精算", "工价工艺", "工地记录", "巡查二维码", "通讯录"};
        int[] edTypes = {0, 0, 0, 0, 1, 0};//0=不需原生定位处理  1=需原生定位处理
        int[] edShowTypes = {0, 0, 0, 0, 2, 0};//1：大管家有的，2：工匠有的，0：都有
        String[] edImages = {"artisan_25.png", "artisan_26.png", "artisan_27.png", "artisan_29.png", "erweima.png", "artisan_30.png"};
        String[] edUrls = {DjConstants.GJPageAddress.PROJECTDRAWINGLIST, DjConstants.GJPageAddress.GJJINGSUANLIST, DjConstants.GJPageAddress.GJPRICE,
                DjConstants.GJPageAddress.PROJECTRECORD, DjConstants.GJPageAddress.QRCODE, DjConstants.GJPageAddress.PROJECTADRESSLIST};//H5路由
        ConstructionByWorkerIdBean.BigListBean bigListBean = new ConstructionByWorkerIdBean.BigListBean();
        bigListBean.setName("工程资料");
        List<ConstructionByWorkerIdBean.BigListBean.ListMapBean> listMap = new ArrayList<>();
        String houseFlowId = houseFlowMapper.selectHouseFlowId(house.getId(), worker.getWorkerTypeId());
        for (int i = 0; i < edNames.length; i++) {
            if (worker.getWorkerType() == 3) {
                if (edShowTypes[i] == 2) continue;
            } else {
                if (edShowTypes[i] == 1) continue;
            }
            String name = edNames[i];
            ConstructionByWorkerIdBean.BigListBean.ListMapBean mapBean = new ConstructionByWorkerIdBean.BigListBean.ListMapBean();
            mapBean.setName(name);
            mapBean.setUrl(configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + getH5Url(edUrls[i], userToken, cityId, name)
                    + "&houseId=" + house.getId() + "&houseFlowId=" + houseFlowId);
            mapBean.setImage(address + "gongjiang/" + edImages[i]);
            mapBean.setType(edTypes[i]);
            listMap.add(mapBean);
        }
        bigListBean.setListMap(listMap);
        bigList.add(bigListBean);
        //添加其他菜单
        String[] qtNames = {"货品管理", "材料用量", "收货", "人工变更", "补人工", "要补退记录", "服务管理"};
        int[] qtTypes = {0, 0, 0, 0, 0, 0, 0};
        int[] qtShowTypes = {2, 1, 0, 1, 2, 0, 1};//1：大管家有的，2：工匠有的，0：都有
        String[] qtImages = {"artisan_22.png", "artisan_37@2x.png", "artisan_38@2x.png", "artisan_54@3x.png", "artisan_54@3x.png", "artisan_24.png", "fuwuguanli@2x.png"};
        String[] qtUrls = {DjConstants.GJPageAddress.HPMANAGE, DjConstants.GJPageAddress.MATERIALCONSUMPTION, DjConstants.GJPageAddress.RECIVEGOODSLIST,
                DjConstants.GJPageAddress.CHANGEARTIFICIAL,
                DjConstants.GJPageAddress.ADDARTIFICIAL, DjConstants.YZPageAddress.REFUNDLIST, DjConstants.GJPageAddress.SERVICEMANAGE};//H5路由
        bigListBean = new ConstructionByWorkerIdBean.BigListBean();
        bigListBean.setName("材料人工");
        listMap = new ArrayList<>();
        for (int i = 0; i < qtNames.length; i++) {
            //整体完工后"材料人工"只留下"要补退记录"
            if (hf.getWorkSteta() == 2) {
                if (!qtNames[i].equals("要补退记录")) {
                    continue;
                }
            }
            ConstructionByWorkerIdBean.BigListBean.ListMapBean mapBean = new ConstructionByWorkerIdBean.BigListBean.ListMapBean();
            if (worker.getWorkerType() == 3) {
                if (qtShowTypes[i] == 2) continue;
            } else {
                if (qtShowTypes[i] == 1) continue;
            }
            String name = qtNames[i];
            if (worker.getWorkerType() == 3) {
                mapBean.setUrl(configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + getH5Url(qtUrls[i], userToken, cityId, name) +
                        "&houseId=" + house.getId() + "&houseFlowId=" + houseFlowId + "&roleType=2");//管家
            } else {
                mapBean.setUrl(configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + getH5Url(qtUrls[i], userToken, cityId, name) +
                        "&houseId=" + house.getId() + "&houseFlowId=" + houseFlowId + "&roleType=3");//工匠
            }
            mapBean.setName(name);
            mapBean.setImage(address + "gongjiang/" + qtImages[i]);
            mapBean.setType(qtTypes[i]);
            listMap.add(mapBean);
        }
        bigListBean.setListMap(listMap);
        bigList.add(bigListBean);
        return bigList;
    }

    /**
     * 显示当前需要申请的状态
     *
     * @param hf           自己的任务状态
     * @param promptList   消息
     * @param buttonList   按钮
     * @param checkFlowApp 此工人待审核申请
     * @param isShow       true 整体完工 false 阶段完工
     */
    private void setDisplayState(HouseFlow hf, List<String> promptList, List<ConstructionByWorkerIdBean.ButtonListBean> buttonList, HouseFlowApply checkFlowApp, boolean isShow) {
        if (isShow) {//整体完工
            if (checkFlowApp == null) {
                if (hf.getWorkSteta() != 2) {
                    buttonList.add(getButton("申请整体完工", 5));
                }
            } else if (checkFlowApp.getSupervisorCheck() == 0) {
                promptList.add("已申请整体完工,等待大管家审核");
            } else if (checkFlowApp.getSupervisorCheck() == 1) {
                promptList.add("大管家已审核您的整体完工,待业主审核");
            }
        } else {//阶段完工申请
            if (checkFlowApp == null) {
                if (hf.getWorkSteta() != 1 && hf.getWorkSteta() != 2) {
                    buttonList.add(getButton("申请阶段完工", 4));
                }
            } else if (checkFlowApp.getSupervisorCheck() == 0) {
                promptList.add("已申请阶段完工,等待大管家审核");
            } else if (checkFlowApp.getSupervisorCheck() == 1) {
                promptList.add("大管家已审核您的阶段完工,待业主审核");
            }
        }
    }

    /**
     * 获取按钮对象
     *
     * @param name 按钮名称
     * @param type 按钮类型
     * @return 按钮对象
     */
    private ConstructionByWorkerIdBean.ButtonListBean getButton(String name, int type) {
        ConstructionByWorkerIdBean.ButtonListBean buttonListBean = new ConstructionByWorkerIdBean.ButtonListBean();
        buttonListBean.setButtonType(type);
        buttonListBean.setButtonTypeName(name);
        return buttonListBean;
    }

    /**
     * 公用拼接H5地址
     *
     * @param url       路由
     * @param userToken userToken
     * @param cityId    cityId
     * @param name      name
     * @return 拼接好的URL
     */
    private String getH5Url(String url, String userToken, String cityId, String name) {
        String address = String.format(url, userToken, cityId, name);
        return address;
    }


    /**
     * 获取我的界面
     *
     * @param userToken 用户登录信息
     * @param cityId    城市ID
     * @return 我的页面
     */
    public ServerResponse getMyHomePage(String userToken, String cityId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            worker = memberMapper.selectByPrimaryKey(worker.getId());
            if (worker == null) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), EventStatus.USER_TOKEN_ERROR.getDesc());
            }
            HomePageBean homePageBean = new HomePageBean();
            homePageBean.setWorkerId(worker.getId());
            homePageBean.setIoflow(CommonUtil.isEmpty(worker.getHead()) ? null : address + worker.getHead());
            homePageBean.setWorkerName(worker.getName());
            homePageBean.setEvaluation(worker.getEvaluationScore());
            homePageBean.setFavorable(worker.getPraiseRate() == null ? "0.00%" : worker.getPraiseRate().multiply(new BigDecimal(100)) + "%");
            StringBuilder stringBuffer = new StringBuilder();
            if (worker.getIsCrowned() != 1) {
                if (Double.parseDouble(worker.getEvaluationScore().toString()) > 90) {
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
            String[] names = {"我的任务", "我的银行卡", "提现记录",
                    "接单记录", "奖罚记录", "工价工艺", "我的邀请码", "帮助中心"};
//            "工艺要求", "工匠报价",
            String[] urls = {DjConstants.GJPageAddress.MYTASK, DjConstants.YZPageAddress.BANKCARDALREADYADD, DjConstants.GJPageAddress.CASHRECORD,
                    DjConstants.GJPageAddress.ORDERRECORD, DjConstants.GJPageAddress.JIANGFALIST, DjConstants.GJPageAddress.GJPRICE, DjConstants.GJPageAddress.MYINVITECODE, DjConstants.GJPageAddress.HELPCENTER};
//            , DjConstants.GJPageAddress.PROCESSREQUIRE,DjConstants.GJPageAddress.GJPRICE
//            "artisan_35.png","artisan_36.png",
            String[] imageUrls = {"artisan_40.png", "artisan_41.png", "artisan_39.png",
                    "artisan_61.png", "artisan_69.png", "artisan_36.png", "artisan_42.png", "artisan_60.png"};
            List<HomePageBean.ListBean> list = new ArrayList<>();
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                HomePageBean.ListBean listBean = new HomePageBean.ListBean();
                listBean.setName(name);
                listBean.setUrl(configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + getH5Url(urls[i], userToken, cityId, name));
                listBean.setImageUrl(address + "gongjiang/" + imageUrls[i]);
                listBean.setType(urls[i].equals("") ? 1 : 0);
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
    public ServerResponse getHouseFlowApply(String userToken, String houseFlowApplyId) {
        HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
        return ServerResponse.createBySuccess(houseFlowApply);
    }

    /**
     * 提交审核、停工
     *
     * @Deprecated
     */
    public ServerResponse setHouseFlowApply(String userToken, Integer applyType, String houseFlowId, Integer suspendDay,
                                            String applyDec, String imageList, String houseFlowId2) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member worker = accessToken.getMember();

        Example example = new Example(HouseFlowApply.class);
        example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, houseFlowId).andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                .andEqualTo(HouseFlowApply.MEMBER_CHECK, 1).andEqualTo(HouseFlowApply.PAY_STATE, 1);
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.selectByExample(example);
        if (houseFlowApplyList.size() > 0) {
            HouseFlowApply houseFlowApply = houseFlowApplyList.get(0);
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
            if (houseFlowApply.getStartDate().before(new Date()) && houseFlowApply.getEndDate().after(new Date())) {
                return ServerResponse.createByErrorMessage("工序(" + workerType.getName() + ")处于停工期间!");
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
        try {
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);//工序
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
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
            List<ChangeOrder> changeOrderList = changeOrderMapper.unCheckOrder(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
            if (changeOrderList.size() > 0) {
                return ServerResponse.createByErrorMessage("该工种（" + workerType.getName() + "）有未处理变更单,通知管家处理");
            }

            //包括所有申请 和 巡查
            List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getTodayHouseFlowApply(houseFlowId, applyType, workerId, new Date());
            if (houseFlowApplyList.size() > 0) {
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
                hfa.setApplyDec("我是" + workerType.getName() + ",我已申请了阶段完工");//描述
                hfa.setSupervisorMoney(supervisorHF.getCheckMoney());//管家得相应验收收入
                //增加倒计时系统自动审核时间
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 3);
                hfa.setEndDate(calendar.getTime());
                // 阶段完工,管家审核通过工匠完工申请 @link checkOk()
                houseFlowApplyMapper.insert(hfa);
                configMessageService.addConfigMessage(null, "gj", supervisorHF.getWorkerId(), "0", "阶段完工申请",
                        String.format(DjConstants.PushMessage.STEWARD_APPLY_FINISHED, house.getHouseName(), workerType.getName()), "5");
                //***整体完工申请***//
            } else if (applyType == 2) {
                hfa.setApplyMoney(workPrice.subtract(haveMoney));
                hfa.setApplyDec("我是" + workerType.getName() + ",我已申请整体完工");//描述
                hfa.setSupervisorMoney(supervisorHF.getCheckMoney());//管家得相应验收收入
                //增加倒计时系统自动审核时间
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 3);
                hfa.setEndDate(calendar.getTime());
                houseFlowApplyMapper.insert(hfa);

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

                houseFlow.setPause(1);//0:正常；1暂停；
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);//发停工申请默认修改施工状态为暂停

                configMessageService.addConfigMessage(null, "gj", supervisorHF.getWorkerId(), "0", "工匠申请停工",
                        String.format(DjConstants.PushMessage.STEWARD_CRAFTSMEN_APPLY_FOR_STOPPAGE, house.getHouseName()), "5");
                return ServerResponse.createBySuccessMessage("工匠申请停工（" + workerType.getName() + "）操作成功");

            } else if (applyType == 4) {
                String s2 = DateUtil.getDateString2(new Date().getTime()) + " 12:00:00";//当天12点
                Date lateDate = DateUtil.toDate(s2);
                Date newDate2 = new Date();//当前时间
                Long downTime = newDate2.getTime() - lateDate.getTime();//对比12点
                if (downTime > 0) {
                    return ServerResponse.createByErrorMessage("请在当天12点之前开工,您已超过开工时间！");
                }
                hfa.setApplyDec("我是" + workerType.getName() + ",我今天已经开工了");//描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                houseFlow.setPause(0);//0:正常；1暂停；
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);//发每日开工将暂停状态改为正常
                houseFlowApplyMapper.insert(hfa);
                return ServerResponse.createBySuccessMessage("操作成功");
            } else if (applyType == 5) {//有人巡
                hfa.setApplyDec("业主您好,我是" + workerType.getName() + ",大管家已经巡查了");//描述
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

                    if (supervisor.getHaveMoney() == null) {
                        supervisor.setHaveMoney(new BigDecimal(0));
                    }
                    if (supervisor.getSurplusMoney() == null) {
                        supervisor.setSurplusMoney(new BigDecimal(0));
                    }
                    BigDecimal haveMoneys = supervisor.getHaveMoney().add(supervisorHF.getPatrolMoney());
                    BigDecimal surplusMoneys =supervisor.getSurplusMoney().add(supervisorHF.getPatrolMoney());
                    supervisor.setHaveMoney(haveMoneys);
                    supervisor.setSurplusMoney(surplusMoneys);
                    memberMapper.updateByPrimaryKeySelective(supervisor);


                    //记录到管家流水
                    WorkerDetail workerDetail = new WorkerDetail();
                    workerDetail.setName(workerType.getName()+"巡查收入");
                    workerDetail.setWorkerId(supervisor.getId());
                    workerDetail.setWorkerName(supervisor.getName());
                    workerDetail.setHouseId(hfa.getHouseId());
                    workerDetail.setMoney(supervisorHF.getPatrolMoney());
                    workerDetail.setState(0);//进钱
                    workerDetail.setHaveMoney(supervisorHWO.getHaveMoney());
                    workerDetail.setHouseWorkerOrderId(supervisorHWO.getId());
                    workerDetail.setApplyMoney(haveMoneys);
                    workerDetail.setWalletMoney(supervisor.getHaveMoney());
                    workerDetailMapper.insert(workerDetail);
                } else {
                    houseFlowApplyMapper.insert(hfa);
                    return ServerResponse.createBySuccessMessage("工序（" + workerType.getName() + "）巡查成功");
                }

            } else if (applyType == 6) {//无人巡查
                hfa.setApplyDec("业主您好，我已经巡查了工地，工地情况如图");//描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                houseFlowApplyMapper.insert(hfa);
            } else if (applyType == 7) {//追加巡查
                hfa.setApplyDec("业主您好，我已经巡查了工地，工地情况如图");//描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                houseFlowApplyMapper.insert(hfa);
            }
            /**********保存巡查图片,验收节点图片等信息************/
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
                        technologyRecord.setState(1);//已验收
                        technologyRecord.setModifyDate(new Date());
                        technologyRecordMapper.insert(technologyRecord);
                    } else {
                        String[] imageArr = imageUrl.split(",");
                        for (int j = 0; j < imageArr.length; j++) {
                            HouseFlowApplyImage houseFlowApplyImage = new HouseFlowApplyImage();
                            houseFlowApplyImage.setHouseFlowApplyId(hfa.getId());
                            houseFlowApplyImage.setImageUrl(imageArr[j]);
                            houseFlowApplyImage.setImageType(imageType);//图片类型 0：材料照片；1：进度照片；2:现场照片；3:其他
                            houseFlowApplyImage.setImageTypeName(imageObj.getString("imageTypeName"));//图片类型名称 例如：材料照片；进度照片
                            houseFlowApplyImageMapper.insert(houseFlowApplyImage);
                        }
                    }
                }
            }
            /*每日完工自动审核*/
            if (applyType == 0) {//每日完工
                houseFlowApplyService.checkWorker(hfa.getId());
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
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
    public ServerResponse getAdvanceInAdvance(String userToken, String houseFlowId) {
        try {
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            if (houseFlow.getWorkType() > 1) {
                return ServerResponse.createByErrorMessage("提前进场失败");
            } else if (houseFlow.getWorkType() == null) {
                houseFlow.setWorkType(0);//
            }
            // 重新调整施工顺序
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
            sort++;
            houseFlow.setWorkType(2);
            houseFlow.setReleaseTime(new Date());//发布时间
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);

            for (HouseFlow hf : hflist) {
                if (hf.getWorkType() < 2) {
                    hf.setSort(sort);
                    sort++;
                    houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                }
            }
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
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            List<HouseWorker> listHouseWorker = houseWorkerMapper.getAllHouseWorker(worker.getId());
            List<Map<String, Object>> listMap = new ArrayList<>();//返回通讯录list
            if (listHouseWorker != null) {
                for (HouseWorker houseWorker : listHouseWorker) {
                    HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
                    Map<String, Object> map = new HashMap<>();
                    map.put("houseFlowId", houseFlow.getId());//任务id
                    if (houseFlow == null || (worker.getWorkerType() != null && worker.getWorkerType() != 3 && houseFlow.getWorkType() == 2))
                        continue;
                    House house = houseMapper.selectByPrimaryKey(houseWorker.getHouseId());//查询房产信息.
                    if (house == null) continue;
                    Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                    if (member == null) continue;
                    //房产信息
                    map.put("houseName", house.getHouseName());//地址
                    map.put("releaseTime", houseFlow.getReleaseTime() == null ? "" : houseFlow.getReleaseTime().getTime());//发布时间
                    map.put("square", (house.getSquare() == null ? "0" : house.getSquare()) + "m²");//面积
                    map.put("memberName", member.getName());//业主姓名
                    map.put("price", "¥" + (houseFlow.getWorkPrice() == null ? "0" : String.format("%.2f", houseFlow.getWorkPrice().doubleValue())));//价格
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
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            List<HouseWorker> listHouseWorker = houseWorkerMapper.getAllHouseWorker(worker.getId());
            for (HouseWorker houseWorker : listHouseWorker) {
                HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
                if (houseFlow.getId().equals(houseFlowId)) {//选中的任务isSelect改为1
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
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
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
            houseFlowApplyMapper.insert(hfa);
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
