package com.dangjia.acg.service.engineer;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.excel.ExportExcel;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.engineer.ArtisanDTO;
import com.dangjia.acg.dto.house.WareDTO;
import com.dangjia.acg.dto.repair.RepairMendDTO;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.matter.IWorkerDisclosureMapper;
import com.dangjia.acg.mapper.matter.IWorkerEverydayMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IInsuranceMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishConditionMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.matter.WorkerDisclosure;
import com.dangjia.acg.modle.matter.WorkerEveryday;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.Insurance;
import com.dangjia.acg.modle.worker.RewardPunishCondition;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.service.core.HouseWorkerService;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 17:37
 * 工程部
 */
@Service
public class EngineerService {
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private HouseWorkerService houseWorkerService;
    @Autowired
    private IInsuranceMapper insuranceMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IWarehouseMapper iWarehouseMapper;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper;
    @Autowired
    private IRewardPunishConditionMapper rewardPunishConditionMapper;
    @Autowired
    private IWorkerEverydayMapper iWorkerEverydayMapper;
    @Autowired
    private IWorkerDisclosureMapper iWorkerDisclosureMapper;

    @Autowired
    private RedisClient redisClient;//缓存

    /**
     * 已支付换工匠
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse changePayed(String houseWorkerId, String workerId) {
        try {
            HouseWorker houseWorker = houseWorkerMapper.selectByPrimaryKey(houseWorkerId);
//            if (houseWorker.getWorkerType() != 3) {//不操作管家
            //记录被换的人
            HouseWorker hw = new HouseWorker();
            hw.setHouseId(houseWorker.getHouseId());
            hw.setWorkerId(houseWorker.getWorkerId());
            hw.setWorkerTypeId(houseWorker.getWorkerTypeId());
            hw.setCreateDate(houseWorker.getCreateDate());
            hw.setModifyDate(houseWorker.getModifyDate());
            hw.setWorkerType(houseWorker.getWorkerType());
            hw.setWorkType(4);//4已支付被平台换
            hw.setIsSelect(0);
            houseWorkerMapper.insert(hw);

            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
            hwo.setAfterChange(hwo.getWorkPrice().subtract(hwo.getHaveMoney()));
            hwo.setWorkerId(workerId);
            hwo.setModifyDate(new Date());
            houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);

            //删除老工人已发出未审核的申请
            houseFlowApplyMapper.deleteNotMemberCheck(houseWorker.getHouseId(), houseWorker.getWorkerId());

            HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
            houseFlow.setNominator(workerId);
            houseFlow.setWorkType(4);
            houseFlow.setWorkerId(workerId);
            hw.setCreateDate(new Date());
            hw.setModifyDate(new Date());
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);


            houseWorker.setWorkType(6);
            houseWorker.setIsSelect(1);
            houseWorker.setWorkerId(workerId);
            houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
//            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("换人失败");
        }
    }

    /**
     * 抢单未支付
     * 换工匠重新抢
     */
    public ServerResponse changeWorker(String houseWorkerId) {
        try {
            HouseWorker houseWorker = houseWorkerMapper.selectByPrimaryKey(houseWorkerId);
            if (houseWorker.getWorkType() == 6) {
                return ServerResponse.createByErrorMessage("已支付,更换换人方式");
            }
            houseWorker.setWorkType(3);//被平台换
            houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);

            HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
            houseFlow.setWorkerId("");
            houseFlow.setWorkType(2);
            houseFlow.setReleaseTime(new Date());//重新发布
            houseFlow.setRefuseNumber(houseFlow.getRefuseNumber() + 1);
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("换人失败");
        }
    }

    /**
     * 取消指定
     */
    public ServerResponse cancelLockWorker(String houseFlowId) {
        try {
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            houseFlow.setGrabLock(0);
            houseFlow.setNominator("");
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 指定/修改指定工匠
     * #1.3.2版 指定
     */
    public ServerResponse setLockWorker(String houseFlowId, String workerId) {
        try {
            ServerResponse serverResponse = setGrabVerification(workerId, houseFlowId);
            if (!serverResponse.isSuccess()) {
                return serverResponse;
            }
            Member worker = memberMapper.selectByPrimaryKey(workerId);
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            houseFlow.setGrabLock(1);
            houseFlow.setNominator(workerId);
            houseFlow.setWorkType(3);//等待支付
            houseFlow.setWorkerId(workerId);
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            HouseWorker houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 1);
            houseWorkerService.grabSheet(worker, house, houseFlow, houseMapper);
            if (houseWorker != null) {
                houseWorker.setWorkerId(workerId);
                houseWorker.setWorkType(1);//已抢单
                houseWorker.setIsSelect(1);
                houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
            } else {
                houseWorker = new HouseWorker();
                houseWorker.setHouseId(house.getId());
                houseWorker.setWorkerId(worker.getId());
                houseWorker.setWorkerTypeId(houseFlow.getWorkerTypeId());
                houseWorker.setWorkerType(houseFlow.getWorkerType());
                houseWorker.setWorkType(1);//已抢单
                houseWorker.setIsSelect(1);
                houseWorkerMapper.insert(houseWorker);
            }
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 指定工匠验证
     *
     * @param memberId 用户登录信息
     */
    private ServerResponse setGrabVerification(String memberId, String houseFlowId) {
        try {
            Member member = memberMapper.selectByPrimaryKey(memberId);
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            if (hf == null) {
                return ServerResponse.createByErrorMessage("找不到该工序");
            }
            House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
            if (house == null) {
                return ServerResponse.createByErrorMessage("找不到该房子");
            }
            if (member == null) {
                return ServerResponse.createByErrorMessage("找不到该工匠");
            }
            //0待确认开工,1装修中,2休眠中,3已完工
            if (hf.getWorkerType() == 3 && hf.getSupervisorStart() == 0) {
                return ServerResponse.createByErrorMessage("该房大管家未开工");
            }
            if (house.getVisitState() == 3) {
                return ServerResponse.createByErrorMessage("该房已完工");
            }
            if (house.getPause() == 1) {
                return ServerResponse.createByErrorMessage("该房已休眠");
            }
            if (member.getWorkerType() != null && member.getWorkerType() != 3) {
                if (house.getVisitState() != 1) {
                    return ServerResponse.createByErrorMessage("该房子不在装修中，无法指派工匠");
                }
                HouseFlow supervisorHf = houseFlowMapper.getHouseFlowByHidAndWty(hf.getHouseId(), 3);//查询大管家的
                if (supervisorHf == null || supervisorHf.getWorkSteta() == 0) {
                    //大管家未开工，不允许指定工匠
                    return ServerResponse.createByErrorMessage("大管家未开工，不能指定其他工匠");
                }
            }
            if (member.getCheckType() == 0) {
                //审核中的人不能抢单
                return ServerResponse.createByErrorMessage("该工匠正在审核中！");
            }
            if (member.getCheckType() == 1) {
                //审核未通过 的人不能抢单
                return ServerResponse.createByErrorMessage("该工匠审核未通过！");
            }
            if (member.getCheckType() == 3) {
                //被禁用的帐户不能抢单
                return ServerResponse.createByErrorMessage("该工匠已经被禁用！");
            }
            if (member.getCheckType() == 4) {
                //冻结的帐户不能抢单
                return ServerResponse.createByErrorMessage("该工匠已冻结");
            }
            if (member.getCheckType() == 5) {
                return ServerResponse.createByErrorMessage("该工匠未提交资料审核,请通知工匠完善资料并提交审核！");
            }
            Example example = new Example(RewardPunishRecord.class);
            example.createCriteria().andEqualTo(RewardPunishRecord.MEMBER_ID, member.getId()).andEqualTo(RewardPunishRecord.STATE, "0");
            List<RewardPunishRecord> recordList = rewardPunishRecordMapper.selectByExample(example);
            //通过查看奖罚限制抢单时间限制抢单
            for (RewardPunishRecord record : recordList) {
                example = new Example(RewardPunishCondition.class);
                example.createCriteria().andEqualTo("rewardPunishCorrelationId", record.getRewardPunishCorrelationId());
                List<RewardPunishCondition> conditionList = rewardPunishConditionMapper.selectByExample(example);
                for (RewardPunishCondition rewardPunishCondition : conditionList) {
                    if (rewardPunishCondition.getType() == 3) {
                        Date wraprDate = rewardPunishCondition.getEndTime();
                        Date date = new Date();
                        if (date.getTime() < wraprDate.getTime()) {
                            return ServerResponse.createByErrorMessage("该工匠处于平台处罚期内，" +  DateUtil.getDateString(wraprDate.getTime()) + "以后才能抢单！");
                        }
                    }
                }
            }
            //抢单时间限制
            if (member.getWorkerType() > 3) {//其他工人
                long num = houseWorkerMapper.grabControl(member.getId());//查询未完工工地
                WorkerType wt = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                if (member.getWorkerType() != 7 && num >= wt.getMethods()) {
                    return ServerResponse.createByErrorMessage("该工匠达到持单上限，无法设置！");
                }

            }
            return ServerResponse.createBySuccessMessage("通过验证");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("验证出错！");
        }
    }

    /**
     * 抢单记录
     */
    public ServerResponse grabRecord(String houseId, String workerTypeId) {
        Example example = new Example(HouseWorker.class);
        example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseId).andEqualTo(HouseWorker.WORKER_TYPE_ID, workerTypeId);
        example.orderBy(HouseWorker.CREATE_DATE).desc();
        List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (HouseWorker houseWorker : houseWorkerList) {
            Member worker = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseWorker.getWorkerTypeId());
            HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseId, houseWorker.getWorkerTypeId());
            Map<String, Object> map = new HashMap<>();
            map.put("workSteta", houseFlow.getWorkSteta());//0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中
            map.put("name", worker.getName());
            map.put("workerId", worker.getId());
            map.put("workerTypeName", workerType.getName());
            map.put("HouseWorkerId", houseWorker.getId());
            map.put("workerTypeId", houseWorker.getWorkerTypeId());
            map.put("workType", houseWorker.getWorkType());//抢单状态:1已抢单等待被支付,2被换人,4已开工被换人,5拒单(工匠主动拒绝)，6被采纳支付,7抢单后放弃
            map.put("mobile", worker.getMobile());
            map.put("createDate", houseWorker.getCreateDate());
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }

    /**
     * 查看工匠订单
     */
    public ServerResponse workerOrder(String houseId) {
        Example example = new Example(HouseWorkerOrder.class);
        example.createCriteria().andEqualTo(HouseWorkerOrder.HOUSE_ID, houseId);
        example.orderBy(HouseWorkerOrder.CREATE_DATE).desc();
        List<HouseWorkerOrder> houseWorkerOrderList = houseWorkerOrderMapper.selectByExample(example);
        House house = houseMapper.selectByPrimaryKey(houseId);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (HouseWorkerOrder hwo : houseWorkerOrderList) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hwo.getWorkerTypeId());
            Member worker = memberMapper.selectByPrimaryKey(hwo.getWorkerId());
            HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseId, hwo.getWorkerTypeId());
            Map<String, Object> map = new HashMap<>();
            map.put("name", worker.getName());
            map.put("workerId", worker.getId());
            map.put("designerOk", house.getDesignerOk());
            map.put("budgetOk", house.getBudgetOk());
            map.put("workerTypeId", worker.getWorkerTypeId());
            map.put("workerType", worker.getWorkerType());
            map.put("workerTypeName", workerType.getName());
            map.put("mobile", worker.getMobile());
            map.put("createDate", houseFlow.getCreateDate());
            map.put("workSteta", houseFlow.getWorkSteta());
            map.put("EndTime", null);
            if (houseFlow.getWorkSteta() == 2) {
                map.put("EndTime", houseFlow.getModifyDate());
            }
            map.put("payState", hwo.getPayState());//0未支付，1已经支付
            map.put("retentionMoney", hwo.getRetentionMoney());//此单滞留金
            map.put("afterChange", hwo.getAfterChange());//换人后钱
            map.put("totalPrice", hwo.getTotalPrice());//工钱+材料
            map.put("materialPrice", hwo.getMaterialPrice());//材料钱
            map.put("workPrice", hwo.getWorkPrice());//工钱
            map.put("repairPrice", hwo.getRepairTotalPrice());//补人工钱
            map.put("haveMoney", hwo.getHaveMoney());//已拿钱
            map.put("everyMoney", hwo.getEveryMoney());//每日申请累计钱
            example = new Example(HouseFlowApply.class);
            example.createCriteria().andEqualTo(HouseFlowApply.WORKER_ID, hwo.getWorkerId())
                    .andEqualTo(HouseFlowApply.APPLY_TYPE, 5)
                    .andEqualTo(HouseFlowApply.HOUSE_ID, hwo.getHouseId());
            map.put("checkMoney", houseFlowApplyMapper.selectCountByExample(example));//巡查次数
            map.put("patrol", houseFlow.getPatrol());//巡查标准
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }


    /**
     * 禁用启用工序
     */
    public ServerResponse setState(String houseFlowId) {
        try {
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            if (houseFlow.getState() == 0) {
                houseFlow.setState(2);//禁用
            } else if (houseFlow.getState() == 2) {
                houseFlow.setState(0);//启用
            }
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 查看工序
     */
    public ServerResponse houseFlowList(String houseId) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId);
        example.orderBy(HouseFlow.SORT).desc();
        String workerId;
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (HouseFlow houseFlow : houseFlowList) {
            workerId = houseFlow.getWorkerId();
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            Map<String, Object> map = new HashMap<>();
            map.put("houseFlowId", houseFlow.getId());
            map.put("workerTypeId", houseFlow.getWorkerTypeId());
            map.put("workerTypeName", workerType.getName());
            map.put("state", houseFlow.getState());//0可用排期，2禁用，3删除
            map.put("grabLock", houseFlow.getGrabLock());//0可抢，1已指定工人 2不可以抢
            //map.put("nominator",houseFlow.getNominator());//指定的工人
            map.put("refuseNumber", houseFlow.getRefuseNumber());//被拒人数
            map.put("grabNumber", houseFlow.getGrabNumber());//抢过单人数
            map.put("workType", houseFlow.getWorkType());//抢单状态，1还没有发布，只是默认房产,2等待被抢，3有工匠抢单,4已采纳已支付
            if (houseFlow.getWorkType() == 3) {//待支付
                HouseWorker houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 1);
                if (houseWorker != null && CommonUtil.isEmpty(workerId)) {
                    workerId = houseWorker.getWorkerId();
                }
                map.put("houseWorkerId", houseWorker != null ? houseWorker.getId() : "");
            } else if (houseFlow.getWorkType() == 4) {//已支付
                HouseWorker houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 6);
                if (houseWorker != null) {
                    if (CommonUtil.isEmpty(workerId)) {
                        workerId = houseWorker.getWorkerId();
                    }
                    map.put("houseWorkerId", houseWorker.getId());
                }
            }
            map.put("releaseTime", houseFlow.getReleaseTime());//发布时间
            map.put("workSteta", houseFlow.getWorkSteta());//0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中
            map.put("pause", houseFlow.getPause());//施工状态0正常,1暂停
            map.put("totalPrice", houseFlow.getTotalPrice());//总钱 工钱+材料
            map.put("materialPrice", houseFlow.getMaterialPrice());//材料钱
            map.put("workPrice", houseFlow.getWorkPrice().setScale(2, BigDecimal.ROUND_HALF_UP));//工钱
            map.put("patrol", houseFlow.getPatrol());//巡查次数
            map.put("workerId", houseFlow.getWorkerId());//工人ID
            if (houseFlow.getWorkerType() == 1) {//设计
                map.put("designerOk", house.getDesignerOk());
            }
            if (houseFlow.getWorkerType() == 2) {//精算
                map.put("budgetOk", house.getBudgetOk());
            }
            if (houseFlow.getWorkType() > 2 && !CommonUtil.isEmpty(workerId)) {
                Member worker = memberMapper.selectByPrimaryKey(workerId);
                if (worker != null) {
                    map.put("workerName", worker.getName());//工人姓名
                    map.put("mobile", worker.getMobile());//电话
                }
            }
            Example example1 = new Example(HouseWorker.class);
            example1.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseFlow.getHouseId()).andEqualTo(HouseWorker.WORKER_TYPE_ID, houseFlow.getWorkerTypeId()).andNotEqualTo(HouseWorker.WORK_TYPE, 6).andNotEqualTo(HouseWorker.WORK_TYPE, 1);
            List<HouseWorker> houseWorkers = houseWorkerMapper.selectByExample(example1);
            if (houseWorkers.size() == 0) {
                map.put("show", "0");
            } else {
                map.put("show", "1");
            }
            Example example2 = new Example(HouseWorkerOrder.class);
            example2.createCriteria().andEqualTo(HouseWorkerOrder.HOUSE_ID, houseFlow.getHouseId()).andEqualTo(HouseWorkerOrder.WORKER_TYPE_ID, houseFlow.getWorkerTypeId());
            List<HouseWorkerOrder> houseWorkerOrderList = houseWorkerOrderMapper.selectByExample(example2);
            if (houseWorkerOrderList.size() == 0) {
                map.put("havaMoney", 0);
            } else {
                BigDecimal retentionMoney = houseWorkerOrderList.get(0).getRetentionMoney();
                if (retentionMoney == null) {
                    retentionMoney = new BigDecimal(0);
                }
                BigDecimal havaMoney = houseWorkerOrderList.get(0).getRepairTotalPrice().subtract(houseWorkerOrderList.get(0).getRepairPrice());
                havaMoney = houseWorkerOrderList.get(0).getHaveMoney().add(havaMoney);
                havaMoney = retentionMoney.add(havaMoney);
                map.put("workPrice", houseWorkerOrderList.get(0).getWorkPrice().add(houseWorkerOrderList.get(0).getRepairTotalPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
                map.put("havaMoney", havaMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }


    /**
     * 查看工序(业主申请的供需)
     */
    public ServerResponse getHouseFlowList(String houseId) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId);
        example.orderBy(HouseFlow.SORT).desc();
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (HouseFlow houseFlow : houseFlowList) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            Map<String, Object> map = new HashMap<>();
            map.put("houseFlowId", houseFlow.getId());
            map.put("houseId", houseFlow.getHouseId());
            map.put("workerTypeId", houseFlow.getWorkerTypeId());
            map.put("workerTypeName", workerType.getName());
            map.put("state", houseFlow.getWorkType() == 1 ? 0 : 1);
            map.put("disable", houseFlow.getWorkType() != 1);
            if (!CommonUtil.isEmpty(house.getCustomEdit())) {
                String[] workerTypeArr = house.getCustomSort().split(",");
                for (String s : workerTypeArr) {
                    if (houseFlow.getWorkerTypeId().equals(s)) {
                        map.put("disable", true);
                        break;
                    }
                }
            }
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }

    /**
     * 工匠钱包 信息
     */
    public ServerResponse workerMess(String workerId) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Member worker = memberMapper.selectByPrimaryKey(workerId);
        Map<String, Object> map = new HashMap<>();
        map.put("haveMoney", worker.getHaveMoney());
        map.put("surplusMoney", worker.getSurplusMoney());
        map.put("retentionMoney", worker.getRetentionMoney());
        if (CommonUtil.isEmpty(worker.getHead())) {
            worker.setHead(Utils.getHead());
        }
        map.put("userName", worker.getUserName());
        map.put("name", worker.getName());
        map.put("head", address + worker.getHead());//头像
        map.put("idcaoda", address + worker.getIdcaoda());//身份证正面
        map.put("idcaodb", address + worker.getIdcaodb());//反面
        map.put("idcaodall", address + worker.getIdcaodall());//半身照
        map.put("idnumber", worker.getIdnumber());//身份证号码
        map.put("praiseRate", worker.getPraiseRate());//好评率
        map.put("volume", worker.getVolume());//成交量
        return ServerResponse.createBySuccess("查询成功", map);
    }

    /**
     * 历史工地
     */
    public ServerResponse historyHouse(String workerId) {
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.WORKER_ID, workerId).andEqualTo(HouseFlow.WORK_TYPE, 4);
        example.orderBy(HouseFlow.CREATE_DATE).desc();
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (HouseFlow houseFlow : houseFlowList) {
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            if (house == null) continue;
            Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
            if (member == null) continue;
            Map<String, Object> map = new HashMap<>();
            map.put("houseId", house.getId());
            map.put("address", house.getHouseName());
            map.put("memberName", member.getNickName() == null ? member.getName() : member.getNickName());
            map.put("mobile", member.getMobile());
            Member supervisor = memberMapper.getSupervisor(house.getId());
            if (supervisor != null) {
                map.put("supName", supervisor.getName());
                map.put("supMobile", supervisor.getMobile());
            }
            map.put("createDate", houseFlow.getCreateDate());
            map.put("workSteta", house.getVisitState()); //0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }


    /**
     * 工地暂停施工
     */
    public ServerResponse setPause(String houseId) {
        try {
            House house = houseMapper.selectByPrimaryKey(houseId);
            if (house.getPause() == 0) {
                house.setPause(1);
            } else {
                house.setPause(0);
            }
            houseMapper.updateByPrimaryKeySelective(house);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 工地列表
     */
    public ServerResponse getHouseList(HttpServletRequest request, PageDTO pageDTO, Integer visitState, String searchKey, String startDate, String endDate, String supKey) {
        String userID = request.getParameter(Constants.USERID);

        String cityKey = request.getParameter(Constants.CITY_ID);
//        String cityKey = redisClient.getCache(Constants.CITY_KEY + userID, String.class);
        if (CommonUtil.isEmpty(cityKey)) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<House> houseList = houseMapper.getHouseListLikeSearchKey(cityKey, visitState, searchKey, startDate, endDate, supKey);
        PageInfo pageResult = new PageInfo(houseList);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (House house : houseList) {
            if (StringUtils.isNotEmpty(house.getMemberId())) {
                Map<String, Object> map = new HashMap<>();
                map.put("houseId", house.getId());
                map.put("address", house.getHouseName());
                map.put("memberName", house.getOwnerNickName() == null ? house.getOwnerName() : house.getOwnerNickName());
                map.put("mobile", house.getOwnerMobile());
                map.put("pause", house.getPause());
                map.put("visitState", house.getVisitState()); //0待确认开工,1装修中,2休眠中,3已完工 4提前结束装修 5提前结束装修申请中
                map.put("supName", house.getSupName());
                map.put("supMobile", house.getSupMobile());
                HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart1(house.getId(), new Date());//查询今日开工记录
                map.put("todayStartPause", todayStart == null ? "0" : "1"); //0否,1是
                map.put("createDate", house.getConstructionDate());

                Example example1 = new Example(HouseFlowApply.class);
                example1.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, house.getId()).andEqualTo(HouseFlowApply.MEMBER_CHECK, 1).andEqualTo(HouseFlowApply.APPLY_TYPE, 3);
                List<HouseFlowApply> houseFlowss = houseFlowApplyMapper.selectByExample(example1);
                int suspendDay = 0;//停工天数
                for (HouseFlowApply flowss : houseFlowss) {
                    suspendDay += flowss.getSuspendDay();
                }
                int startDay;
                if (house.getCompletedDate() != null) {
                    startDay = DateUtil.daysofTwo(house.getConstructionDate(), house.getCompletedDate());
                } else {
                    startDay = DateUtil.daysofTwo(house.getConstructionDate(), new Date());
                }
                map.put("startDay", 0);
                if (suspendDay < startDay) {
                    map.put("startDay", (startDay - suspendDay));
                }
                int buildDay = houseMapper.getBuildDay(house.getId());
                map.put("buildDay", buildDay);
                mapList.add(map);
            }
        }
        pageResult.setList(mapList);
        return ServerResponse.createBySuccess("查询列表成功", pageResult);
    }

    /**
     * 工匠列表
     */
    public ServerResponse artisanList(String cityId, String name, String workerTypeId, String type, String checkType, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Member> memberList = memberMapper.artisanList(cityId, name, workerTypeId, type, checkType);
            PageInfo pageResult = new PageInfo(memberList);
            List<ArtisanDTO> artisanDTOS = new ArrayList<>();
            for (Member member : memberList) {
                if (StringUtil.isEmpty(member.getWorkerTypeId())) {
                    continue;
                }
                ArtisanDTO artisanDTO = new ArtisanDTO();
                artisanDTO.setId(member.getId());
                artisanDTO.setName(member.getName());
                artisanDTO.setMobile(member.getMobile());
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                if (workerType != null) {
                    artisanDTO.setWorkerTypeName(workerType.getName());
                }
                artisanDTO.setCreateDate(member.getCreateDate());
                artisanDTO.setInviteNum(member.getInviteNum());
                artisanDTO.setCheckType(member.getCheckType());
                artisanDTO.setEvaluationScore(member.getEvaluationScore());
//                Example example = new Example(HouseWorker.class);
//                example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId());
//                artisanDTO.setVolume(houseWorkerMapper.selectByExample(example).size());//接单量
                artisanDTO.setVolume(alternative(member.getId(), member.getWorkerType()));//接单量
                artisanDTO.setRealNameState(member.getRealNameState());
                artisanDTO.setRealNameDescribe(member.getRealNameDescribe());
                artisanDTO.setCheckDescribe(member.getCheckDescribe());
                artisanDTO.setIsJob(member.getIsJob());
                if (StringUtil.isNotEmpty(member.getSuperiorId())) {
                    Member superior = memberMapper.selectByPrimaryKey(member.getSuperiorId());
                    if (superior != null) {
                        artisanDTO.setSuperior(superior.getName());
                    }
                }
                Example example = new Example(Insurance.class);
                example.createCriteria().andEqualTo(Insurance.WORKER_ID, member.getId());
                example.orderBy(Insurance.END_DATE).desc();
                List<Insurance> insurances = insuranceMapper.selectByExample(example);

                if(insurances.size()==0){
                    artisanDTO.setSurDay(0);
                }
                if(insurances.size()>0){
                    //保险服务剩余天数小于等于60天
                    Integer daynum=DateUtil.daysofTwo(new Date(),insurances.get(0).getEndDate());
                    artisanDTO.setSurDay(daynum);
                }
                artisanDTOS.add(artisanDTO);
            }
            pageResult.setList(artisanDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public ServerResponse getWareHouse(String houseId, PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(Warehouse.class);
        example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, houseId);
        example.orderBy(Warehouse.PRODUCT_SN).desc();
        List<Warehouse> warehouseList = iWarehouseMapper.selectByExample(example);
        if (warehouseList == null) {
            return ServerResponse.createByErrorMessage("查无数据");
        }
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        PageInfo pageResult = new PageInfo(warehouseList);
        List<WareDTO> warehouseMap = new ArrayList<>();
        Integer visitState = houseMapper.selectByPrimaryKey(houseId).getVisitState();
        for (Warehouse warehouse : warehouseList) {
            List<RepairMendDTO> repairMends = houseMapper.getRepairMend(houseId, warehouse.getProductId());
            warehouse.setWorkBack(0.0);
            warehouse.setOwnerBack(0.0);
            for (RepairMendDTO r : repairMends) {
                //工匠退
                if (r.getType() == 2) {
                    warehouse.setWorkBack(warehouse.getWorkBack() + r.getShopCount());
                }
                //业主退
                else {
                    warehouse.setOwnerBack(warehouse.getOwnerBack() + r.getShopCount());
                }
            }
            warehouse.setImage(imageAddress + warehouse.getImage());
            WareDTO wareDTO = new WareDTO();
            BeanUtils.beanToBean(warehouse, wareDTO);
            wareDTO.setNoSend(warehouse.getShopCount() - warehouse.getAskCount());
            wareDTO.setLeftAskCount(warehouse.getShopCount() - warehouse.getAskCount() - warehouse.getOwnerBack());
            if (visitState == 3) {
                wareDTO.setUseCount(warehouse.getReceive() - warehouse.getWorkBack());
            }
            warehouseMap.add(wareDTO);
        }
        pageResult.setList(warehouseMap);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    public ServerResponse exportWareHouse(HttpServletResponse response, String houseId, String userName, String address) {
        try {
            Example example = new Example(Warehouse.class);
            example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, houseId);
            example.orderBy(Warehouse.PRODUCT_SN).desc();
            List<Warehouse> warehouseList = iWarehouseMapper.selectByExample(example);
            if (warehouseList == null) {
                return ServerResponse.createByErrorMessage("查无数据");
            }
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<WareDTO> warehouseMap = new ArrayList<>();
            Integer visitState = houseMapper.selectByPrimaryKey(houseId).getVisitState();
            for (Warehouse warehouse : warehouseList) {
                List<RepairMendDTO> repairMends = houseMapper.getRepairMend(houseId, warehouse.getProductId());
                warehouse.setWorkBack(0.0);
                warehouse.setOwnerBack(0.0);
                for (RepairMendDTO r : repairMends) {
                    //工匠退
                    if (r.getType() == 2) {
                        warehouse.setWorkBack(warehouse.getWorkBack() + r.getShopCount());
                    }
                    //业主退
                    else {
                        warehouse.setOwnerBack(warehouse.getOwnerBack() + r.getShopCount());
                    }
                }
                warehouse.setImage(imageAddress + warehouse.getImage());
                WareDTO wareDTO = new WareDTO();
                BeanUtils.beanToBean(warehouse, wareDTO);
                wareDTO.setUserName(userName);
                wareDTO.setAddress(address);
                wareDTO.setNoSend(warehouse.getShopCount() - warehouse.getAskCount());
                wareDTO.setLeftAskCount(warehouse.getShopCount() - warehouse.getAskCount() - warehouse.getOwnerBack());
                if (visitState == 3) {
                    wareDTO.setUseCount(warehouse.getReceive() - warehouse.getWorkBack());
                }
                warehouseMap.add(wareDTO);
            }
            ExportExcel exportExcel = new ExportExcel();//创建表格实例
            exportExcel.setDataList("业主仓库", WareDTO.class, warehouseMap);
            exportExcel.write(response, houseId + ".xlsx");
            return ServerResponse.createBySuccessMessage("导出Excel成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("导出Excel失败");
        }
    }

    public ServerResponse freeze(String memberId, boolean type) {
        try {
            Member member = memberMapper.selectByPrimaryKey(memberId);
            member.setIsJob(type);
            memberMapper.updateByPrimaryKeySelective(member);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }

    }

    /**
     * 获取工地交底事项
     */
    public ServerResponse getSureList(Integer type, Integer state, String search, PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(WorkerDisclosure.class);
        Example.Criteria criteria = example.createCriteria();
        if (state != null) {
            criteria.andEqualTo(WorkerDisclosure.STATE, state);
        }
        criteria.andEqualTo(WorkerDisclosure.TYPE, type);
        if (!CommonUtil.isEmpty(search)) {
            criteria.andLike(WorkerDisclosure.NAME, "%" + search + "%");
        }
        example.orderBy(WorkerDisclosure.MODIFY_DATE).desc();
        List<WorkerDisclosure> workerDisclosures = iWorkerDisclosureMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(workerDisclosures);
        if (type == 0) {
            List<Object> list = new ArrayList<>();
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for (WorkerDisclosure d : workerDisclosures) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", d.getId());
                map.put("name", d.getName());
                map.put("desc", d.getDetails());
                map.put("img", d.getImg());
                map.put("imgUrl", imageAddress + d.getImg());
                map.put("status", d.getState());
                map.put("createDate", d.getCreateDate());
                map.put("modifyDate", d.getModifyDate());
                list.add(map);
            }
            pageResult.setList(list);
        }
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    public ServerResponse addSure(WorkerDisclosure workerDisclosure) {
        try {
            workerDisclosure.setId((int) (Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
            iWorkerDisclosureMapper.insertSelective(workerDisclosure);
            return ServerResponse.createBySuccessMessage("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }

    public ServerResponse updateSure(String name, String details, String img, Integer state, String id) {
        WorkerDisclosure workerDisclosure = iWorkerDisclosureMapper.selectByPrimaryKey(id);
        if (workerDisclosure == null) {
            return ServerResponse.createByErrorMessage("该交底事项不存在");
        }
        if (CommonUtil.isEmpty(name) && CommonUtil.isEmpty(details) && CommonUtil.isEmpty(img) && state == null) {
            return ServerResponse.createByErrorMessage("请输入需要修改的属性");
        }
        if (!CommonUtil.isEmpty(name)) {
            workerDisclosure.setName(name);
        }
        if (!CommonUtil.isEmpty(details)) {
            workerDisclosure.setDetails(details);
        }
        if (!CommonUtil.isEmpty(img)) {
            workerDisclosure.setImg(img);
        }
        if (state != null) {
            workerDisclosure.setState(state);
        }
        workerDisclosure.setModifyDate(new Date());
        iWorkerDisclosureMapper.updateByPrimaryKeySelective(workerDisclosure);
        return ServerResponse.createBySuccessMessage("修改成功");
    }

    public ServerResponse addItems(String name, Integer type, Integer state) {
        try {
            WorkerEveryday workerEveryday = new WorkerEveryday();
            workerEveryday.setName(name);
            workerEveryday.setType(type);
            if (state != null) {
                workerEveryday.setState(state);
            }
            iWorkerEverydayMapper.insert(workerEveryday);
            return ServerResponse.createBySuccessMessage("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }

    public ServerResponse getItemsList(Integer type, Integer state, String search, PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(WorkerEveryday.class);
        if (state != null) {
            example.createCriteria().andEqualTo(WorkerEveryday.STATE, state);
        }
        if (type != null) {
            example.createCriteria().andEqualTo(WorkerEveryday.TYPE, type);
        }
        if (CommonUtil.isEmpty(search)) {
            example.createCriteria().andLike(WorkerEveryday.NAME, "%" + search + "%");
        }
        example.orderBy(WorkerEveryday.TYPE).orderBy(WorkerEveryday.MODIFY_DATE).desc();
        List<WorkerEveryday> workerEverydays = iWorkerEverydayMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(workerEverydays);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    public ServerResponse updateItems(String name, Integer type, Integer state, String id) {
        WorkerEveryday workerEveryday = iWorkerEverydayMapper.selectByPrimaryKey(id);
        if (workerEveryday == null) {
            return ServerResponse.createByErrorMessage("该日常事项不存在");
        }
        if (CommonUtil.isEmpty(name) && type == null && state == null) {
            return ServerResponse.createByErrorMessage("请输入需要修改的属性");
        }
        if (!CommonUtil.isEmpty(name)) {
            workerEveryday.setName(name);
        }
        if (type != null) {
            workerEveryday.setType(type);
        }
        if (state != null) {
            workerEveryday.setState(state);
        }
        workerEveryday.setModifyDate(new Date());
        iWorkerEverydayMapper.updateByPrimaryKeySelective(workerEveryday);
        return ServerResponse.createBySuccessMessage("修改成功");
    }


    /**
     * 接单数
     *
     * @param workerId
     * @return
     */
    public int alternative(String workerId, Integer workerType) {
        Example example = new Example(HouseFlow.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(HouseFlow.WORKER_ID, workerId);
        if (workerType == 3) {
            criteria.andEqualTo(HouseFlow.SUPERVISOR_START, 1);
        } else if (workerType != 1 && workerType != 2) {
            criteria.andCondition(" work_steta not in(0,3)");
        }
        return houseFlowMapper.selectCountByExample(example);
    }
}
