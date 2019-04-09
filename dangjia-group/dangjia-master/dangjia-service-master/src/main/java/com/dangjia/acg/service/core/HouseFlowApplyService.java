package com.dangjia.acg.service.core;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HouseFlowApplyDTO;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.mapper.worker.IEvaluateMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.modle.worker.Evaluate;
import com.dangjia.acg.modle.worker.WorkIntegral;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/26 0026
 * Time: 16:05
 */
@Service
public class HouseFlowApplyService {

    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IEvaluateMapper evaluateMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;
    @Autowired
    private IWorkerTypeSafeMapper workerTypeSafeMapper;
    @Autowired
    private IWorkIntegralMapper workIntegralMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    /*@Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private RedisClient redisClient;*/
    @Autowired
    private IChangeOrderMapper changeOrderMapper;

    /**
     * 工匠端工地记录
     */
    public ServerResponse houseRecord(String userToken, String houseId, Integer pageNum, Integer pageSize) {
        try {
            //        Member worker = accessToken.getMember();
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            Map<Integer, String> applyTypeMap = new HashMap<>();
            applyTypeMap.put(DjConstants.ApplyType.MEIRI_WANGGONG, "每日完工");
            applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG, "阶段完工");
            applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG, "整体完工");
            applyTypeMap.put(DjConstants.ApplyType.TINGGONG, "停工");
            applyTypeMap.put(DjConstants.ApplyType.MEIRI_KAIGONG, "每日开工");
            applyTypeMap.put(DjConstants.ApplyType.YOUXIAO_XUNCHA, "巡查");
            applyTypeMap.put(DjConstants.ApplyType.WUREN_XUNCHA, "巡查");
            applyTypeMap.put(DjConstants.ApplyType.ZUIJIA_XUNCHA, "巡查");

