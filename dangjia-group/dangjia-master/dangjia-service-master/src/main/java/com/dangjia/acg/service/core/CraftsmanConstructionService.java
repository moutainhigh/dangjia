package com.dangjia.acg.service.core;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.ConstructionByWorkerIdBean;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.matter.IWorkerEverydayMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.menu.IMenuConfigurationMapper;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.matter.WorkerEveryday;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.menu.MenuConfiguration;
import com.dangjia.acg.util.HouseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Ruking.Cheng
 * @descrilbe 新版工匠施工相关实现
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/22 2:34 PM
 */
@Service
public class CraftsmanConstructionService {
    @Autowired
    private IMenuConfigurationMapper iMenuConfigurationMapper;
    @Autowired
    private RedisClient redisClient;//缓存
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
    private ConfigUtil configUtil;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IWorkerEverydayMapper workerEverydayMapper;

    /**
     * 获取施工页面
     *
     * @param userToken userToken
     * @return 施工页面信息
     */
    public ServerResponse getConstructionView(HttpServletRequest request, String userToken) {
        ConstructionByWorkerIdBean bean = new ConstructionByWorkerIdBean();//公用返回体
        Object object = getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        if (worker.getWorkerType() == null) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "请上传资料");
        }
        object = getHouseWorker(bean, worker.getId());
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        HouseWorker hw = (HouseWorker) object;
        House house = houseMapper.selectByPrimaryKey(hw.getHouseId());//查询房产信息
        if (house == null) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "房产信息不存在");
        }
        HouseFlow hf = houseFlowMapper.getByWorkerTypeId(hw.getHouseId(), hw.getWorkerTypeId());//查询自己的任务状态
        if (hf == null) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "没有查到该任务");
        }
        bean.setHouseId(house.getId());
        bean.setHouseName(house.getHouseName());
        switch (worker.getWorkerType()) {
            case 1://设计师
                return getDesignerBean(request, bean, hw, house, hf);
            case 2://精算师
                return getActuariesBean(request, bean, hw, worker, house, hf);
            case 3://大管家
                return getHousekeeperBean(request, bean, hw, worker, house, hf);
            default://工匠
                return getCraftsmanBean(request, bean, hw, worker, house, hf);
        }

    }

    /**
     * 设计师
     */
    private ServerResponse getDesignerBean(HttpServletRequest request, ConstructionByWorkerIdBean bean, HouseWorker hw, House house, HouseFlow hf) {
        if (!getApiVersion(request)) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "设计师/精算师请在后台管理中查看施工详情");
        }
        bean.setWorkerType(2);//0:大管家；1：工匠；2：设计师；3：精算师
        bean.setHouseFlowId(hf.getId());
        bean.setDecorationType(house.getDecorationType());
        bean.setDesignerOk(house.getDesignerOk());
        setMoney(bean, hw);
        Member houseMember = memberMapper.selectByPrimaryKey(house.getMemberId());//业主
        if (houseMember != null) {
            bean.setHouseMemberName(houseMember.getNickName());//业主名称
            bean.setHouseMemberPhone(houseMember.getMobile());//业主电话
            bean.setUserId(houseMember.getId());//
        }
        setMenus(bean, house, hf);
        Map<String, Object> dataMap = HouseUtil.getDesignDatas(house);
        bean.setDataList((List<Map<String, Object>>) dataMap.get("dataList"));
        List<ConstructionByWorkerIdBean.ButtonListBean> buttonList = new ArrayList<>();
        if (house.getDecorationType() != 2 && house.getDesignerOk() == 1) {
            buttonList.add(getButton("去量房", 2));
        } else {
            switch (house.getDesignerOk()) {
                case 1://1已支付-设计师待量房
                case 9://9量房图发给业主
                    buttonList.add(getButton("上传平面图", 3));
                    break;
                case 6://6平面图审核不通过（NG，可编辑平面图）
                    buttonList.add(getButton("修改平面图", 3));
                    break;
                case 7://7通过平面图待发施工图（OK，可编辑施工图）
                    buttonList.add(getButton("上传施工图", 4));
                    break;
                case 8://8施工图片审核不通过（NG，可编辑施工图）
                    buttonList.add(getButton("修改施工图", 4));
                    break;
            }
        }
        bean.setButtonList(buttonList);
        return ServerResponse.createBySuccess("获取施工列表成功！", bean);
    }

    /**
     * 判断
     *
     * @param request
     * @return
     */
    private boolean getApiVersion(HttpServletRequest request) {
        if (!CommonUtil.isEmpty(request.getParameter("apiVersion"))) {
            try {
                int apiVersion = Integer.parseInt(request.getParameter("apiVersion"));
                if (apiVersion >= 132) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    /**
     * 精算师
     */
    private ServerResponse getActuariesBean(HttpServletRequest request, ConstructionByWorkerIdBean bean, HouseWorker hw, Member worker, House house, HouseFlow hf) {
        if (!getApiVersion(request)) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "设计师/精算师请在后台管理中查看施工详情");
        }
        bean.setWorkerType(3);//0:大管家；1：工匠；2：设计师；3：精算师
        bean.setHouseFlowId(hf.getId());
        setMoney(bean, hw);
        Member houseMember = memberMapper.selectByPrimaryKey(house.getMemberId());//业主
        if (houseMember != null) {
            bean.setHouseMemberName(houseMember.getNickName());//业主名称
            bean.setHouseMemberPhone(houseMember.getMobile());//业主电话
            bean.setUserId(houseMember.getId());//
        }
        setMenus(bean, house, hf);
        Map<String, Object> dataMap = HouseUtil.getBudgetDatas(house);
        bean.setDataList((List<Map<String, Object>>) dataMap.get("dataList"));
        List<ConstructionByWorkerIdBean.ButtonListBean> buttonList = new ArrayList<>();
        if (house.getDecorationType() == 2 && house.getDesignerOk() != 3) {
            buttonList.add(getButton("上传设计", 4));
        }
        bean.setButtonList(buttonList);
        return ServerResponse.createBySuccess("获取施工列表成功！", bean);
    }

    /**
     * 大管家
     */
    private ServerResponse getHousekeeperBean(HttpServletRequest request, ConstructionByWorkerIdBean bean, HouseWorker hw, Member worker, House house, HouseFlow hf) {
        bean.setWorkerType(0);//0:大管家；1：工匠；2：设计师；3：精算师
        bean.setHouseFlowId(hf.getId());
        setMoney(bean, hw);
        Member houseMember = memberMapper.selectByPrimaryKey(house.getMemberId());//业主
        if (houseMember != null) {
            bean.setHouseMemberName(houseMember.getNickName());//业主名称
            bean.setHouseMemberPhone(houseMember.getMobile());//业主电话
            bean.setUserId(houseMember.getId());//
        }
        Long allPatrol = houseFlowApplyMapper.countPatrol(house.getId(), null);
        bean.setAllPatrol("总巡查次数:" + (allPatrol == null ? 0 : allPatrol));
        setMenus(bean, house, hf);
        List<String> promptList = new ArrayList<>();//消息提示list
        List<ConstructionByWorkerIdBean.ButtonListBean> buttonList = new ArrayList<>();
        List<ConstructionByWorkerIdBean.WokerFlowListBean> workerFlowList = new ArrayList<>();
        boolean houseIsStart = false;
        //当业主支付大管家费用并且确认开工之后之后才出现
        if (hf.getWorkerType() == 3 && hf.getWorkType() == 4 && hf.getSupervisorStart() == 1) {
            Example example = new Example(HouseFlow.class);
            example.createCriteria()
                    .andEqualTo(HouseFlow.STATE, 0)
                    .andEqualTo(HouseFlow.HOUSE_ID, hw.getHouseId())
                    .andNotEqualTo(HouseFlow.WORKER_TYPE, 1)//排除设计师
                    .andNotEqualTo(HouseFlow.WORKER_TYPE, 2)//排除精算师
                    .andNotEqualTo(HouseFlow.WORKER_TYPE, 3);//排除大管家
            example.orderBy(HouseFlow.SORT).asc();
            List<HouseFlow> hfList = houseFlowMapper.selectByExample(example);//查询该房产下的工序
            for (HouseFlow hfl : hfList) {
                ConstructionByWorkerIdBean.WokerFlowListBean wfr = new ConstructionByWorkerIdBean.WokerFlowListBean();
                example = new Example(HouseWorker.class);
                example.createCriteria()
                        .andEqualTo(HouseWorker.HOUSE_ID, house.getId())
                        .andEqualTo(HouseWorker.WORKER_TYPE_ID, hfl.getWorkerTypeId())
                        .andEqualTo(HouseWorker.WORK_TYPE, 6);
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
                wfr.setPatrolSecond("巡查次数" + houseFlowApplyMapper.countPatrol(house.getId(), worker2 == null ? "0" : worker2.getWorkerTypeId()));//工序巡查次数
                wfr.setPatrolStandard("巡查标准" + hfl.getPatrol());//巡查标准
                HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(house.getId(), worker2 == null ? "" : worker2.getId(), new Date());//查询今日开工记录
                if (todayStart == null) {//没有今日开工记录
                    wfr.setIsStart(0);//今日是否开工0:否；1：是；
                } else {
                    wfr.setIsStart(1);//今日是否开工0:否；1：是；
                    houseIsStart = true;
                }
                String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                        String.format(DjConstants.GJPageAddress.GJMANAGERISOK, house.getHouseName())
                        + "&houseId=" + house.getId() + "&houseFlowId=" + hfl.getId();
                wfr.setDetailUrl(url);//进程详情链接
                HouseFlowApply houseFlowApp = houseFlowApplyMapper.checkHouseFlowApply(hfl.getId(), worker2 == null ? "" : worker2.getId());//根据工种任务id和工人id查询此工人待审核
                if (houseFlowApp != null && houseFlowApp.getApplyType() == 1) {//阶段完工申请
                    wfr.setButtonTitle("阶段完工申请");//按钮提示
                    promptList.add("我是" + (workerType == null ? "" : workerType.getName()) + "工" +
                            (worker2 == null ? "" : worker2.getName() + ",我提交了阶段完工申请"));
                    wfr.setState(4);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                } else if (houseFlowApp != null && houseFlowApp.getApplyType() == 2) {
                    wfr.setButtonTitle("整体完工申请");//按钮提示
                    wfr.setState(6);
                    promptList.add("我是" + (workerType == null ? "" : workerType.getName()) + "工" +
                            (worker2 == null ? "" : worker2.getName() + ",我提交了整体完工申请"));
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
                } else if (hfl.getWorkSteta() == 2 || hfl.getWorkSteta() == 6) {
                    if(hfl.getWorkSteta() == 2) {
                        wfr.setButtonTitle("已整体完工");
                    }else {
                        wfr.setButtonTitle("提前竣工");
                    }
                    wfr.setState(6);
                }
                if (houseFlowApp != null && houseFlowApp.getApplyType() == 3) {
                    wfr.setButtonTitle("停工申请");//按钮提示
                    promptList.add("我是" + (workerType == null ? "" : workerType.getName()) + "工"
                            + (worker2 == null ? "" : worker2.getName() + ",我提交了停工申请"));
                }
                if (hfl.getPause() == 1) {
                    wfr.setButtonTitle("已停工");//按钮提示
                }
                workerFlowList.add(wfr);
            }
        }
        bean.setHouseIsStart(houseIsStart ? "今日已开工" : "今日未开工");
        bean.setWokerFlowList(workerFlowList);
        if (hf.getWorkType() == 3) {//如果是已抢单待支付。则提醒业主支付
            bean.setIfBackOut(0);
            promptList.add("请联系业主支付您的大管家费用");
        }
        //查询是否全部整体完工
        List<HouseFlow> checkFinishList = houseFlowMapper.checkAllFinish(hf.getHouseId(), hf.getId());
        //查询是否提前结束装修
        Example example=new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID,hf.getHouseId()).andGreaterThanOrEqualTo(HouseFlow.WORKER_TYPE,3);
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(example);
        for(HouseFlow h:houseFlows){
            if(h.getWorkSteta()==6){
                checkFinishList.clear();
                break;
            }
        }
        System.out.println(checkFinishList.size());
        //查询是否今天已经上传过巡查
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getTodayPatrol(hf.getHouseId(), new Date());
        if (hf.getSupervisorStart() == 0) {//已开工之后都是巡查工地；1：巡查工地2：申请业主验收；3:确认开工
            List<HouseFlow> listStart = houseFlowMapper.getHouseIsStart(hf.getHouseId());
            if (listStart.size() > 0) {
                hf.setSupervisorStart(1);//改为开工状态(兼容老数据)
                houseFlowMapper.updateByPrimaryKeySelective(hf);
                buttonList.add(getButton("巡查工地", 1));
            } else if (hf.getWorkType() == 4) {//支付之后显示按钮
                buttonList.add(getButton("确认开工", 3));
            }
        } else if (checkFinishList.size() == 0) {//所有工种都整体完工，申请业主验收
            if (house.getHaveComplete() == 1) {
                promptList.add("该房子已竣工!");
                if (getApiVersion(request)) {
                    String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                            "takeMoneyDetailed" +
                            "?title=拿钱明细" +
                            "&houseId=" + house.getId() +
                            "&houseFlowId=" + hf.getId() +
                            "&houseName=" + house.getHouseName();
                    buttonList.add(getButton("查看拿钱明细", url, 0));
                }
            } else {
                HouseFlowApply houseFlowApp = houseFlowApplyMapper.checkSupervisorApply(hf.getId(), worker.getId());//查询大管家是否有验收申请
                if (houseFlowApp == null) {//没有发验收申请
                    buttonList.add(getButton("申请业主验收", 2));
                } else {
                    promptList.add("您已提交业主验收申请，请耐心等待业主审核！");
                }
            }
        } else if (houseFlowApplyList.size() != 0) {//今日已提交过有人巡查
            buttonList.add(getButton("追加巡查", 4));
        } else {
            buttonList.add(getButton("巡查工地", 1));
        }
        bean.setPromptList(promptList);
        bean.setButtonList(buttonList);
        return ServerResponse.createBySuccess("获取施工列表成功", bean);
    }


    /**
     * 工匠
     */
    private ServerResponse getCraftsmanBean(HttpServletRequest request, ConstructionByWorkerIdBean bean, HouseWorker hw, Member worker, House house, HouseFlow hf) {
        bean.setWorkerType(1);//0:大管家；1：工匠；2：设计师；3：精算师
        bean.setHouseFlowId(hf.getId());
        setMoney(bean, hw);
        //房产信息
        HouseWorker supervisorWorker = houseWorkerMapper.getHwByHidAndWtype(hf.getHouseId(), 3);//查询大管家的
        if (supervisorWorker != null) {
            Member workerSup = memberMapper.selectByPrimaryKey(supervisorWorker.getWorkerId());//查询大管家
            if (workerSup != null) {
                bean.setSupervisorName(workerSup.getName());//大管家名字
                bean.setSupervisorPhone(workerSup.getMobile());
                bean.setUserId(workerSup.getId());
                bean.setSupervisorEvation("积分:" + workerSup.getEvaluationScore());//大管家积分
                Long supervisorCountOrder = houseWorkerMapper.getCountOrderByWorkerId(workerSup.getId());
                if (supervisorCountOrder != null)
                    bean.setSupervisorCountOrder("总单数:" + supervisorCountOrder);//大管家总单数
                bean.setSupervisorPraiseRate("好评率:" + workerSup.getPraiseRate().multiply(new BigDecimal(100)) + "%");//大管家好评率
            }
        }
        if (hf.getPause() == 1) {//已暂停  停工有两种情况需要处理
            bean.setIfBackOut(2);
        } else {
            Example example = new Example(HouseFlowApply.class);
            example.createCriteria()
                    .andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, hf.getId())
                    .andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                    .andEqualTo(HouseFlowApply.PAY_STATE, 1);
            List<HouseFlowApply> houseFlowApplies = houseFlowApplyMapper.selectByExample(example);
            if (houseFlowApplies != null && houseFlowApplies.size() > 0) {
                HouseFlowApply hfa = houseFlowApplies.get(0);
                switch (hfa.getMemberCheck()) {
                    case 0://0未审核
                        bean.setIfBackOut(3);//0可放弃；1：申请停工；2：已停工 3 审核中
                        break;
                    case 1://1审核通过
                        Date date = new Date();
                        if (hfa.getStartDate() != null && date.getTime() < hfa.getStartDate().getTime()) {
                            bean.setIfBackOut(3);//0可放弃；1：申请停工；2：已停工 3 审核中
                        } else if (hfa.getEndDate() != null && date.getTime() > hfa.getEndDate().getTime()) {
                            bean.setIfBackOut(1);//0可放弃；1：申请停工；2：已停工 3 审核中
                        } else {
                            bean.setIfBackOut(2);//0可放弃；1：申请停工；2：已停工 3 审核中
                        }
                        break;
                    default://2审核不通过
                        bean.setIfBackOut(1);//0可放弃；1：申请停工；2：已停工 3 审核中
                        break;
                }
            } else {
                bean.setIfBackOut(1);//0可放弃；1：申请停工；2：已停工 3 审核中
            }
        }
        setMenus(bean, house, hf);
        List<String> promptList = new ArrayList<>();//消息提示list
        List<ConstructionByWorkerIdBean.ButtonListBean> buttonList = new ArrayList<>();
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
        if (hf.getWorkType() == 3) {//如果是已抢单待支付。则提醒业主支付
            promptList.add("请联系业主支付您的工匠费用");
            bean.setIfBackOut(0);//0可放弃；1：申请停工；2：已停工 3 审核中
        } else if (hf.getPause() == 1) {
            promptList.add("您已停工");
        } else if (hf.getWorkSteta() == 1) {
            promptList.add("您已阶段完工");
        }
        if (hf.getWorkSteta() == 2 || hf.getWorkSteta() == 6) {
            if(hf.getWorkSteta() == 2) {
                promptList.add("您已整体完工");
            }else {
                promptList.add("该房子已提前结束装修,您的工钱已自动入账！");
            }
            bean.setIfBackOut(2);
            if (getApiVersion(request)) {
                String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                        "takeMoneyDetailed" +
                        "?title=拿钱明细" +
                        "&houseId=" + house.getId() +
                        "&houseFlowId=" + hf.getId() +
                        "&houseName=" + house.getHouseName();
                buttonList.add(getButton("查看拿钱明细", url, 0));
            }
        } else if (hf.getWorkType() == 4) {
            if (hf.getWorkSteta() == 3) {//待交底
                buttonList.add(getButton("找大管家交底", 1));
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
                        bean.setIfBackOut(2);
                    } else if (stageAppList.size() > 0) {
                        promptList.add("今日已申请阶段完工");
                        bean.setIfBackOut(2);
                    } else if (flowAppList != null && flowAppList.size() > 0) {//已提交今日完工
                        promptList.add("今日已完工");
                        bean.setIfBackOut(2);
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
                        if (hf.getWorkSteta() == 1) {
                            setDisplayState(hf, promptList, buttonList, checkFlowApp, true);
                        } else {
                            setDisplayState(hf, promptList, buttonList, checkFlowApp, false);
                        }
                    }
                }
                bean.setWorkerEverydayList(workerEverydayList);//每日完工事项
            }
        }
        bean.setPromptList(promptList);
        bean.setButtonList(buttonList);
        return ServerResponse.createBySuccess("获取施工列表成功", bean);
    }


    /**
     * 设置菜单
     */
    private void setMenus(ConstructionByWorkerIdBean bean, House house, HouseFlow hf) {
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
        List<ConstructionByWorkerIdBean.BigListBean> bigList = new ArrayList<>();
        Example example = new Example(MenuConfiguration.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo(MenuConfiguration.DATA_STATUS, 0)
                .andEqualTo(MenuConfiguration.MENU_TYPE, 0)
                .andIsNull(MenuConfiguration.PARENT_ID);
        menuCondition(bean, criteria);
        example.orderBy(MenuConfiguration.SORT).asc();
        List<MenuConfiguration> menuConfigurations = iMenuConfigurationMapper.selectByExample(example);
        for (MenuConfiguration menuConfiguration : menuConfigurations) {
            ConstructionByWorkerIdBean.BigListBean bigListBean = new ConstructionByWorkerIdBean.BigListBean();
            bigListBean.setName(menuConfiguration.getName());
            List<ConstructionByWorkerIdBean.BigListBean.ListMapBean> listMap = new ArrayList<>();
            example = new Example(MenuConfiguration.class);
            criteria = example.createCriteria()
                    .andEqualTo(MenuConfiguration.DATA_STATUS, 0)
                    .andEqualTo(MenuConfiguration.MENU_TYPE, 0)
                    .andEqualTo(MenuConfiguration.PARENT_ID, menuConfiguration.getId());
            if (hf.getWorkSteta() == 2) {//完工了屏蔽完工禁止显示的
                criteria.andEqualTo(MenuConfiguration.SHOW_TYPE, 1);
            }
            if (hf.getWorkType() != 4) {//未支付屏蔽未支付禁止显示的
                criteria.andEqualTo(MenuConfiguration.SHOW_PAYMENT, 1);
            }
            if (house.getDecorationType() == 2) {//如果是自带设计不查询量房
                criteria.andNotEqualTo(MenuConfiguration.TYPE, 2);
            }
            menuCondition(bean, criteria);
            example.orderBy(MenuConfiguration.SORT).asc();
            List<MenuConfiguration> menuConfigurations2 = iMenuConfigurationMapper.selectByExample(example);
            for (MenuConfiguration configuration : menuConfigurations2) {
                int roleType;
                switch (bean.getWorkerType()) {//0:大管家；1：工匠；2：设计师；3：精算师
                    case 0:
                        roleType = 2;//0:设计师；1：精算师；2：大管家；3：工匠
                        break;
                    case 1:
                        roleType = 3;//0:设计师；1：精算师；2：大管家；3：工匠
                        break;
                    case 2:
                        roleType = 0;//0:设计师；1：精算师；2：大管家；3：工匠
                        break;
                    default:
                        roleType = 1;//0:设计师；1：精算师；2：大管家；3：工匠
                        break;
                }
                configuration.initPath(imageAddress, webAddress, house.getId(), hf.getId(), roleType);
                ConstructionByWorkerIdBean.BigListBean.ListMapBean mapBean = new ConstructionByWorkerIdBean.BigListBean.ListMapBean();
                mapBean.setName(configuration.getName());
                mapBean.setUrl(configuration.getUrl());
                mapBean.setImage(configuration.getImage());
                mapBean.setType(configuration.getType());
                listMap.add(mapBean);
            }
            if (listMap.size() > 0) {
                bigListBean.setListMap(listMap);
                bigList.add(bigListBean);
            }
        }
        bean.setBigList(bigList);//添加菜单到返回体中
    }

    /**
     * 设置菜单查询条件
     */
    private void menuCondition(ConstructionByWorkerIdBean bean, Example.Criteria criteria) {
        switch (bean.getWorkerType()) {//0:大管家；1：工匠；2：设计师；3：精算师
            case 0:
                criteria.andEqualTo(MenuConfiguration.SHOW_HOUSEKEEPER, 1);
                break;
            case 1:
                criteria.andEqualTo(MenuConfiguration.SHOW_CRAFTSMAN, 1);
                break;
            case 2:
                criteria.andEqualTo(MenuConfiguration.SHOW_DESIGNER, 1);
                break;
            default:
                criteria.andEqualTo(MenuConfiguration.SHOW_ACTUARIES, 1);
                break;
        }
    }

    /**
     * 获取已得钱和还可得钱
     */
    private void setMoney(ConstructionByWorkerIdBean bean, HouseWorker hw) {
        HouseWorkerOrder hwo = houseWorkerOrderMapper.getHouseWorkerOrder(hw.getHouseId(), hw.getWorkerId(), hw.getWorkerTypeId());
        if (hwo == null) {
            bean.setAlreadyMoney(new BigDecimal(0));//已得钱
            bean.setAlsoMoney(new BigDecimal(0));//还可得钱
        } else {
            BigDecimal workPrice = hwo.getWorkPrice() == null ? new BigDecimal(0) : hwo.getWorkPrice();//总共钱
            BigDecimal haveMoney = hwo.getHaveMoney() == null ? new BigDecimal(0) : hwo.getHaveMoney();//已得到的钱
            BigDecimal repairPrice = hwo.getRepairPrice() == null ? new BigDecimal(0) : hwo.getRepairPrice();//当前阶段补人工钱
            BigDecimal repairTotalPrice = hwo.getRepairTotalPrice() == null ? new BigDecimal(0) : hwo.getRepairTotalPrice();//补人工总钱
            BigDecimal retentionMoney = hwo.getRetentionMoney() == null ? new BigDecimal(0) : hwo.getRetentionMoney();//滞留金
            BigDecimal deductPrice = hwo.getDeductPrice() == null ? new BigDecimal(0) : hwo.getDeductPrice();//评价积分扣除的钱
            //总共钱-已得到的钱+补人工钱-滞留金-评价扣的钱=还可得钱
            BigDecimal alsoMoney = new BigDecimal(workPrice.doubleValue() - haveMoney.doubleValue() + repairPrice.doubleValue() - retentionMoney.doubleValue() - deductPrice.doubleValue());
            if (alsoMoney.doubleValue() < 0) {
                alsoMoney = new BigDecimal(0);
            }
            bean.setAlsoMoney(alsoMoney);//还可得钱

            //已得到的钱+滞留金的钱+（补人工总钱-当前阶段补人工钱）=已得总钱
            BigDecimal alreadyMoney = new BigDecimal(haveMoney.doubleValue() + retentionMoney.doubleValue() + (repairTotalPrice.doubleValue() - repairPrice.doubleValue()));
            bean.setAlreadyMoney(alreadyMoney);//已得钱


        }
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
        return getButton(name, null, type);
    }

    private ConstructionByWorkerIdBean.ButtonListBean getButton(String name, String url, int type) {
        ConstructionByWorkerIdBean.ButtonListBean buttonListBean = new ConstructionByWorkerIdBean.ButtonListBean();
        buttonListBean.setButtonType(type);
        buttonListBean.setUrl(url);
        buttonListBean.setButtonTypeName(name);
        return buttonListBean;
    }


    /**
     * 获取用户信息
     *
     * @param userToken userToken
     * @return Member/ServerResponse
     */
    public Object getMember(String userToken) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        if (accessToken == null) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), EventStatus.USER_TOKEN_ERROR.getDesc());
        }
        Member worker = accessToken.getMember();
        if (worker == null) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), EventStatus.USER_TOKEN_ERROR.getDesc());
        }
        return worker;
    }

    /**
     * 获取工匠当前的施工单
     *
     * @param workerId 工匠ID
     * @return HouseWorker/ServerResponse
     */
    private Object getHouseWorker(ConstructionByWorkerIdBean bean, String workerId) {
        Example example = new Example(HouseWorker.class);
        example.createCriteria()
                .andEqualTo(HouseWorker.DATA_STATUS, 0)
                .andEqualTo(HouseWorker.WORKER_ID, workerId)
                .andCondition(" work_type in (1,6) ")
                .andEqualTo(HouseWorker.IS_SELECT, 1);
        example.orderBy(HouseWorker.MODIFY_DATE).desc();
        List<HouseWorker> selectList = houseWorkerMapper.selectByExample(example);//查询选中
        HouseWorker hw;
        if (selectList != null && selectList.size() > 0) {
            hw = selectList.get(0);
        } else {
            example = new Example(HouseWorker.class);
            example.createCriteria()
                    .andEqualTo(HouseWorker.DATA_STATUS, 0)
                    .andEqualTo(HouseWorker.WORKER_ID, workerId)
                    .andCondition(" work_type in (1,6) ");
            example.orderBy(HouseWorker.MODIFY_DATE).desc();
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);//查询选中
            if (houseWorkerList == null || houseWorkerList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "您暂无施工中的记录,快去接单吧！");
            }
            int count = 0;
            for (HouseWorker houseWorker : houseWorkerList) {//循环所有订单任务
                List<HouseFlowApply> supervisorCheckList = houseFlowApplyMapper.getSupervisorCheckList(houseWorker.getHouseId());//查询所有待大管家审核
                count += supervisorCheckList.size();
            }
            bean.setTaskNumber(count);//总任务数量
            hw = houseWorkerList.get(0);
            hw.setIsSelect(1);//设置成默认
            houseWorkerMapper.updateByPrimaryKeySelective(hw);
        }
        return hw;
    }
}
