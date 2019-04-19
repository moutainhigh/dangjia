package com.dangjia.acg.service.engineer;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.engineer.ArtisanDTO;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishConditionMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.RewardPunishCondition;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

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
    private IRewardPunishRecordMapper rewardPunishRecordMapper;

    @Autowired
    private IRewardPunishConditionMapper rewardPunishConditionMapper;
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
            hw.setWorkerType(houseWorker.getWorkerType());
            hw.setWorkType(4);//4已支付被平台换
            hw.setIsSelect(0);
            houseWorkerMapper.insert(hw);

            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
            hwo.setAfterChange(hwo.getWorkPrice().subtract(hwo.getHaveMoney()));
            hwo.setWorkerId(workerId);
            houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);

            //删除老工人已发出未审核的申请
            houseFlowApplyMapper.deleteNotMemberCheck(houseWorker.getHouseId(), houseWorker.getWorkerId());
            HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
            houseFlow.setWorkerId(workerId);
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);

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
            ServerResponse serverResponse=setGrabVerification(workerId,houseFlowId);
            if(serverResponse.getResultCode()!= EventStatus.SUCCESS.getCode()){
                return serverResponse;
            }
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            houseFlow.setGrabLock(1);
            houseFlow.setNominator(workerId);
            houseFlow.setWorkType(3);//等待支付
            houseFlow.setWorkerId(workerId);
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);

            HouseWorker houseWorker=houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(),houseFlow.getWorkerTypeId(),1);
            houseWorker.setWorkerId(workerId);
            houseWorker.setWorkType(1);//已抢单
            houseWorker.setIsSelect(1);
            houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }
