package com.dangjia.acg.service.core;

import com.dangjia.acg.api.repair.MendMaterielAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.house.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2019/3/27 0027
 * Time: 9:55
 * 1.31业务补充
 */
@Service
public class HouseWorkerSupService {
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private MendMaterielAPI mendMaterielAPI;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private ConfigMessageService configMessageService;

    @Autowired
    private HouseService houseService;
    @Autowired
    private HouseFlowScheduleService houseFlowScheduleService;

    /**
     * 管家审核验收申请h
     * 材料审查
     * 剩余材料列表
     */
    public ServerResponse surplusList(String houseFlowApplyId) {
        HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
        return mendMaterielAPI.surplusList(houseFlowApply.getWorkerTypeId(), houseFlowApply.getHouseId());
    }

    /**
     * 审核停工页面内容
     */
    public ServerResponse tingGongPage(String houseFlowApplyId) {
        HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
        Member worker = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> map = new HashMap<>();
        map.put("head", configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + worker.getHead());
        map.put("workName", workerType.getName());
        map.put("name", worker.getName());
        map.put("praiseRate", worker.getPraiseRate() == null ? "100%" : worker.getPraiseRate().multiply(new BigDecimal(100)) + "%");
        map.put("mobile", worker.getMobile());
        map.put("memberId", worker.getId());
        map.put("applyDec", houseFlowApply.getApplyDec());
        map.put("startDate", sdf.format(houseFlowApply.getStartDate()));
        map.put("endDate", sdf.format(houseFlowApply.getEndDate()));
        map.put("createDate", houseFlowApply.getCreateDate());
        return ServerResponse.createBySuccess("获取成功", map);
    }