            List<HouseFlowApplyDTO> houseFlowApplyDTOList = new ArrayList<HouseFlowApplyDTO>();
            HouseWorker gjhouseWorker = houseWorkerMapper.getHwByHidAndWtype(houseId, 3);
            Member worker2 = memberMapper.selectByPrimaryKey(gjhouseWorker.getWorkerId());//根据工匠id查询工匠信息详情
            PageHelper.startPage(pageNum, pageSize);
            List<HouseWorker> listHouseWorker = houseWorkerMapper.paidListByHouseId(houseId);
            for (HouseWorker houseWorker : listHouseWorker) {
                Member member = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                HouseFlowApplyDTO houseFlowApplyDTO = new HouseFlowApplyDTO();
                houseFlowApplyDTO.setWorkerTypeName(workerTypeMapper.selectByPrimaryKey(houseWorker.getWorkerTypeId()).getName());
                houseFlowApplyDTO.setNameA(member.getName());
                houseFlowApplyDTO.setMobileB(member.getMobile());
                houseFlowApplyDTO.setWorkerId(houseWorker.getWorkerId());
                houseFlowApplyDTO.setManagerId(member.getId());
                Example example = new Example(HouseFlowApply.class);
                example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, houseId).andEqualTo(HouseFlowApply.WORKER_ID, member.getId());
                List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.selectByExample(example);
                List<Map> mapList = new ArrayList<>();
                for (HouseFlowApply hfa : houseFlowApplyList) {
                    Map map = new HashMap();
                    map.put(HouseFlowApply.CREATE_DATE, hfa.getCreateDate());
                    map.put(Member.NAME, worker2.getName());
                    map.put(HouseFlowApply.APPLY_TYPE + "Name", applyTypeMap.get(hfa.getApplyType()));
                    mapList.add(map);
                }
                houseFlowApplyDTO.setList(mapList);
                houseFlowApplyDTOList.add(houseFlowApplyDTO);
            }
            PageInfo pageResult = new PageInfo(houseFlowApplyDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 业主审核工匠
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse checkWorker(String houseFlowApplyId) {
        try {
            //HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HouseFlowApply hfa = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            if (hfa.getMemberCheck() == 1) {
                return ServerResponse.createByErrorMessage("重复审核");
            }
            //工匠订单
            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hfa.getHouseId(), hfa.getWorkerTypeId());

            if (hfa.getApplyType() == 2) {//整体完工
                /**验证未处理补人工订单*/
                List<ChangeOrder> changeOrderList = changeOrderMapper.unCheckOrder(hfa.getHouseId(), hfa.getWorkerTypeId());
                if (changeOrderList.size() > 0) {
                    return ServerResponse.createByErrorMessage("该工种有未处理人工变更单,通知管家处理");
                }

                //修改进程
                HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(hwo.getHouseId(), hwo.getWorkerTypeId());
                houseFlow.setWorkSteta(2);
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                //处理工人拿钱
                workerMoney(hwo, hfa);
                //大管家拿钱
                stewardMoney(hfa);

                //设置保险时间
                WorkerTypeSafeOrder wtso = workerTypeSafeOrderMapper.getByWorkerTypeId(hwo.getWorkerTypeId(), hwo.getHouseId());
                if (wtso != null) {
                    WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(wtso.getWorkerTypeSafeId());//获得类型算出时间
                    int month = (wts.getMonth()); //获取保险时长(月)
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, month);
                    wtso.setForceTime(new Date());//设置生效时间
                    wtso.setExpirationDate(cal.getTime()); //设置到期时间
                    workerTypeSafeOrderMapper.updateByPrimaryKeySelective(wtso);
                }

                if (hfa.getWorkerType() == 4) {//是拆除的整体完工通知下个工种开工
                    //查下工种
                    List<HouseFlow> houseFlowList = houseFlowMapper.getNextHouseFlow(houseFlow.getSort(), houseFlow.getHouseId());
                    if (houseFlowList.size() > 0) {//有下一个工种
                        HouseFlow nextHouseFlow = houseFlowList.get(0);
                        if (nextHouseFlow.getWorkType() == 1) {//下个工种还没有开工，让它变成被抢壮态
                            nextHouseFlow.setWorkType(2);
                            nextHouseFlow.setReleaseTime(new Date());//发布时间
                            houseFlowMapper.updateByPrimaryKeySelective(nextHouseFlow);

                        }
                    }
                }

            } else if (hfa.getApplyType() == 1) {//阶段完工
                List<ChangeOrder> changeOrderList = changeOrderMapper.unCheckOrder(hfa.getHouseId(), hfa.getWorkerTypeId());
                if (changeOrderList.size() > 0) {
                    return ServerResponse.createByErrorMessage("该工种有未处理人工变更单,通知管家处理");
                }

                //修改进程
                HouseFlow hf = houseFlowMapper.getByWorkerTypeId(hwo.getHouseId(), hwo.getWorkerTypeId());
                hf.setWorkSteta(1);
                houseFlowMapper.updateByPrimaryKeySelective(hf);

                //处理押金
                deposit(hfa);
                //处理工人拿钱
                workerMoney(hwo, hfa);
                //大管家拿钱
                stewardMoney(hfa);

                //查下工种
                List<HouseFlow> houseFlowList = houseFlowMapper.getNextHouseFlow(hf.getSort(), hf.getHouseId());
                if (houseFlowList.size() > 0) {
                    if (houseFlowList.get(0).getWorkType() == 1) {//下个工种还没有开工，让它变成被抢壮态
                        houseFlowList.get(0).setWorkType(2);
                        houseFlowList.get(0).setReleaseTime(new Date());//发布时间
                        houseFlowMapper.updateByPrimaryKeySelective(houseFlowList.get(0));
                    }
                }
            } else { //每日完工,处理钱
                //算每日积分
                updateDayIntegral(hfa);

                if (hwo.getHaveMoney() == null) {//已获工钱
                    hwo.setHaveMoney(new BigDecimal(0.0));
                }
                hwo.setHaveMoney(hwo.getHaveMoney().add(hfa.getApplyMoney()));
                hwo.setEveryMoney(hwo.getEveryMoney().add(hfa.getApplyMoney()));//每日完工得到的钱，同时每日完工得到不会再清空
                houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);

                //记录流水
                Member worker = memberMapper.selectByPrimaryKey(hwo.getWorkerId());
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("每日完工");
                workerDetail.setWorkerId(hwo.getWorkerId());
                workerDetail.setWorkerName(worker.getName());
                workerDetail.setHouseId(hwo.getHouseId());
                workerDetail.setMoney(hfa.getApplyMoney());
                workerDetail.setHaveMoney(hwo.getHaveMoney());
                workerDetail.setHouseWorkerOrderId(hwo.getId());
                workerDetail.setApplyMoney(hfa.getApplyMoney());
                workerDetail.setWalletMoney(worker.getHaveMoney());
                workerDetail.setState(0);//进钱
                workerDetailMapper.insert(workerDetail);

                //处理工钱
                if (worker.getHaveMoney() == null) {//工人已获取
                    worker.setHaveMoney(new BigDecimal(0.0));
                }
                worker.setHaveMoney(worker.getHaveMoney().add(hfa.getApplyMoney()));

                if (worker.getSurplusMoney() == null) {//可取余额 赋初始值为0
                    worker.setSurplusMoney(new BigDecimal(0.0));
                }
                if (worker.getRetentionMoney() == null) {
                    worker.setRetentionMoney(new BigDecimal(0.0));
                }
                BigDecimal mid = worker.getHaveMoney().subtract(worker.getRetentionMoney());
                if (mid.compareTo(BigDecimal.ZERO) == 1) {//大于0
                    worker.setSurplusMoney(mid);
                } else {
                    worker.setSurplusMoney(BigDecimal.ZERO);
                }
                memberMapper.updateByPrimaryKeySelective(worker);
            }
            hfa.setMemberCheck(1);
            hfa.setPayState(1);
            houseFlowApplyMapper.updateByPrimaryKeySelective(hfa);

            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("审核失败");
        }
    }

    /**
     * 每日完工计分
     */
    private void updateDayIntegral(HouseFlowApply houseFlowApply) {
        try {
            Member worker = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
            BigDecimal score = new BigDecimal("0.05");
            if (worker.getEvaluationScore().compareTo(new BigDecimal("70")) == -1) {

                score = score.multiply(new BigDecimal("1.6"));
            } else if ((worker.getEvaluationScore().compareTo(new BigDecimal("70")) == 1 ||
                    worker.getEvaluationScore().compareTo(new BigDecimal("70")) == 0) &&
                    worker.getEvaluationScore().compareTo(new BigDecimal("80")) == -1) {

                score = score.multiply(new BigDecimal("0.8"));
            } else if (worker.getEvaluationScore().compareTo(new BigDecimal("80")) >= 0 &&
                    worker.getEvaluationScore().compareTo(new BigDecimal("90")) == -1) {

                score = score.multiply(new BigDecimal("0.4"));
            } else if (worker.getEvaluationScore().compareTo(new BigDecimal("90")) >= 0) {

                score = score.multiply(new BigDecimal("0.2"));
            }

            if (worker.getEvaluationScore() == null) {
                worker.setEvaluationScore(new BigDecimal("60.0"));
            }
            worker.setEvaluationScore(worker.getEvaluationScore().add(score));
            memberMapper.updateByPrimaryKeySelective(worker);

            if (score.compareTo(new BigDecimal("0")) == 1) {
                WorkIntegral workIntegral = new WorkIntegral();
                workIntegral.setWorkerId(worker.getId());
                workIntegral.setHouseId(houseFlowApply.getHouseId());
                workIntegral.setStatus(0);
                workIntegral.setIntegral(score);
                workIntegral.setBriefed("每日完工");
                workIntegralMapper.insert(workIntegral);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 普通工匠的阶段完工、整体完工审核通过后
     * 大管家拿相应验收收入
     **/
    public void stewardMoney(HouseFlowApply hfa) {
        //这是工匠的houseFlowId
        String houseFlowId = hfa.getHouseFlowId();

        //订单累计大管家工钱
        HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hfa.getHouseId(), "3");
        //查大管家被业主的评价
        Evaluate evaluate = evaluateMapper.getForCountMoney(houseFlowId, hfa.getApplyType(), hwo.getWorkerId());

        int star;
        if (evaluate == null) {
            star = 0;
        } else {
            star = evaluate.getStar();//几星？
        }
        /*****兼容老工地该字段没有初始值*****/
        if (hfa.getSupervisorMoney() == null) {
            hfa.setSupervisorMoney(new BigDecimal(0));
        }

        BigDecimal supervisorMoney;
        if (star == 0 || star == 5) {
            supervisorMoney = hfa.getSupervisorMoney();//大管家的验收收入
        } else if (star == 3 || star == 4) {
            supervisorMoney = hfa.getSupervisorMoney().multiply(new BigDecimal(0.8));
        } else {
            supervisorMoney = new BigDecimal(0);//为0元
        }

        hwo.setHaveMoney(hwo.getHaveMoney().add(supervisorMoney));
        //大管家验收钱
        hwo.setEveryMoney(hwo.getEveryMoney().add(supervisorMoney));
        houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);

        //钱包处理大管家的工钱
        Member worker = memberMapper.selectByPrimaryKey(hwo.getWorkerId());

        //管家押金处理
        HouseFlowApply houseFlowApply = new HouseFlowApply();
        houseFlowApply.setWorkerType(3);
        houseFlowApply.setWorkerId(worker.getId());
        houseFlowApply.setWorkerTypeId(worker.getWorkerTypeId());
        houseFlowApply.setHouseId(hwo.getHouseId());
        deposit(houseFlowApply);

        //记录流水
        WorkerDetail workerDetail = new WorkerDetail();
        if (hfa.getApplyType() == 2) {//整体完工申请
            workerDetail.setName("整体验收收入");
        } else if (hfa.getApplyType() == 1) {
            workerDetail.setName("阶段验收收入");
        }
        workerDetail.setWorkerId(worker.getId());
        workerDetail.setWorkerName(worker.getName());
        workerDetail.setHouseId(hwo.getHouseId());
        workerDetail.setMoney(supervisorMoney);//实际收入
        workerDetail.setState(0);//进钱
        workerDetail.setHaveMoney(hwo.getHaveMoney());
        workerDetail.setHouseWorkerOrderId(hwo.getId());
        workerDetail.setApplyMoney(hfa.getSupervisorMoney());//管家应拿的验收收入
        workerDetail.setWalletMoney(worker.getHaveMoney());
        workerDetailMapper.insert(workerDetail);
        //处理工钱
        if (worker.getHaveMoney() == null) {//工人已获取
            worker.setHaveMoney(new BigDecimal(0.0));
        }
        worker.setHaveMoney(worker.getHaveMoney().add(supervisorMoney));

        if (worker.getSurplusMoney() == null) {//可取余额 赋初始值为0
            worker.setSurplusMoney(new BigDecimal(0.0));
        }
        BigDecimal mid = worker.getHaveMoney().subtract(worker.getRetentionMoney());//可取等于 获得减押金
        if (mid.compareTo(BigDecimal.ZERO) == 1) {//大于0
            worker.setSurplusMoney(mid);
        } else {
            worker.setSurplusMoney(BigDecimal.ZERO);
        }
        memberMapper.updateByPrimaryKeySelective(worker);
    }

    /**
     * 处理工人押金
     */
    public void deposit(HouseFlowApply hfa) {
        if (hfa.getWorkerType() > 5 || hfa.getWorkerType() == 3) {//收滞留金
            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hfa.getHouseId(), hfa.getWorkerTypeId());
            Member worker = memberMapper.selectByPrimaryKey(hwo.getWorkerId());
            /*
             * 工匠积分70分以下每单滞留金无上限,70以上含80以下2000元
             * 80含以上90以下1500元,90分含以上500元
             */
            if (worker.getEvaluationScore().compareTo(new BigDecimal(70)) >= 0 && worker.getEvaluationScore().compareTo(new BigDecimal(80)) < 0) {
                worker.setDeposit(new BigDecimal(2000));//设置滞留金2000元
                //实际滞留金减上限
                BigDecimal bd = worker.getRetentionMoney().subtract(worker.getDeposit());
                if (bd.compareTo(BigDecimal.ZERO) == 1) {//实际多于2000元退押金
                    //记录流水
                    //WorkerDetail workerDetail = new WorkerDetail();
                    /*workerDetail.setName("涨积分退滞留金");
                    workerDetail.setWorkerId(worker.getId());
                    workerDetail.setWorkerName(worker.getName());
                    workerDetail.setHouseId(hwo.getHouseId());
                    workerDetail.setMoney(bd);
                    workerDetail.setState(0);//进钱
                    workerDetailMapper.insert(workerDetail);*/
                    worker.setRetentionMoney(worker.getDeposit());//实际2000元
                    worker.setSurplusMoney(worker.getSurplusMoney().add(bd));
                }
            } else if (worker.getEvaluationScore().compareTo(new BigDecimal(80)) >= 0 && worker.getEvaluationScore().compareTo(new BigDecimal(90)) < 0) {
                worker.setDeposit(new BigDecimal(1500));//设置滞留金上限1500元
                //实际滞留金减上限
                BigDecimal bd = worker.getRetentionMoney().subtract(worker.getDeposit());
                if (bd.compareTo(BigDecimal.ZERO) == 1) {
                    //记录流水
                    /*WorkerDetail workerDetail = new WorkerDetail();
                    workerDetail.setName("涨积分退滞留金 ");
                    workerDetail.setWorkerId(worker.getId());
                    workerDetail.setWorkerName(worker.getName());
                    workerDetail.setHouseId(hwo.getHouseId());
                    workerDetail.setMoney(bd);
                    workerDetail.setState(0);//进钱
                    workerDetailMapper.insert(workerDetail);*/
                    worker.setRetentionMoney(worker.getDeposit());//实际2000元
                    worker.setSurplusMoney(worker.getSurplusMoney().add(bd));
                }
            } else if (worker.getEvaluationScore().compareTo(new BigDecimal(90)) >= 0) {
                worker.setDeposit(new BigDecimal(500));//设置滞留金上限500元
                //实际滞留金减上限
                BigDecimal bd = worker.getRetentionMoney().subtract(worker.getDeposit());
                if (bd.compareTo(BigDecimal.ZERO) == 1) {
                   /* //记录流水
                    WorkerDetail workerDetail = new WorkerDetail();
                    workerDetail.setName(" 涨积分退滞留金");
                    workerDetail.setWorkerId(worker.getId());
                    workerDetail.setWorkerName(worker.getName());
                    workerDetail.setHouseId(hwo.getHouseId());
                    workerDetail.setMoney(bd);
                    workerDetail.setState(0);//进钱
                    workerDetailMapper.insert(workerDetail);*/
                    worker.setRetentionMoney(worker.getDeposit());//实际2000元
                    worker.setSurplusMoney(worker.getSurplusMoney().add(bd));
                }
            } else {
                worker.setDeposit(new BigDecimal(99999));//重新设置无上限
            }

            //BigDecimal deposit = workDepositService.getWorkDepositByList().getDeposit();//获取押金比例 5%
            BigDecimal deposit = new BigDecimal(0.05);
            if (worker.getRetentionMoney() == null) {
                worker.setRetentionMoney(new BigDecimal(0.0));
            }
            if (worker.getRetentionMoney().compareTo(worker.getDeposit()) == -1 && hwo.getRetentionMoney() == null) {//押金没收够并且没有算过押金
                //算订单的5%
                BigDecimal mid = hwo.getWorkPrice().multiply(deposit);
                if (worker.getRetentionMoney().add(mid).compareTo(worker.getDeposit()) == -1 ||
                        worker.getRetentionMoney().add(mid).compareTo(worker.getDeposit()) == 0) {
                    //实际滞留金
                    hwo.setRetentionMoney(mid);
                    worker.setRetentionMoney(worker.getRetentionMoney().add(mid));
                } else {
                    mid = worker.getDeposit().subtract(worker.getRetentionMoney());//只收这么多了
                    hwo.setRetentionMoney(mid);
                    worker.setRetentionMoney(worker.getRetentionMoney().add(mid));
                }
                houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
                memberMapper.updateByPrimaryKeySelective(worker);
            }
        }
    }

    /**
     * 工人拿钱
     */
    private void workerMoney(HouseWorkerOrder hwo, HouseFlowApply hfa) {
        try {
            Member worker = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
            //处理工人得到的钱
            if (hwo.getHaveMoney() == null) {
                hwo.setHaveMoney(new BigDecimal(0.0));
            }
            HouseFlow hf = houseFlowMapper.getByWorkerTypeId(hwo.getHouseId(), hwo.getWorkerTypeId());

            //查工匠被管家的评价
            Evaluate e1 = evaluateMapper.getForCountMoneySup(hf.getId(), hfa.getApplyType(), hwo.getWorkerId());
            //查工匠被业主的评价
            Evaluate e2 = evaluateMapper.getForCountMoney(hf.getId(), hfa.getApplyType(), hwo.getWorkerId());
            int star1;
            int star2;
            if (e1 == null) {
                star1 = 5;
            } else {
                star1 = e1.getStar();//几星？
            }
            if (e2 == null) {
                star2 = 5;
            } else {
                star2 = e2.getStar();//几星？
            }
            Double star = (double) ((star1 + star2) / 2);
            //工人钱
            BigDecimal applymoney = new BigDecimal(0);
            if (star >= 4) {
                applymoney = hfa.getApplyMoney();
            } else if (star > 2) {
                applymoney = hfa.getApplyMoney().multiply(new BigDecimal(0.97));
            } else if (star <= 2) {
                applymoney = hfa.getApplyMoney().multiply(new BigDecimal(0.95));
            }

            hwo.setHaveMoney(hwo.getHaveMoney().add(applymoney));
            houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);

            //记录流水
            WorkerDetail workerDetail = new WorkerDetail();
            if (hfa.getApplyType() == 2) {//整体完工申请
                workerDetail.setName("整体完工");
                if (hwo.getRepairPrice().compareTo(new BigDecimal(0)) > 0) {
                    /*有补的人工钱加入工人流水*/
                    this.workerDetailRepair(hwo);
                }
            } else {
                workerDetail.setName("阶段完工");
            }
            workerDetail.setWorkerId(hwo.getWorkerId());
            workerDetail.setWorkerName(worker.getName());
            workerDetail.setHouseId(hwo.getHouseId());
            workerDetail.setMoney(applymoney);
            workerDetail.setState(0);//进钱
            workerDetail.setHaveMoney(hwo.getHaveMoney());
            workerDetail.setHouseWorkerOrderId(hwo.getId());
            workerDetail.setApplyMoney(hfa.getApplyMoney());
            workerDetail.setWalletMoney(worker.getHaveMoney());
            workerDetailMapper.insert(workerDetail);


            if (worker.getHaveMoney() == null) {//工人已获取
                worker.setHaveMoney(new BigDecimal(0.0));
            }
            worker.setHaveMoney(worker.getHaveMoney().add(applymoney));

            if (worker.getSurplusMoney() == null) {//可取余额 赋初始值为0
                worker.setSurplusMoney(new BigDecimal(0.0));
            }
            BigDecimal mid = worker.getHaveMoney().subtract(worker.getRetentionMoney());//可取等于 获得减押金
            if (mid.compareTo(BigDecimal.ZERO) == 1) {//大于0
                worker.setSurplusMoney(mid);
            } else {
                worker.setSurplusMoney(BigDecimal.ZERO);
            }

            //成交量加1
            if (hfa.getApplyType() == 2) {
                if (worker.getVolume() == null) {
                    worker.setVolume(new BigDecimal(0.0));
                }
                worker.setVolume(worker.getVolume().add(new BigDecimal(1)));
            }
            memberMapper.updateByPrimaryKeySelective(worker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 补人工算钱
     */
    private void workerDetailRepair(HouseWorkerOrder hwo) {
        Member worker = memberMapper.selectByPrimaryKey(hwo.getWorkerId());
        WorkerDetail workerDetail = new WorkerDetail();
        workerDetail.setName("补人工钱");
        workerDetail.setWorkerId(hwo.getWorkerId());
        workerDetail.setWorkerName(worker.getName());
        workerDetail.setHouseId(hwo.getHouseId());
        workerDetail.setMoney(hwo.getRepairPrice());
        workerDetail.setState(0);//进钱
        workerDetail.setHaveMoney(hwo.getHaveMoney());
        workerDetail.setHouseWorkerOrderId(hwo.getId());
        workerDetail.setWalletMoney(worker.getHaveMoney());
        workerDetail.setApplyMoney(hwo.getRepairPrice());
        workerDetailMapper.insert(workerDetail);

        //处理工钱
        if (worker.getWorkerPrice() == null) {//总钱
            worker.setWorkerPrice(new BigDecimal(0.0));
        }
        worker.setWorkerPrice(worker.getWorkerPrice().add(hwo.getRepairPrice()));

        if (worker.getHaveMoney() == null) {//工人已获取
            worker.setHaveMoney(new BigDecimal(0.0));
        }
        worker.setHaveMoney(worker.getHaveMoney().add(hwo.getRepairPrice()));

        if (worker.getSurplusMoney() == null) {//可取余额 赋初始值为0
            worker.setSurplusMoney(new BigDecimal(0.0));
        }
        BigDecimal mid = worker.getHaveMoney().subtract(worker.getRetentionMoney());//可取等于 获得减押金
        if (mid.compareTo(BigDecimal.ZERO) == 1) {//大于0
            worker.setSurplusMoney(mid);
        } else {
            worker.setSurplusMoney(BigDecimal.ZERO);
        }
        memberMapper.updateByPrimaryKeySelective(worker);
    }


    /**
     * 业主审核大管家完工申请
     */
    public ServerResponse checkSupervisor(String houseFlowApplyId) {
        try {
            HouseFlowApply hfa = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            hfa.setMemberCheck(1);//通过
            houseFlowApplyMapper.updateByPrimaryKeySelective(hfa);

            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(hfa.getHouseFlowId());
            hf.setWorkSteta(2);
            houseFlowMapper.updateByPrimaryKeySelective(hf);

            //查大管家被业主的评价
            //查工匠被业主的评价
            Evaluate evaluate = evaluateMapper.getForCountMoney(hfa.getHouseFlowId(), hfa.getApplyType(), hfa.getWorkerId());
            int star;
            if (evaluate == null) {
                star = 0;
            } else {
                star = evaluate.getStar();//几星？
            }
            //管家钱
            BigDecimal applyMoney;
            if (star == 0 || star == 5) {
                applyMoney = hfa.getApplyMoney();
            } else if (star == 3 || star == 4) {
                applyMoney = hfa.getApplyMoney().multiply(new BigDecimal(0.9));
            } else {
                applyMoney = hfa.getApplyMoney().multiply(new BigDecimal(0.8));
            }

            //计算大管家整体完工金额
            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hfa.getHouseId(), hfa.getWorkerTypeId());
            //处理worker表中大管家
            Member worker = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
            //记录流水
            WorkerDetail workerDetail = new WorkerDetail();
            workerDetail.setName("项目完工收入");
            workerDetail.setWorkerId(worker.getId());
            workerDetail.setWorkerName(worker.getName());
            workerDetail.setHouseId(hwo.getHouseId());
            workerDetail.setMoney(applyMoney);
            workerDetail.setState(0);//进钱
            workerDetail.setHaveMoney(hwo.getHaveMoney());
            workerDetail.setHouseWorkerOrderId(hwo.getId());
            workerDetail.setApplyMoney(hfa.getApplyMoney());
            workerDetail.setWalletMoney(worker.getHaveMoney());
            workerDetailMapper.insert(workerDetail);

            //记录项目流水
          /*  HouseAccounts ha = new HouseAccounts();
            ha.setReason("支出大管家整体完工工钱");
            ha.setMoney(house.getMoney().subtract(workerDetail.getMoney()));//项目总钱
            ha.setState(1);//出
            ha.setPaymoney(applymoney);//本次数额
            ha.setHouseid(hwo.getHouseid());
            ha.setHousename(house.getResidential()+house.getBuilding()+"栋"+house.getUnit()+"单元"+house.getHousenumber()+"号");
            ha.setMemberid(hwo.getMemberid());
            ha.setName(w.getName());
            houseAccountsDao.save(ha);
            house.setMoney(ha.getMoney());*/
            //记录到房子
            House house = houseMapper.selectByPrimaryKey(hfa.getHouseId());
            house.setHaveComplete(1);//房子已完成
            house.setVisitState(3);//新状态 已完工
            houseMapper.updateByPrimaryKeySelective(house);

            //处理工钱
            if (worker.getHaveMoney() == null) {//工人已获取
                worker.setHaveMoney(new BigDecimal(0.0));
            }
            worker.setHaveMoney(worker.getHaveMoney().add(applyMoney));

            if (worker.getSurplusMoney() == null) {//可取余额 赋初始值为0
                worker.setSurplusMoney(new BigDecimal(0.0));
            }
            BigDecimal mid = worker.getHaveMoney().subtract(worker.getRetentionMoney());//可取等于 获得减押金
            if (mid.compareTo(BigDecimal.ZERO) == 1) {//大于0
                worker.setSurplusMoney(mid);
            } else {
                worker.setSurplusMoney(new BigDecimal(0.0));
            }

            hwo.setHaveMoney(hwo.getWorkPrice());//已经得到的钱
            houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);

            //成交量加1
            if (worker.getVolume() == null) {
                worker.setVolume(new BigDecimal(0.0));
            }
            worker.setVolume(worker.getVolume().add(new BigDecimal(1)));
            memberMapper.updateByPrimaryKeySelective(worker);

            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 验收详情
     */
    public ServerResponse checkDetail(String houseFlowApplyId) {
        try {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            HouseFlowApplyDTO houseFlowApplyDTO = new HouseFlowApplyDTO();
            Member worker = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
            String local = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            if (houseFlowApply.getWorkerType() == 3) {//是管家发起的
                houseFlowApplyDTO.setHouseFlowApplyId(houseFlowApplyId);
                houseFlowApplyDTO.setApplyType(3);//管家提交的整体完工申请
                houseFlowApplyDTO.setHeadA(local + worker.getHead());
                houseFlowApplyDTO.setNameA(worker.getName());
                houseFlowApplyDTO.setMobileA(worker.getMobile());
                houseFlowApplyDTO.setWorkerTypeName(workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId()).getName());
                Example example = new Example(HouseFlowApplyImage.class);
                example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, houseFlowApplyId);
                List<HouseFlowApplyImage> houseFlowApplyImageList = houseFlowApplyImageMapper.selectByExample(example);
                List<String> imageList = new ArrayList<String>();
                for (HouseFlowApplyImage houseFlowApplyImage : houseFlowApplyImageList) {
                    imageList.add(local + houseFlowApplyImage.getImageUrl());
                }
                houseFlowApplyDTO.setImageList(imageList);
                houseFlowApplyDTO.setDate(DateUtil.dateToString(houseFlowApply.getModifyDate(), "yyyy-MM-dd HH:mm"));
                return ServerResponse.createBySuccess("查询管家整体申请成功", houseFlowApplyDTO);
            }

            //查询管家houseFLow
            HouseFlow houseFlow = houseFlowMapper.getHouseFlowByHidAndWty(houseFlowApply.getHouseId(), 3);
            Member steward = memberMapper.selectByPrimaryKey(houseFlow.getWorkerId());//管家

            houseFlowApplyDTO.setWorkerId(worker.getId());
            houseFlowApplyDTO.setManagerId(steward.getId());
            houseFlowApplyDTO.setHouseFlowApplyId(houseFlowApplyId);
            houseFlowApplyDTO.setApplyType(houseFlowApply.getApplyType());
            houseFlowApplyDTO.setHeadA(local + worker.getHead());
            houseFlowApplyDTO.setNameA(worker.getName());
            houseFlowApplyDTO.setMobileA(worker.getMobile());
            houseFlowApplyDTO.setWorkerTypeName(workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId()).getName());
            houseFlowApplyDTO.setHeadB(local + steward.getHead());
            houseFlowApplyDTO.setNameB(steward.getName());
            houseFlowApplyDTO.setMobileB(steward.getMobile());
            Example example = new Example(HouseFlowApplyImage.class);
            example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, houseFlowApplyId);
            List<HouseFlowApplyImage> houseFlowApplyImageList = houseFlowApplyImageMapper.selectByExample(example);
            List<String> imageList = new ArrayList<String>();
            for (HouseFlowApplyImage houseFlowApplyImage : houseFlowApplyImageList) {
                imageList.add(local + houseFlowApplyImage.getImageUrl());
            }
            houseFlowApplyDTO.setImageList(imageList);
            houseFlowApplyDTO.setDate(DateUtil.dateToString(houseFlowApply.getModifyDate(), "yyyy-MM-dd HH:mm"));
            return ServerResponse.createBySuccess("查询成功", houseFlowApplyDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 管家端验收详情
     */
    public ServerResponse stewardCheckDetail(String houseFlowApplyId) {
        try {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            HouseFlowApplyDTO houseFlowApplyDTO = new HouseFlowApplyDTO();
            Member worker = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());

            houseFlowApplyDTO.setHouseFlowApplyId(houseFlowApplyId);
            houseFlowApplyDTO.setApplyType(houseFlowApply.getApplyType());
            houseFlowApplyDTO.setWorkerTypeName(workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId()).getName());
            Example example = new Example(HouseFlowApplyImage.class);
            example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, houseFlowApplyId);
            List<HouseFlowApplyImage> houseFlowApplyImageList = houseFlowApplyImageMapper.selectByExample(example);
            List<String> imageList = new ArrayList<String>();
            for (HouseFlowApplyImage houseFlowApplyImage : houseFlowApplyImageList) {
                imageList.add(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + houseFlowApplyImage.getImageUrl());
            }
            houseFlowApplyDTO.setImageList(imageList);
            houseFlowApplyDTO.setDate(DateUtil.dateToString(houseFlowApply.getModifyDate(), "yyyy-MM-dd HH:mm"));

            return ServerResponse.createBySuccess("查询成功", houseFlowApplyDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}