//    /**
//     * 指定/修改指定工匠
//     * #1.3.1版 指定
//     */
//    public ServerResponse setLockWorker(String houseFlowId, String workerId) {
//        try {
//            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
//            houseFlow.setGrabLock(1);
//            houseFlow.setNominator(workerId);
//            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
//
//            HouseWorker houseWorker=houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(),houseFlow.getWorkerTypeId(),1);
//            houseWorker.setWorkerId(workerId);
//            houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
//            return ServerResponse.createBySuccessMessage("操作成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ServerResponse.createByErrorMessage("操作失败");
//        }
//    }

    /**
     * 指定工匠验证
     *
     * @param memberId 用户登录信息
     * @return
     */
    private ServerResponse setGrabVerification(String memberId, String houseFlowId) {
        try {
            Member member = memberMapper.selectByPrimaryKey(memberId);

            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
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
            House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
            if (house.getVisitState() == 2) {
                return ServerResponse.createByErrorMessage("该房已休眠");
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
                        DateFormat longDateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
                        Date date = new Date();
                        if (date.getTime() < wraprDate.getTime()) {
                            return ServerResponse.createByErrorMessage("该工匠处于平台处罚期内，" + longDateFormat.format(wraprDate) + "以后才能抢单！");
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

            // 抢单详情
            return ServerResponse.createBySuccess("通过验证", "ok");
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
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (HouseWorkerOrder hwo : houseWorkerOrderList) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hwo.getWorkerTypeId());
            Member worker = memberMapper.selectByPrimaryKey(hwo.getWorkerId());
            HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseId, hwo.getWorkerTypeId());
            Map<String, Object> map = new HashMap<>();
            map.put("name", worker.getName());
            map.put("workerId", worker.getId());
            map.put("workerTypeId", worker.getWorkerTypeId());
            map.put("workerTypeName", workerType.getName());
            map.put("mobile", worker.getMobile());
            map.put("createDate", worker.getCreateDate());
            map.put("workSteta", houseFlow.getWorkSteta());
            map.put("payState", hwo.getPayState());//0未支付，1已经支付
            map.put("retentionMoney", hwo.getRetentionMoney());//此单滞留金
            map.put("afterChange", hwo.getAfterChange());//换人后钱
            map.put("totalPrice", hwo.getTotalPrice());//工钱+材料
            map.put("materialPrice", hwo.getMaterialPrice());//材料钱
            map.put("workPrice", hwo.getWorkPrice());//工钱
            map.put("repairPrice", hwo.getRepairPrice());//补人工钱
            map.put("haveMoney", hwo.getHaveMoney());//已拿钱
            map.put("everyMoney", hwo.getEveryMoney());//每日申请累计钱
            map.put("checkMoney", hwo.getCheckMoney());//管家巡查累计
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
        String workerId="";
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (HouseFlow houseFlow : houseFlowList) {
            workerId=houseFlow.getWorkerId();
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
                if(houseWorker!=null&&CommonUtil.isEmpty(workerId)){
                    workerId=houseWorker.getWorkerId();
                }
                map.put("houseWorkerId", houseWorker.getId());
            } else if (houseFlow.getWorkType() == 4) {//已支付
                HouseWorker houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 6);
               if(houseWorker!=null) {
                   if(CommonUtil.isEmpty(workerId)){
                       workerId=houseWorker.getWorkerId();
                   }
                   map.put("houseWorkerId", houseWorker.getId());
               }
            }

            map.put("releaseTime", houseFlow.getReleaseTime());//发布时间
            map.put("workSteta", houseFlow.getWorkSteta());//0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中
            map.put("pause", houseFlow.getPause());//施工状态0正常,1暂停
            map.put("totalPrice", houseFlow.getTotalPrice());//总钱 工钱+材料
            map.put("materialPrice", houseFlow.getMaterialPrice());//材料钱
            map.put("workPrice", houseFlow.getWorkPrice());//工钱
            map.put("patrol", houseFlow.getPatrol());//巡查次数
            map.put("workerId", houseFlow.getWorkerId());//工人ID
            if (houseFlow.getWorkerType() == 1) {//设计
                map.put("designerOk", house.getDesignerOk());
            }
            if (houseFlow.getWorkerType() == 2) {//精算
                map.put("budgetOk", house.getBudgetOk());
            }

            if (houseFlow.getWorkType() > 2&&!CommonUtil.isEmpty(workerId)) {
                Member worker = memberMapper.selectByPrimaryKey(workerId);
                if (worker != null) {
                    map.put("workerName", worker.getName());//工人姓名
                    map.put("mobile", worker.getMobile());//电话
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
            worker.setHead("qrcode/logo.png");
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
            map.put("workSteta", houseFlow.getWorkSteta()); //施工状态，0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中
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
    public ServerResponse getHouseList(Integer pageNum, Integer pageSize, Integer visitState, String searchKey) {
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;

        PageHelper.startPage(pageNum, pageSize);
//        List<House> houseList = houseMapper.selectAll();
        List<House> houseList = houseMapper.getHouseListLikeSearchKey(visitState, searchKey);
        PageInfo pageResult = new PageInfo(houseList);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (House house : houseList) {
            Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
            if (member != null) {
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
                map.put("pause", house.getPause()); //0正常,1暂停
                map.put("createDate", house.getCreateDate());
                map.put("visitState", house.getVisitState()); //0待确认开工,1装修中,2休眠中,3已完工
                mapList.add(map);
            }
        }
        pageResult.setList(mapList);
        return ServerResponse.createBySuccess("查询列表成功", pageResult);
    }

    /**
     * 工匠列表
     */
    public ServerResponse artisanList(String name, String workerTypeId, Integer pageNum, Integer pageSize) {
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;

        try {
            PageHelper.startPage(pageNum, pageSize);
            List<Member> memberList = memberMapper.artisanList(name, workerTypeId);
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
//                artisanDTO.setVolume(member.getVolume());
                Example example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo(HouseWorker.WORKER_ID, member.getId());
                List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);
                artisanDTO.setVolume(houseWorkerList.size());//接单量
                artisanDTO.setRealNameState(member.getRealNameState());
                artisanDTO.setRealNameDescribe(member.getRealNameDescribe());
                artisanDTO.setCheckDescribe(member.getCheckDescribe());

                if (StringUtil.isNotEmpty(member.getSuperiorId())) {
                    Member superior = memberMapper.selectByPrimaryKey(member.getSuperiorId());
                    if (superior != null) {
                        artisanDTO.setSuperior(superior.getName());
                    }
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

}