    /**
     * 工匠申请停工
     */
    public ServerResponse applyShutdown(String userToken, String houseFlowId, String applyDec, String startDate, String endDate) {
        try {
            if (CommonUtil.isEmpty(startDate)) {
                return ServerResponse.createByErrorMessage("请选择开始时间");
            }
            if (CommonUtil.isEmpty(endDate)) {
                return ServerResponse.createByErrorMessage("请选择结束时间");
            }
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            String format = "yyyy-MM-dd";
            Date start = DateUtil.convert(startDate, format);
            Date end = DateUtil.convert(endDate, format);
            //如果为大管家整体停工， 则功所有工序整体顺延（效果为：对于已完工工序无效果，对于已开工未完工工序相当于请假XX天，对于未开工工序相当于同时推迟开工和阶段完工工序；）
            //5查看装修排期时，计划有工序开始或结束的日期有特殊标记
            //6已经停工的工序，若大管家再操作“整体停工”，不能简单累加两次停工天数，而是比对两次停工的日期，修改停工天数
            if (worker.getWorkerType() == 3) {
                String[] houseFlowIds = houseFlowId.split(",");
                if (houseFlowIds.length > 0) {
                    for (String flowId : houseFlowIds) {
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(flowId);
                        HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(houseFlow.getHouseId(), houseFlow.getWorkerId(), new Date());//查询今日开工记录
                        if (todayStart != null && DateUtil.daysofTwo(new Date(), start) == 0) {
                            start = DateUtil.addDateDays(start, 1);
                            //如果结束日期小于开始日期，则表示请假的开始结束日期即是今天，则不记录停工申请
                            if (start.getTime() > end.getTime()) {
                                continue;//跳过
                            }
                        }
                        Example example = new Example(HouseFlowApply.class);
                        example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, flowId)
                                .andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                                .andEqualTo(HouseFlowApply.MEMBER_CHECK, 1)
                                .andEqualTo(HouseFlowApply.DATA_STATUS, 0);
                        List<HouseFlowApply> houseFlowList = houseFlowApplyMapper.selectByExample(example);
                        boolean isBG = false;//是否变开始时间，用于不差延续，不包括当前，因为上次的延续包括了当前
                        for (HouseFlowApply flow : houseFlowList) {
                            if (flow.getEndDate().getTime() >= start.getTime()) {
                                start = flow.getEndDate();
                                isBG = true;
                            }
                        }
                        if (start.getTime() > end.getTime()) {
                            start = end;
                        }
                        //查看是否存在相同的结束时间的停工申请，存在则不延后时间
                        example = new Example(HouseFlowApply.class);
                        example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, flowId)
                                .andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                                .andEqualTo(HouseFlowApply.DATA_STATUS, 0)
                                .andEqualTo(HouseFlowApply.END_DATE, end);
                        List<HouseFlowApply> houseFlowLists = houseFlowApplyMapper.selectByExample(example);

                        int suspendDay =  DateUtil.daysofTwo(start, end);
                        //如果没有变更则加上开始第一天
                        if (!isBG) {
                            suspendDay++;
                        }
                        if (houseFlowLists.size() > 0) {
                            suspendDay = 0;
                        }

                        if (suspendDay > 0) {
                            //计划顺延
                            houseFlowScheduleService.updateFlowSchedule(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), suspendDay, null);
                        }
                        HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
                        hfa.setHouseFlowId(flowId);//工序id
                        hfa.setWorkerId(houseFlow.getWorkerId());//工人id
                        hfa.setWorkerTypeId(houseFlow.getWorkerTypeId());//工种id
                        hfa.setWorkerType(houseFlow.getWorkerType());//工种类型
                        hfa.setHouseId(houseFlow.getHouseId());//房子id
                        hfa.setApplyType(3);//申请类型0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5巡查,6无人巡查
                        hfa.setApplyDec(applyDec);//描述
                        hfa.setApplyMoney(new BigDecimal(0));//申请得钱
                        hfa.setSupervisorMoney(new BigDecimal(0));
                        hfa.setOtherMoney(new BigDecimal(0));
                        hfa.setMemberCheck(0);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核R
                        hfa.setPayState(1);//标记为新停工申请
                        hfa.setMemberCheck(1);//默认业主审核状态通过
                        hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                        hfa.setSuspendDay(suspendDay);//申请停工天数 计算
                        hfa.setStartDate(start);
                        hfa.setEndDate(end);
                        hfa.setOperator(worker.getId());
                        houseFlowApplyMapper.insert(hfa);

                        //重新获取
                        houseFlow = houseFlowMapper.selectByPrimaryKey(flowId);
                        if (houseFlow.getWorkSteta() != 1 && houseFlow.getWorkSteta() != 2 && houseFlow.getWorkSteta() != 6) {
                            if (start.getTime() == DateUtil.toDate(DateUtil.getDateString2(new Date().getTime())).getTime()) {
                                houseFlow.setPause(1);//0:正常；1暂停；
                                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);//发停工申请默认修改施工状态为暂停
                            }
                        }
                        houseService.insertConstructionRecord(hfa);
                    }
                }

            } else {
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
                House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());//查询房子
                if (houseFlow.getPause() == 1) {
                    return ServerResponse.createByErrorMessage("工序已暂停施工,请勿重复申请");
                }
                if (houseFlow.getWorkSteta() == 3) {
                    return ServerResponse.createByErrorMessage("工序待交底请勿发起停工申请");
                }

                HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(houseFlow.getHouseId(), houseFlow.getWorkerId(), new Date());//查询今日开工记录
                if (todayStart != null && DateUtil.daysofTwo(new Date(), start) == 0) {
                    return ServerResponse.createByErrorMessage("工序今日已开工，请勿选择今日时间！");
                }
                Example example = new Example(HouseFlowApply.class);
                example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, houseFlowId).andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                        .andCondition(" member_check in (1,3) ").andEqualTo(HouseFlowApply.PAY_STATE, 1);
                List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.selectByExample(example);
                for (HouseFlowApply flow : houseFlowApplyList) {
                    if (start.getTime() >= flow.getStartDate().getTime() && start.getTime() <= flow.getEndDate().getTime()) {
                        return ServerResponse.createByErrorMessage("已申请过(" + DateUtil.getDateString2(flow.getStartDate().getTime()) + "-" + DateUtil.getDateString2(flow.getEndDate().getTime()) + ")范围内的停工，请更换其他开始时间");
                    }
                    if (end.getTime() >= flow.getStartDate().getTime() && end.getTime() <= flow.getEndDate().getTime()) {
                        return ServerResponse.createByErrorMessage("已申请过(" + DateUtil.getDateString2(flow.getStartDate().getTime()) + "-" + DateUtil.getDateString2(flow.getEndDate().getTime()) + ")范围内的停工，请更换其他结束时间");

                    }
                }
                HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
                hfa.setHouseFlowId(houseFlowId);//工序id
                hfa.setWorkerId(houseFlow.getWorkerId());//工人id
                hfa.setWorkerTypeId(houseFlow.getWorkerTypeId());//工种id
                hfa.setWorkerType(houseFlow.getWorkerType());//工种类型
                hfa.setHouseId(houseFlow.getHouseId());//房子id
                hfa.setApplyType(3);//申请类型0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5巡查,6无人巡查
                hfa.setApplyDec(applyDec);//描述
                hfa.setApplyMoney(new BigDecimal(0));//申请得钱
                hfa.setSupervisorMoney(new BigDecimal(0));
                hfa.setOtherMoney(new BigDecimal(0));
                hfa.setMemberCheck(0);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
                hfa.setPayState(1);//标记为新停工申请
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                hfa.setSuspendDay(DateUtil.daysofTwo(start, end) + 1);//申请停工天数 计算
                hfa.setStartDate(start);
                hfa.setEndDate(end);
                hfa.setOperator(worker.getId());
                houseFlowApplyMapper.insert(hfa);
                houseService.insertConstructionRecord(hfa);
                if (houseFlow.getWorkSteta() != 1 && houseFlow.getWorkSteta() != 2 && houseFlow.getWorkSteta() != 6) {
                    if (start.getTime() == DateUtil.toDate(DateUtil.getDateString2(new Date().getTime())).getTime()) {
                        houseFlow.setPause(1);//0:正常；1暂停；
                        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);//发停工申请默认修改施工状态为暂停
                    }
                }
                //计划顺延
                houseFlowScheduleService.updateFlowSchedule(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), hfa.getSuspendDay(), null);
                configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "工匠申请停工",
                        String.format(DjConstants.PushMessage.STEWARD_CRAFTSMEN_APPLY_FOR_STOPPAGE, house.getHouseName()), "");
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败");
        }
    }

    /**
     * 管家停工选择影响顺延的工序列表
     */
    public ServerResponse getShutdownWorkerType(String houseId) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId).andCondition(" worker_type>3 and state=0 and work_steta not in (1,2,6) ");
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        List<WorkerType> listtype = new ArrayList<>();
        for (HouseFlow flow : houseFlowList) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(flow.getWorkerTypeId());
            if (workerType == null) {
                continue;
            }
            workerType.setImage(address + workerType.getImage());
            workerType.setId(flow.getId());
            listtype.add(workerType);
        }
        return ServerResponse.createBySuccess("查询成功", listtype);
    }

}
