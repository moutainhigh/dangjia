package com.dangjia.acg.service.worker;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IBankCardMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.worker.IWithdrawDepositMapper;
import com.dangjia.acg.mapper.worker.IWorkerBankCardMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.modle.worker.WorkerBankCard;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 工匠管理
 * zmj
 */
@Service
public class WorkerService {
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IWithdrawDepositMapper withdrawDepositMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private IWorkerBankCardMapper workerBankCardMapper;
    @Autowired
    private IBankCardMapper bankCardMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;

    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询通讯录
     */
    public ServerResponse getMailList(String userToken, String houseId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        House house = houseMapper.selectByPrimaryKey(houseId);
        List<Map<String, Object>> listMap = new ArrayList<>();//返回通讯录list
        Member member = memberMapper.selectByPrimaryKey(house.getMemberId());//房主
        Map<String, Object> map2 = new HashMap<>();
        map2.put("workerTypeName", "业主");
        map2.put("workerName", member.getNickName() == null ? member.getName() : member.getNickName());
        map2.put("workerPhone", member.getMobile());
        map2.put("workerId", member.getId());
        listMap.add(map2);
        if (worker != null) {
            //管家和工匠都能联系精算和设计
            Example example = new Example(HouseWorker.class);
            example.createCriteria()
                    .andEqualTo(HouseWorker.HOUSE_ID, houseId)
                    .andEqualTo(HouseWorker.WORK_TYPE, 6)
                    .andCondition(" worker_type in(1,2) ");
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);
            for (HouseWorker houseWorker : houseWorkerList) {
                Map<String, Object> map = new HashMap<>();
                Member worker2 = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                if (worker2 == null) {
                    continue;
                }
                map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(worker2.getWorkerTypeId()).getName());
                map.put("workerName", worker2.getName());
                map.put("workerPhone", worker2.getMobile());
                map.put("workerId", worker2.getId());
                listMap.add(map);
            }
            if (worker.getWorkerType() == 3) {//大管家
                List<HouseWorker> listHouseWorker = houseWorkerMapper.paidListByHouseId(houseId);
                for (HouseWorker houseWorker : listHouseWorker) {
                    Map<String, Object> map = new HashMap<>();
                    Member worker2 = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                    if (worker2 == null) {
                        continue;
                    }
                    map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(worker2.getWorkerTypeId()).getName());
                    map.put("workerName", worker2.getName());
                    map.put("workerPhone", worker2.getMobile());
                    map.put("workerId", worker2.getId());
                    listMap.add(map);
                }
            } else {//普通工匠
                HouseWorker houseWorker = houseWorkerMapper.getHwByHidAndWtype(houseId, 3);
                Map<String, Object> map = new HashMap<String, Object>();
                Member worker2 = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());//根据工匠id查询工匠信息详情
                map.put("workerTypeName", "大管家");
                map.put("workerName", worker2.getName());//大管家
                map.put("workerPhone", worker2.getMobile());
                map.put("workerId", worker2.getId());
                listMap.add(map);
            }


        }
        return ServerResponse.createBySuccess("获取成功", listMap);
    }

    /**
     * 我的资料
     *
     * @param userToken
     * @return
     */
    public ServerResponse getWorker(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        return ServerResponse.createBySuccess("获取工匠基本信息成功", worker);
    }

    /**
     * 提现记录
     *
     * @param userToken
     * @return
     */
    public ServerResponse getWithdrawDeposit(String userToken, PageDTO pageDTO) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(WithdrawDeposit.class);
        example.createCriteria().andEqualTo(WithdrawDeposit.WORKER_ID, worker.getId());
        example.orderBy(WithdrawDeposit.CREATE_DATE).desc();
        List<WithdrawDeposit> wdList = withdrawDepositMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(wdList);
        return ServerResponse.createBySuccess("获取工匠提现记录成功", pageResult);
    }

    /**
     * 我的任务
     *
     * @param userToken
     * @return
     */
    public ServerResponse getHouseWorkerList(String userToken, PageDTO pageDTO) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        Example example = new Example(HouseFlow.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(HouseFlow.WORKER_ID, worker.getId());
        if (worker.getWorkerType() == 3) {
            criteria.andEqualTo(HouseFlow.SUPERVISOR_START, 1);
        } else if (worker.getWorkerType() != 1 && worker.getWorkerType() != 2) {
            criteria.andCondition(" work_steta not in(0,3)");
        }
        //获取我的任务的房子Id
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<HouseFlow> houseId = houseFlowMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(houseId);
        List<HouseWorkerOrder> hwList = new ArrayList<>();
        for (HouseFlow houseFlow : houseId) {
            Example example1 = new Example(HouseWorkerOrder.class);
            example1.createCriteria().andEqualTo(HouseWorkerOrder.WORKER_ID, worker.getId())
                    .andEqualTo(HouseWorkerOrder.HOUSE_ID, houseFlow.getHouseId());
            example1.orderBy(HouseFlow.CREATE_DATE).desc();
            hwList.addAll(houseWorkerOrderMapper.selectByExample(example1));
        }
        List<Map> hwMapList = new ArrayList<>();
        for (HouseWorkerOrder hw : hwList) {
            Map hwMap = BeanUtils.beanToMap(hw);
            House house = houseMapper.selectByPrimaryKey(hw.getHouseId());
            if (house != null) {
                Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                if (member != null) {
                    hwMap.put(Member.NICK_NAME, member.getNickName());
                }
                hwMap.put("houseName", house.getHouseName());
                hwMap.put("buildSquare", house.getBuildSquare());
                Long suspendDay = houseFlowApplyMapper.getSuspendApply(house.getId(), worker.getId());//根据房子id和工人id查询暂停天数
                Long everyEndDay = houseFlowApplyMapper.getEveryDayApply(house.getId(), worker.getId());//根据房子id和工人id查询每日完工申请天数
                long totalDay = 0;
                List<HouseFlowApply> earliestTime = houseFlowApplyMapper.getEarliestTimeHouseApply(house.getId(), worker.getId());//查询最早的每日开工申请
                if (earliestTime != null && earliestTime.size() > 0) {
                    Date EarliestDay = earliestTime.get(0).getCreateDate();//最早开工时间
                    Example example1 = new Example(HouseFlowApply.class);
                    example1.createCriteria().andEqualTo(HouseFlowApply.WORKER_ID, worker.getId())
                            .andEqualTo(HouseFlowApply.HOUSE_ID, house.getId()).andEqualTo(HouseFlowApply.APPLY_TYPE, 2);
                    List<HouseFlowApply> houseFlowApplies = houseFlowApplyMapper.selectByExample(example1);
                    Date newDate = new Date();
                    if (houseFlowApplies.size() > 0) {
                        newDate = houseFlowApplies.get(0).getModifyDate();
                    }
                    long num = 1 + DateUtil.daysofTwo(EarliestDay, newDate);//计算当前时间隔最早开工时间相差多少天
                    if (suspendDay == null) {
                        totalDay = num;//总开工天数
                    } else {
                        long aa = num - suspendDay;
                        if (aa >= 0) {
                            totalDay = aa;
                        }
                    }
                }
                hwMap.put("visitState", house.getVisitState());
                hwMap.put("suspendDay", suspendDay == null ? 0 : suspendDay);//暂停天数
                hwMap.put("everyEndDay", everyEndDay == null ? 0 : everyEndDay);//每日完工申请天数
                hwMap.put("totalDay", totalDay);//总开工数
            }
            hwMapList.add(hwMap);
        }
        pageResult.setList(hwMapList);
        return ServerResponse.createBySuccess("获取我的任务成功", pageResult);
    }

    /**
     * 我的任务-详情流水
     *
     * @param userToken
     * @return
     */
    public ServerResponse getHouseWorkerDetail(String userToken, PageDTO pageDTO, String houseId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(WorkerDetail.class);
        example.createCriteria()
                .andEqualTo(WorkerDetail.WORKER_ID, worker.getId())
                .andEqualTo(WorkerDetail.HOUSE_ID, houseId);
        example.orderBy(WorkerDetail.CREATE_DATE).desc();
        List<WorkerDetail> hwList = workerDetailMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(hwList);
        return ServerResponse.createBySuccess("获取我的任务成功", pageResult);
    }

    /**
     * 我的银行卡
     *
     * @param userToken
     * @return
     */
    public ServerResponse getMyBankCard(String userToken,String userId) {

        String id = null;
        //如果 userToken 为空，代表中台登录 供应商，店铺 添加银行卡 2019/11/7 添加
        if (CommonUtil.isEmpty(userToken)) {
            if (CommonUtil.isEmpty(userId)) {
                return ServerResponse.createByErrorMessage("userId不能为空");
            }
            id = userId;
        }else{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            id = worker.getId();
        }

        Example example = new Example(WorkerBankCard.class);
        example.createCriteria().andEqualTo(WorkerBankCard.WORKER_ID, id)
                .andEqualTo(WorkerBankCard.DATA_STATUS, 0);
        List<WorkerBankCard> wbList = workerBankCardMapper.selectByExample(example);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (WorkerBankCard wb : wbList) {
            Map<String, Object> map = BeanUtils.beanToMap(wb);
            BankCard bankCard = bankCardMapper.selectByPrimaryKey(wb.getBankCardId());
            if (bankCard != null) {
                bankCard.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                map.put("bankCardName", bankCard.getBankName());//银行名称
                map.put("bankCardImage", bankCard.getBankCardImage());//银行卡图片
            }
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("获取我的银行卡成功", mapList);
    }

    /**
     * 新增银行卡
     *
     * @param bankCard
     * @return
     */
    public ServerResponse addMyBankCard(HttpServletRequest request, String userToken, WorkerBankCard bankCard,String userId) {
        try {
            if (CommonUtil.isEmpty(bankCard.getBankCardNumber())) {
                return ServerResponse.createByErrorMessage("请输入银行卡卡号");
            }
            if (CommonUtil.isEmpty(bankCard.getBankCardId())) {
                return ServerResponse.createByErrorMessage("请选择银行卡类型");
            }

            String id = null;
            //如果 userToken 为空，代表中台登录 供应商，店铺 添加银行卡 2019/11/7 添加
            if (CommonUtil.isEmpty(userToken)) {
                if (CommonUtil.isEmpty(userId)) {
                    return ServerResponse.createByErrorMessage("userId不能为空");
                }
                id = userId;
            }else{
                Object object = constructionService.getMember(userToken);
                if (object instanceof ServerResponse) {
                    return (ServerResponse) object;
                }
                Member worker = (Member) object;
                id = worker.getId();
            }

            Example example = new Example(WorkerBankCard.class);
            example.createCriteria()
                    .andEqualTo(WorkerBankCard.BANK_CARD_NUMBER, bankCard.getBankCardNumber())
                    .andEqualTo(WorkerBankCard.WORKER_ID, id)
                    .andEqualTo(WorkerBankCard.DATA_STATUS, 0);
            if (workerBankCardMapper.selectByExample(example).size() > 0) {
                return ServerResponse.createByErrorMessage("添加失败，银行卡已被使用！");
            }
            bankCard.setWorkerId(id);
            bankCard.setDataStatus(0);
            bankCard.setStatus(0);
            this.workerBankCardMapper.insertSelective(bankCard);
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("操作失败，请您稍后再试");
        }
    }

    /**
     * 删除银行卡
     *
     * @param request
     * @param userToken
     * @param workerBankCardId 银行卡ID
     * @return
     */
    public ServerResponse delMyBankCard(HttpServletRequest request, String userToken, String workerBankCardId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            WorkerBankCard workerBankCard = workerBankCardMapper.selectByPrimaryKey(workerBankCardId);
            if (workerBankCard == null) {
                return ServerResponse.createByErrorMessage("没有找到对应的银行卡");
            }
            if (!worker.getId().equals(workerBankCard.getWorkerId())) {
                return ServerResponse.createByErrorMessage("您无权删除此卡");
            }
            workerBankCard.setDataStatus(1);
            workerBankCardMapper.updateByPrimaryKeySelective(workerBankCard);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("操作失败，请您稍后再试");
        }
    }

    /**
     * 解绑银行卡
     * @param userId
     * @param workerBankCardId 银行卡ID
     * @return
     */
    public ServerResponse untyingBankCard(String userId, String workerBankCardId,String payPassword) {
        try {
            if (CommonUtil.isEmpty(userId)) {
                return ServerResponse.createByErrorMessage("userId不能为空");
            }
            if (CommonUtil.isEmpty(payPassword)) {
                return ServerResponse.createByErrorMessage("支付密码必输");
            }

            //查询用户信息
            MainUser mainUser = userMapper.selectByPrimaryKey(userId);

            if(mainUser == null){
                return ServerResponse.createByErrorMessage("用户不存在");
            }
            if(!mainUser.getPayPassword().equals(payPassword)){
                return ServerResponse.createByErrorMessage("解绑失败,支付密码错误");
            }

            WorkerBankCard workerBankCard = workerBankCardMapper.selectByPrimaryKey(workerBankCardId);
            if (workerBankCard == null) {
                return ServerResponse.createByErrorMessage("没有找到对应的银行卡");
            }

            workerBankCard.setDataStatus(1);
            workerBankCardMapper.updateByPrimaryKeySelective(workerBankCard);
            return ServerResponse.createBySuccessMessage("解绑成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("操作失败，请您稍后再试");
        }
    }

    /**
     * 邀请排行榜
     *
     * @param userToken
     * @return
     */
    public ServerResponse getRanking(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        Example example = new Example(Member.class);
        example.createCriteria().andEqualTo("superiorId", worker.getId());
        List<Member> workerList = memberMapper.selectByExample(example);
        workerList.add(worker);
        for (Member w : workerList) {
            Example example2 = new Example(Member.class);
            example2.createCriteria().andEqualTo("superiorId", w.getId());
            List<Member> mList = memberMapper.selectByExample(example);
            w.setInviteNum(mList.size());
        }
        Collections.sort(workerList, (w1, w2) -> (w2.getInviteNum() - w1.getInviteNum()));
        return ServerResponse.createBySuccess("获取邀请排行榜成功", workerList);
    }

    /**
     * 接单记录
     *
     * @param userToken
     * @return
     */
    public ServerResponse getTakeOrder(String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            List<HouseWorkerOrder> houseId = getHouseId(worker.getId(), worker.getWorkerType());
            List<HouseWorker> hwList = new ArrayList<>();
            for (HouseWorkerOrder houseWorkerOrder : houseId) {
                Example example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo("workerId", worker.getId())
                        .andEqualTo(HouseWorker.HOUSE_ID, houseWorkerOrder.getHouseId());
                hwList.addAll(houseWorkerMapper.selectByExample(example));
            }
            return ServerResponse.createBySuccess("获取接单记录成功", hwList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取接单记录失败");
        }
    }


    /**
     * 获取我的任务的房子
     *
     * @param workerId
     * @param workerType
     * @return
     */
    public List<HouseWorkerOrder> getHouseId(String workerId, int workerType) {
        Example example = new Example(HouseFlow.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(HouseFlow.WORKER_ID, workerId);
        if (workerType == 3) {
            criteria.andEqualTo(HouseFlow.SUPERVISOR_START, 1);
        } else if (workerType != 1 && workerType != 2) {
            criteria.andCondition(" work_steta not in(0,3)");
        }
        //获取我的任务的房子Id
        List<HouseFlow> houseId = houseFlowMapper.selectByExample(example);
        List<HouseWorkerOrder> hwList = new ArrayList<>();
        for (HouseFlow houseFlow : houseId) {
            Example example1 = new Example(HouseWorkerOrder.class);
            example1.createCriteria().andEqualTo(HouseWorkerOrder.WORKER_ID, workerId)
                    .andEqualTo(HouseWorkerOrder.HOUSE_ID, houseFlow.getHouseId());
            example1.orderBy(HouseFlow.CREATE_DATE).desc();
            hwList.addAll(houseWorkerOrderMapper.selectByExample(example1));
        }
        return hwList;
    }

}
