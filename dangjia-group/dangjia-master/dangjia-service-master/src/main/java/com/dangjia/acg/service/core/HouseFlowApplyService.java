package com.dangjia.acg.service.core;

import com.dangjia.acg.auth.config.RedisSessionDAO;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HouseFlowApplyDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.mapper.sale.ResidentialBuildingMapper;
import com.dangjia.acg.mapper.sale.ResidentialRangeMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.worker.IEvaluateMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.Evaluate;
import com.dangjia.acg.modle.worker.WorkIntegral;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;
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
    private ITechnologyRecordMapper technologyRecordMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private ClueMapper clueMapper;
    @Autowired
    private ResidentialRangeMapper residentialRangeMapper;
    @Autowired
    private ResidentialBuildingMapper residentialBuildingMapper;

    @Autowired
    private UserMapper userMapper;

    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
    /**
     * 工匠端工地记录
     */
    public ServerResponse houseRecord(String userToken, String houseId, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Map<Integer, String> applyTypeMap = new HashMap<>();
            applyTypeMap.put(DjConstants.ApplyType.MEIRI_WANGGONG, "每日完工");
            applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG, "阶段完工");
            applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG, "整体完工");
            applyTypeMap.put(DjConstants.ApplyType.TINGGONG, "停工");
            applyTypeMap.put(DjConstants.ApplyType.MEIRI_KAIGONG, "每日开工");
            applyTypeMap.put(DjConstants.ApplyType.YOUXIAO_XUNCHA, "巡查");
            applyTypeMap.put(DjConstants.ApplyType.WUREN_XUNCHA, "巡查");
            applyTypeMap.put(DjConstants.ApplyType.ZUIJIA_XUNCHA, "巡查");
            List<HouseFlowApplyDTO> houseFlowApplyDTOList = new ArrayList<>();
            HouseWorker gjhouseWorker = houseWorkerMapper.getHwByHidAndWtype(houseId, 3);
            Member worker2 = memberMapper.selectByPrimaryKey(gjhouseWorker.getWorkerId());//根据工匠id查询工匠信息详情
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
    public ServerResponse checkWorker(String houseFlowApplyId, boolean isAuto) {
        try {
            //HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HouseFlowApply hfa = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            if (hfa.getMemberCheck() == 1 || hfa.getMemberCheck() == 3) {
                return ServerResponse.createByErrorMessage("重复审核");
            }
            if (isAuto) {
                hfa.setMemberCheck(3);
            } else {
                hfa.setMemberCheck(1);
            }
            hfa.setSupervisorCheck(1);
            hfa.setPayState(1);
            hfa.setModifyDate(new Date());
            houseFlowApplyMapper.updateByPrimaryKeySelective(hfa);
            //工匠订单
            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hfa.getHouseId(), hfa.getWorkerTypeId());
            /*
            节点审核通过
             */
            if(null == hwo){
                return ServerResponse.createByErrorMessage("该订单异常");
            }

            technologyRecordMapper.passTecRecord(hwo.getHouseId(), hwo.getWorkerTypeId());
            if (hfa.getApplyType() == 2) {//整体完工
                //修改进程
                HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(hwo.getHouseId(), hwo.getWorkerTypeId());
                if (houseFlow.getWorkSteta() == 2) {
                    return ServerResponse.createBySuccessMessage("操作成功");
                }
                houseFlow.setWorkSteta(2);
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                //处理工人拿钱
                workerMoney(hwo, hfa);
                //大管家拿钱
                stewardMoney(hfa);
                //设置保险时间
                WorkerTypeSafeOrder wtso = workerTypeSafeOrderMapper.getByWorkerTypeId(hwo.getWorkerTypeId(), hwo.getHouseId());

//                //超过免费要货次数,收取工匠运费
//                extraOrderSplitFare(hwo);
//                //收取工匠退货运费
//                extraMendOrderFare(hwo);
                //临时代码-补充未生成的质保卡
                if (wtso == null) {//默认生成一条
                    //该工钟所有保险
                    Example example = new Example(WorkerTypeSafe.class);
                    example.createCriteria().andEqualTo(WorkerTypeSafe.WORKER_TYPE_ID, houseFlow.getWorkerTypeId());
                    List<WorkerTypeSafe> wtsList = workerTypeSafeMapper.selectByExample(example);
                    if (wtsList.size() > 0) {
                        House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
                        wtso = new WorkerTypeSafeOrder();
                        wtso.setWorkerTypeSafeId(wtsList.get(0).getId()); // 向保险订单中存入保险服务类型的id
                        wtso.setHouseId(houseFlow.getHouseId()); // 存入房子id
                        wtso.setWorkerTypeId(houseFlow.getWorkerTypeId()); // 工种id
                        wtso.setWorkerType(houseFlow.getWorkerType());
                        wtso.setPrice(wtsList.get(0).getPrice().multiply(house.getSquare()));
                        wtso.setState(1);
                        workerTypeSafeOrderMapper.insert(wtso);
                    }
                }
                if (wtso != null) {
                    WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(wtso.getWorkerTypeSafeId());//获得类型算出时间
                    int month = (wts.getMonth()); //获取保险时长(月)
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, month);
                    wtso.setState(2);  //已生效
                    wtso.setForceTime(new Date());//设置生效时间
                    wtso.setExpirationDate(cal.getTime()); //设置到期时间
                    workerTypeSafeOrderMapper.updateByPrimaryKeySelective(wtso);
                }
            } else if (hfa.getApplyType() == 1) {//阶段完工
                //修改进程
                HouseFlow hf = houseFlowMapper.getByWorkerTypeId(hwo.getHouseId(), hwo.getWorkerTypeId());
                if (hf.getWorkSteta() == 1) {
                    return ServerResponse.createBySuccessMessage("操作成功");
                }
                hf.setWorkSteta(1);
                houseFlowMapper.updateByPrimaryKeySelective(hf);
                //处理押金
                deposit(hwo, hfa);
                //处理工人拿钱
                workerMoney(hwo, hfa);
                //大管家拿钱
                stewardMoney(hfa);
                Map<String, String> temp_para = new HashMap();
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hfa.getWorkerTypeId());
                House house = houseMapper.selectByPrimaryKey(hfa.getHouseId());
                temp_para.put("house_name", house.getHouseName());
                temp_para.put("worker_name", workerType.getName() + "阶段完工");
                //给售中陶娇发短信
                JsmsUtil.sendSMS("15675101794", "164425", temp_para);
            } else if (hfa.getApplyType() == 0) { //每日完工,处理钱
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
                BigDecimal surplusMoney = worker.getSurplusMoney().add(hfa.getApplyMoney());
                if (hfa.getApplyMoney().doubleValue() > 0) {
                    WorkerDetail workerDetail = new WorkerDetail();
                    workerDetail.setName("每日完工");
                    workerDetail.setWorkerId(hwo.getWorkerId());
                    workerDetail.setWorkerName(worker.getName());
                    workerDetail.setHouseId(hwo.getHouseId());
                    workerDetail.setMoney(hfa.getApplyMoney());
                    workerDetail.setHaveMoney(hwo.getHaveMoney());
                    workerDetail.setHouseWorkerOrderId(hwo.getId());
                    workerDetail.setApplyMoney(hfa.getApplyMoney());
                    workerDetail.setWalletMoney(surplusMoney);
                    workerDetail.setState(0);//进钱
                    workerDetailMapper.insert(workerDetail);
                }

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
                if (hfa.getApplyMoney().doubleValue() > 0) {//大于0
                    worker.setSurplusMoney(surplusMoney);
                }
                memberMapper.updateByPrimaryKeySelective(worker);

            }
            hfa.setModifyDate(new Date());
            houseFlowApplyMapper.updateByPrimaryKeySelective(hfa);

            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("审核失败");
        }
    }

    /*
     * 计算是否超过免费要货次数,收取工匠运费
     */
    public void extraOrderSplitFare(HouseWorkerOrder hwo){
        Example example = new Example(OrderSplit.class);
        example.createCriteria()
                .andEqualTo(OrderSplit.HOUSE_ID, hwo.getHouseId())
                .andEqualTo(OrderSplit.APPLY_STATUS, 2)
                .andEqualTo(OrderSplit.WORKER_TYPE_ID, hwo.getWorkerTypeId());
        List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey( hwo.getWorkerTypeId());
        if (orderSplitList.size() > workerType.getSafeState()) {//超过免费次数收工匠运费
            //每次固定收取100元
            BigDecimal yunFei = new BigDecimal(100);
            Member worker = memberMapper.selectByPrimaryKey(hwo.getWorkerId());//要货人
            for (int i = workerType.getSafeState(); i < orderSplitList.size(); i++) {
                BigDecimal haveMoney = worker.getHaveMoney().subtract(yunFei);
                BigDecimal surplusMoneys = worker.getSurplusMoney().subtract(yunFei);
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName(workerType.getName()+"第"+(i+1)+"次要货运费");
                workerDetail.setWorkerId(worker.getId());
                workerDetail.setWorkerName(worker.getName());
                workerDetail.setHouseId(hwo.getHouseId());
                workerDetail.setMoney(yunFei);
                workerDetail.setState(7);//收取运费
                workerDetail.setWalletMoney(haveMoney);
                workerDetail.setApplyMoney(yunFei);
                workerDetailMapper.insert(workerDetail);
                worker.setHaveMoney(haveMoney);
                worker.setSurplusMoney(surplusMoneys);
                memberMapper.updateByPrimaryKeySelective(worker);
            }
        }
    }

    /*
     * 收取工匠退货运费
     */
    public void extraMendOrderFare(HouseWorkerOrder hwo){
        Example example = new Example(MendOrder.class);
        example.createCriteria()
                .andEqualTo(MendOrder.HOUSE_ID, hwo.getHouseId())
                .andEqualTo(MendOrder.TYPE, 2)
                .andEqualTo(MendOrder.STATE, 4)
                .andEqualTo(MendOrder.WORKER_TYPE_ID, hwo.getWorkerTypeId());
        List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey( hwo.getWorkerTypeId());
        if (workerType.getType()>3) {
            //每次固定收取100元
            BigDecimal yunFei = new BigDecimal(100);
            Member worker = memberMapper.selectByPrimaryKey(hwo.getWorkerId());//要货人
            for (int i = 0; i < mendOrderList.size(); i++) {
                BigDecimal haveMoney = worker.getHaveMoney().subtract(yunFei);
                BigDecimal surplusMoneys = worker.getSurplusMoney().subtract(yunFei);
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName(workerType.getName()+"第"+(i+1)+"次退货运费");
                workerDetail.setWorkerId(worker.getId());
                workerDetail.setWorkerName(worker.getName());
                workerDetail.setHouseId(hwo.getHouseId());
                workerDetail.setMoney(yunFei);
                workerDetail.setState(7);//收取运费
                workerDetail.setWalletMoney(haveMoney);
                workerDetail.setApplyMoney(yunFei);
                workerDetailMapper.insert(workerDetail);
                worker.setHaveMoney(haveMoney);
                worker.setSurplusMoney(surplusMoneys);
                memberMapper.updateByPrimaryKeySelective(worker);
            }
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

        double star;
        if (evaluate == null) {
            star = 0;
        } else {
            star = evaluate.getStar();//几星？
        }
        /*****兼容老工地该字段没有初始值*****/
        if (hfa.getSupervisorMoney() == null) {
            hfa.setSupervisorMoney(new BigDecimal(0));
        }
        if (hwo.getDeductPrice() == null) {
            hwo.setDeductPrice(new BigDecimal(0));
        }
        //评分扣钱
        BigDecimal deductPrice = new BigDecimal(0);
        BigDecimal supervisorMoney;
        if (star == 0 || star == 5) {
            supervisorMoney = hfa.getSupervisorMoney();//大管家的验收收入
        } else if (star == 3 || star == 4) {
            supervisorMoney = hfa.getSupervisorMoney().multiply(new BigDecimal(0.8));
            deductPrice = hfa.getSupervisorMoney().subtract(supervisorMoney);
        } else {
            supervisorMoney = new BigDecimal(0);//为0元
            deductPrice = hfa.getSupervisorMoney();
        }
        hwo.setDeductPrice(hwo.getDeductPrice().add(deductPrice));
        hwo.setHaveMoney(hwo.getHaveMoney().add(supervisorMoney));
        //大管家验收钱
        hwo.setEveryMoney(hwo.getEveryMoney().add(supervisorMoney));
        houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);

        //钱包处理大管家的工钱
        Member worker = memberMapper.selectByPrimaryKey(hwo.getWorkerId());
        //评分扣钱流水
        deductFlow(worker, hwo, star, hfa.getApplyType(), deductPrice);

        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hfa.getWorkerTypeId());
        //管家押金处理
        HouseFlowApply houseFlowApply = new HouseFlowApply();
        houseFlowApply.setWorkerType(3);
        houseFlowApply.setWorkerId(worker.getId());
        houseFlowApply.setWorkerTypeId(worker.getWorkerTypeId());
        houseFlowApply.setHouseId(hwo.getHouseId());
        deposit(hwo, houseFlowApply);

        BigDecimal surplusMoney = worker.getSurplusMoney().add(supervisorMoney);
        //记录流水
        if (supervisorMoney.doubleValue() > 0) {
            WorkerDetail workerDetail = new WorkerDetail();
            if (hfa.getApplyType() == 2) {//整体完工申请
                workerDetail.setName("整体(" + workerType.getName() + ")验收收入");
            } else if (hfa.getApplyType() == 1) {
                workerDetail.setName("阶段(" + workerType.getName() + ")验收收入");
            }
            workerDetail.setWorkerId(worker.getId());
            workerDetail.setWorkerName(worker.getName());
            workerDetail.setHouseId(hwo.getHouseId());
            workerDetail.setMoney(supervisorMoney);//实际收入
            workerDetail.setState(0);//进钱
            workerDetail.setHaveMoney(hwo.getHaveMoney());
            workerDetail.setDefinedWorkerId(hfa.getId());
            workerDetail.setHouseWorkerOrderId(hwo.getId());
            workerDetail.setApplyMoney(hfa.getSupervisorMoney());//管家应拿的验收收入
            workerDetail.setWalletMoney(surplusMoney);
            workerDetailMapper.insert(workerDetail);
        }
        //处理工钱
        if (worker.getHaveMoney() == null) {//工人已获取
            worker.setHaveMoney(new BigDecimal(0.0));
        }
        worker.setHaveMoney(worker.getHaveMoney().add(supervisorMoney));

        if (worker.getSurplusMoney() == null) {//可取余额 赋初始值为0
            worker.setSurplusMoney(new BigDecimal(0.0));
        }
        if (supervisorMoney.doubleValue() > 0) {//大于0
            worker.setSurplusMoney(surplusMoney);
        }
        memberMapper.updateByPrimaryKeySelective(worker);
    }

    /**
     * 记录评分扣钱流水
     */
    public void deductFlow(Member worker , HouseWorkerOrder hwo,Double star,Integer applyType, BigDecimal deductPrice) {
        if(deductPrice.doubleValue()>0) {
            WorkerDetail workerDetail = new WorkerDetail();
            if (applyType == 2) {//整体完工申请
                workerDetail.setName("评分扣钱，评分为:" + star + "");
            } else if (applyType == 1) {
                workerDetail.setName("评分扣钱，评分为:" + star + "");
            }
            workerDetail.setWorkerId(worker.getId());
            workerDetail.setWorkerName(worker.getName());
            workerDetail.setHouseId(hwo.getHouseId());
            workerDetail.setMoney(deductPrice);//实际支出
            workerDetail.setState(3);//扣钱
            workerDetail.setHaveMoney(hwo.getDeductPrice());
            workerDetail.setDefinedWorkerId(hwo.getId());
            workerDetail.setHouseWorkerOrderId(hwo.getId());
            workerDetail.setApplyMoney(hwo.getDeductPrice());
            workerDetail.setWalletMoney(deductPrice);
            workerDetailMapper.insert(workerDetail);
        }
    }
    /**
     * 处理工人押金
     */
    public void deposit(HouseWorkerOrder hwo, HouseFlowApply hfa) {
        if (hfa.getWorkerType() >= 3 && hfa.getWorkerType() != 4) {//精算，设计，拆除除外
            Member worker = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
            /*
             * 工匠积分70分以下每单滞留金无上限,70以上含80以下2000元
             * 80含以上90以下1500元,90分含以上500元
             */
            if (worker.getEvaluationScore().doubleValue() >= 70 && worker.getEvaluationScore().doubleValue() < 80) {
                worker.setDeposit(new BigDecimal(2000));//设置滞留金2000元
            } else if (worker.getEvaluationScore().doubleValue() >= 80 && worker.getEvaluationScore().doubleValue() < 90) {
                worker.setDeposit(new BigDecimal(1500));//设置滞留金上限1500元
            } else if (worker.getEvaluationScore().doubleValue() >= 90) {
                worker.setDeposit(new BigDecimal(500));//设置滞留金上限500元
            } else {
                worker.setDeposit(new BigDecimal(99999));//重新设置无上限
            }
            if (worker.getRetentionMoney() == null) {
                worker.setRetentionMoney(new BigDecimal(0.0));
            }
            //实际滞留金减上限
            BigDecimal bd = worker.getRetentionMoney().subtract(worker.getDeposit());
            if (bd.doubleValue() > 0) {
                //记录流水
                BigDecimal surplusMoney = worker.getSurplusMoney().add(bd);
                BigDecimal retentionMoney = worker.getRetentionMoney().subtract(bd);
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("涨积分退滞留金");
                workerDetail.setWorkerId(worker.getId());
                workerDetail.setWorkerName(worker.getName());
                workerDetail.setHouseId(hfa.getHouseId());
                workerDetail.setMoney(bd);
                workerDetail.setHaveMoney(surplusMoney);
                workerDetail.setDefinedWorkerId(hfa.getId());
                workerDetail.setWalletMoney(surplusMoney);
                workerDetail.setState(0);//进钱
                workerDetailMapper.insert(workerDetail);
                worker.setDeposit(worker.getDeposit());//实际1500元
                worker.setSurplusMoney(surplusMoney);
                worker.setRetentionMoney(retentionMoney);
            }
            //BigDecimal deposit = workDepositService.getWorkDepositByList().getDeposit();//获取押金比例 5%
            BigDecimal deposit = new BigDecimal(0.05);

            //申请的钱为空时将不考虑滞留金转入
            if (hfa != null && hfa.getApplyMoney() != null && worker.getRetentionMoney().doubleValue() < worker.getDeposit().doubleValue()) {//押金没收够并且没有算过押金
                //算订单的5%
                BigDecimal mid = hwo.getWorkPrice().multiply(deposit);
                if (!(worker.getRetentionMoney().add(mid).compareTo(worker.getDeposit()) == -1 ||
                        worker.getRetentionMoney().add(mid).compareTo(worker.getDeposit()) == 0)) {
                    mid = worker.getDeposit().subtract(worker.getRetentionMoney());//只收这么多了
                }

                BigDecimal retentionMoney = worker.getRetentionMoney().add(mid);
                BigDecimal haveMoney = worker.getHaveMoney().add(mid);
                worker.setHaveMoney(haveMoney);
                worker.setRetentionMoney(retentionMoney);
                //记录流水
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("收入转入滞留金");
                workerDetail.setWorkerId(worker.getId());
                workerDetail.setWorkerName(worker.getName());
                workerDetail.setDefinedWorkerId(hfa.getId());
                workerDetail.setHouseId(hwo.getHouseId());
                workerDetail.setMoney(mid);
                workerDetail.setApplyMoney(retentionMoney);
                workerDetail.setHaveMoney(haveMoney);
                workerDetail.setWalletMoney(retentionMoney);
                workerDetail.setState(2);//进钱
                workerDetailMapper.insert(workerDetail);
                //实际滞留金
                hwo.setRetentionMoney(mid);

                //处理阶段申请的钱，将减去滞留金的钱，存入账户余额
                BigDecimal applyMoney = hfa.getApplyMoney().subtract(mid);
                hfa.setApplyMoney(applyMoney);
            }
            memberMapper.updateByPrimaryKeySelective(worker);
        }
    }

    /**
     * 工人拿钱
     */
    private void workerMoney(HouseWorkerOrder hwo, HouseFlowApply hfa) {
        try {
            Member worker = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hfa.getWorkerTypeId());
            if (hfa.getApplyType() == 1 || hfa.getApplyType() == 2) {//整体/阶段完工申请
                if (hwo.getRepairPrice().compareTo(new BigDecimal(0)) > 0) {
                    /*有补的人工钱加入工人流水*/
                    BigDecimal surplusMoney = worker.getSurplusMoney().add(hwo.getRepairPrice());
                    BigDecimal workerPrice = worker.getWorkerPrice().add(hwo.getRepairPrice());
                    BigDecimal haveMoney = worker.getHaveMoney().add(hwo.getRepairPrice());
                    worker.setWorkerPrice(workerPrice);//总共获得钱+ 补人工
                    worker.setHaveMoney(haveMoney);//可取 + 滞留金 + 补人工
                    worker.setSurplusMoney(surplusMoney);//可取钱

                    if (hwo.getRepairPrice().doubleValue() > 0) {
                        WorkerDetail workerDetail = new WorkerDetail();
                        if (hfa.getApplyType() == 2) {//整体完工申请
                            workerDetail.setName(workerType.getName() + "整体完工补人工钱");
                        } else {
                            workerDetail.setName(workerType.getName() + "阶段完工补人工钱");
                        }
                        workerDetail.setWorkerId(hwo.getWorkerId());
                        workerDetail.setWorkerName(worker.getName());
                        workerDetail.setHouseId(hwo.getHouseId());
                        workerDetail.setMoney(hwo.getRepairPrice());
                        workerDetail.setState(0);//进钱
                        workerDetail.setDefinedWorkerId(hfa.getId());
                        workerDetail.setHaveMoney(hwo.getHaveMoney());
                        workerDetail.setHouseWorkerOrderId(hwo.getId());
                        workerDetail.setWalletMoney(surplusMoney);
                        workerDetail.setApplyMoney(hwo.getRepairPrice());
                        workerDetailMapper.insert(workerDetail);
                    }
                }
            }
            //处理工人得到的钱
            if (hwo.getHaveMoney() == null) {
                hwo.setHaveMoney(new BigDecimal(0.0));
            }
            //处理工人评分扣钱
            if (hwo.getDeductPrice() == null) {
                hwo.setDeductPrice(new BigDecimal(0.0));
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
            //评分扣钱
            BigDecimal deductPrice = new BigDecimal(0);

            if (star >= 4) {
                applymoney = hfa.getApplyMoney();
            } else if (star > 2) {
                applymoney = hfa.getApplyMoney().multiply(new BigDecimal(0.97));
                deductPrice = hfa.getApplyMoney().subtract(applymoney);
            } else {
                applymoney = hfa.getApplyMoney().multiply(new BigDecimal(0.95));
                deductPrice = hfa.getApplyMoney().subtract(applymoney);
            }
            //整体完工前均能发起，阶段完工发起的，阶段完工时拿钱（还可拿钱立即体现增加金额），
            // 阶段完工后整体完工前发起的，整体完工时拿钱（还可拿钱立即体现增加金额）
            //清空补人工钱，用于整体完工时记录新的补人工钱CraftsmanConstructionService
            hwo.setRepairPrice(new BigDecimal(0.0));
            hwo.setDeductPrice(hwo.getDeductPrice().add(deductPrice));
            hwo.setHaveMoney(hwo.getHaveMoney().add(applymoney));
            houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);


            //评分扣钱流水
            deductFlow(worker, hwo, star, hfa.getApplyType(), deductPrice);

            BigDecimal haveMoney = worker.getHaveMoney().add(applymoney);
            BigDecimal surplusMoney = worker.getSurplusMoney().add(applymoney);
            //记录流水
            if (applymoney.doubleValue() > 0) {
                WorkerDetail workerDetail = new WorkerDetail();
                if (hfa.getApplyType() == 2) {//整体完工申请
                    workerDetail.setName(workerType.getName() + "整体完工");
                } else {
                    workerDetail.setName(workerType.getName() + "阶段完工");
                }
                workerDetail.setWorkerId(hwo.getWorkerId());
                workerDetail.setWorkerName(worker.getName());
                workerDetail.setHouseId(hwo.getHouseId());
                workerDetail.setMoney(applymoney);
                workerDetail.setState(0);//进钱
                workerDetail.setHaveMoney(hwo.getHaveMoney());
                workerDetail.setDefinedWorkerId(hfa.getId());
                workerDetail.setHouseWorkerOrderId(hwo.getId());
                workerDetail.setApplyMoney(hfa.getApplyMoney());
                workerDetail.setWalletMoney(surplusMoney);
                workerDetailMapper.insert(workerDetail);
            }


            if (worker.getHaveMoney() == null) {//工人已获取
                worker.setHaveMoney(new BigDecimal(0.0));
            }
            if (worker.getSurplusMoney() == null) {//可取余额 赋初始值为0
                worker.setSurplusMoney(new BigDecimal(0.0));
            }

            worker.setHaveMoney(haveMoney);

            if (applymoney.doubleValue() > 0) {//大于0
                worker.setSurplusMoney(surplusMoney);
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
     * 业主审核大管家完工申请
     */
    public ServerResponse checkSupervisor(String houseFlowApplyId, boolean isAuto) {
        try {
            HouseFlowApply hfa = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            if (hfa.getMemberCheck() == 1 || hfa.getMemberCheck() == 3) {
                return ServerResponse.createBySuccessMessage("操作成功");
            }
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hfa.getWorkerTypeId());
            if (isAuto) {
                hfa.setMemberCheck(3);//自动通过
            } else {
                hfa.setMemberCheck(1);//通过
            }
            hfa.setModifyDate(new Date());
            houseFlowApplyMapper.updateByPrimaryKeySelective(hfa);

            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(hfa.getHouseFlowId());
            hf.setWorkSteta(2);
            houseFlowMapper.updateByPrimaryKeySelective(hf);

            //查大管家被业主的评价
            //查工匠被业主的评价
            Evaluate evaluate = evaluateMapper.getForCountMoney(hfa.getHouseFlowId(), hfa.getApplyType(), hfa.getWorkerId());
            double star;
            if (evaluate == null) {
                star = 0;
            } else {
                star = evaluate.getStar();//几星？
            }
            //评分扣钱
            BigDecimal deductPrice = new BigDecimal(0);
            //管家钱
            BigDecimal applyMoney;
            if (star == 5) {
                applyMoney = hfa.getApplyMoney();
            } else if (star == 3 || star == 4) {
                applyMoney = hfa.getApplyMoney().multiply(new BigDecimal(0.9));
                deductPrice = hfa.getApplyMoney().subtract(applyMoney);
            } else {
                applyMoney = hfa.getApplyMoney().multiply(new BigDecimal(0.8));
                deductPrice = hfa.getApplyMoney().subtract(applyMoney);
            }

            //计算大管家整体完工金额
            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(hfa.getHouseId(), hfa.getWorkerTypeId());  //处理工人评分扣钱
            if (hwo.getDeductPrice() == null) {
                hwo.setDeductPrice(new BigDecimal(0.0));
            }
            //处理worker表中大管家
            Member worker = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
            BigDecimal surplusMoney = worker.getSurplusMoney().add(applyMoney);
            //记录流水
            WorkerDetail workerDetail = new WorkerDetail();
            workerDetail.setName(workerType.getName() + "项目完工收入");
            workerDetail.setWorkerId(worker.getId());
            workerDetail.setWorkerName(worker.getName());
            workerDetail.setHouseId(hwo.getHouseId());
            workerDetail.setMoney(applyMoney);
            workerDetail.setState(0);//进钱
            workerDetail.setHaveMoney(hwo.getHaveMoney());
            workerDetail.setHouseWorkerOrderId(hwo.getId());
            workerDetail.setApplyMoney(hfa.getApplyMoney());
            workerDetail.setWalletMoney(surplusMoney);
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
            house.setCompletedDate(new Date());
            houseMapper.updateByPrimaryKeySelective(house);
            List<Customer> ms = iCustomerMapper.getCustomerMemberIdList(house.getMemberId());
            if (ms != null) {
                for (Customer m : ms) {
                    configMessageService.addConfigMessage(AppType.SALE, m.getMemberId(), "竣工提醒",
                            "您的客户【" + house.getHouseName() + "】已竣工，请及时查看提成。", 6);
                }
            }



            Example ex = new Example(Clue.class);
            ex.createCriteria().andEqualTo(Clue.MEMBER_ID, house.getMemberId())
                    .andEqualTo(Clue.DATA_STATUS, 0);
            List<Clue> clueList = clueMapper.selectByExample(ex);
            ResidentialBuilding residentialBuilding = residentialBuildingMapper.selectSingleResidentialBuilding(null, house.getBuilding(), house.getVillageId());
            if (null != residentialBuilding) {   //判断楼栋是否存在
                ResidentialRange residentialRange = residentialRangeMapper.selectSingleResidentialRange(residentialBuilding.getId());
                if (null != residentialRange) {    //楼栋是否分配销售
                    if(!residentialRange.getUserId().equals(clueList.get(0).getCusService())){
                        //判断销售所选楼栋是否在自己楼栋范围内 不在则跟选择的楼栋范围销售分提成  推送消息
                        logger.info("有一个归于您的客户【房子地址】已竣工==================="+residentialRange.getUserId());
                        //销售所选楼栋是否在自己楼栋范围内推送消息
                        MainUser us = userMapper.selectByPrimaryKey(residentialRange.getUserId());
                        if(null != us && !CommonUtil.isEmpty(us.getMemberId())){
                            configMessageService.addConfigMessage(AppType.SALE, us.getMemberId(), "竣工提醒",
                                    "您有一个归于您的客户【" + house.getHouseName() + "】已竣工，请及时查看提成。", 6);
                        }
                    }
                }
            }

            if(clueList.size() == 1){
                if(!CommonUtil.isEmpty(clueList.get(0).getCrossDomainUserId())){
                    logger.info("您的跨域客户【客户名称】已竣工==================="+clueList.get(0).getCrossDomainUserId());
                    //跨域下单推送消息
                    MainUser us = userMapper.selectByPrimaryKey(clueList.get(0).getCrossDomainUserId());
                    if(null != us && !CommonUtil.isEmpty(us.getMemberId())){
                        Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                        configMessageService.addConfigMessage(AppType.SALE, us.getMemberId(), "竣工提醒",
                                "您的跨域客户【" + member.getNickName() + "】已竣工，请及时查看提成。", 6);
                    }

                }
            }


            //处理工钱
            if (worker.getHaveMoney() == null) {//工人已获取
                worker.setHaveMoney(new BigDecimal(0.0));
            }
            worker.setHaveMoney(worker.getHaveMoney().add(applyMoney));

            if (worker.getSurplusMoney() == null) {//可取余额 赋初始值为0
                worker.setSurplusMoney(new BigDecimal(0.0));
            }
            if (applyMoney.doubleValue() > 0) {//大于0
                worker.setSurplusMoney(surplusMoney);
            }
            hwo.setDeductPrice(hwo.getDeductPrice().add(deductPrice));
            hwo.setHaveMoney(hwo.getHaveMoney().add(applyMoney));//已经得到的钱


            //巡查次数结算
            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, hfa.getHouseId())
                    .andCondition(" patrol >0  and work_type =4 ");
            List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(example);
            Integer patrol = 0;
            if (houseFlows.size() > 0) {
                for (HouseFlow houseFlow : houseFlows) {
                    patrol = patrol + houseFlow.getPatrol();
                }
            }
            if (hwo.getCheckMoney() == null) {//大管家每次巡查得到的钱 累计 赋初始值为0
                hwo.setCheckMoney(new BigDecimal(0.0));
            }
            BigDecimal patrolMoney = hwo.getWorkPrice().multiply(new BigDecimal(0.2));
            patrolMoney = patrolMoney.subtract(hwo.getCheckMoney());
            if (patrolMoney.doubleValue() > 0) {
                if (patrol == 0) {
                    hwo.setHaveMoney(hwo.getHaveMoney().add(patrolMoney));
                    worker.setSurplusMoney(worker.getSurplusMoney().add(patrolMoney));
                    worker.setHaveMoney(worker.getHaveMoney().add(patrolMoney));
                }
                if (patrol > 0) {
                    hwo.setDeductPrice(hwo.getDeductPrice().add(patrolMoney));
                }
            }
            houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
            //评分扣钱流水
            deductFlow(worker, hwo, star, hfa.getApplyType(), deductPrice);
            //成交量加1
            if (worker.getVolume() == null) {
                worker.setVolume(new BigDecimal(0.0));
            }
            worker.setVolume(worker.getVolume().add(new BigDecimal(1)));
            memberMapper.updateByPrimaryKeySelective(worker);

            //超过免费要货次数,收取管家运费
//            extraOrderSplitFare(hwo);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 验收详情
     * 1.30 1.31 共用
     */
    public ServerResponse checkDetail(String houseFlowApplyId) {
        try {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            HouseFlowApplyDTO houseFlowApplyDTO = new HouseFlowApplyDTO();
            houseFlowApplyDTO.setHouseId(houseFlowApply.getHouseId());
            Member worker = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            if (houseFlowApply.getWorkerType() == 3) {//是管家发起的
                houseFlowApplyDTO.setHouseId(houseFlowApply.getHouseId());
                houseFlowApplyDTO.setWorkerId(worker.getId());
                houseFlowApplyDTO.setHouseFlowApplyId(houseFlowApplyId);
                houseFlowApplyDTO.setApplyType(3);//管家提交的整体完工申请
                houseFlowApplyDTO.setWorkerTypeId(houseFlowApply.getWorkerTypeId());
                houseFlowApplyDTO.setHeadA(address + worker.getHead());
                houseFlowApplyDTO.setNameA(worker.getName());
                houseFlowApplyDTO.setMobileA(worker.getMobile());
                houseFlowApplyDTO.setWorkerTypeName(workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId()).getName());
                Example example = new Example(HouseFlowApplyImage.class);
                example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, houseFlowApplyId);
                List<HouseFlowApplyImage> houseFlowApplyImageList = houseFlowApplyImageMapper.selectByExample(example);
                List<String> imageList = new ArrayList<>();
                for (HouseFlowApplyImage houseFlowApplyImage : houseFlowApplyImageList) {
                    imageList.add(address + houseFlowApplyImage.getImageUrl());
                }
                if (houseFlowApply.getEndDate() != null) {
                    houseFlowApplyDTO.setEndDate(houseFlowApply.getEndDate().getTime() - new Date().getTime()); //业主自动审核时间
                }
                setImageList(houseFlowApply.getId(), address, imageList);
                houseFlowApplyDTO.setImageList(imageList);
                houseFlowApplyDTO.setDate(DateUtil.dateToString(houseFlowApply.getModifyDate(), "yyyy-MM-dd HH:mm"));
                return ServerResponse.createBySuccess("查询管家整体申请成功", houseFlowApplyDTO);
            }
            //查询管家houseFLow
            HouseFlow houseFlow = houseFlowMapper.getHouseFlowByHidAndWty(houseFlowApply.getHouseId(), 3);
            Member steward = memberMapper.selectByPrimaryKey(houseFlow.getWorkerId());//管家
            houseFlowApplyDTO.setHouseId(houseFlowApply.getHouseId());
            houseFlowApplyDTO.setWorkerId(worker.getId());
            houseFlowApplyDTO.setManagerId(steward.getId());
            houseFlowApplyDTO.setHouseFlowApplyId(houseFlowApplyId);
            houseFlowApplyDTO.setApplyType(houseFlowApply.getApplyType());
            houseFlowApplyDTO.setWorkerTypeId(houseFlowApply.getWorkerTypeId());
            houseFlowApplyDTO.setHeadA(address + worker.getHead());
            houseFlowApplyDTO.setNameA(worker.getName());
            houseFlowApplyDTO.setMobileA(worker.getMobile());
            houseFlowApplyDTO.setWorkerTypeName(workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId()).getName());
            houseFlowApplyDTO.setHeadB(address + steward.getHead());
            houseFlowApplyDTO.setNameB(steward.getName());
            houseFlowApplyDTO.setMobileB(steward.getMobile());
            if (houseFlowApply.getEndDate() != null) {
                houseFlowApplyDTO.setEndDate(houseFlowApply.getEndDate().getTime() - new Date().getTime()); //业主自动审核时间
            }
            Example example = new Example(HouseFlowApplyImage.class);
            example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, houseFlowApplyId);
            List<HouseFlowApplyImage> houseFlowApplyImageList = houseFlowApplyImageMapper.selectByExample(example);
            List<String> imageList = new ArrayList<>();
            for (HouseFlowApplyImage houseFlowApplyImage : houseFlowApplyImageList) {
                imageList.add(address + houseFlowApplyImage.getImageUrl());
            }
            setImageList(houseFlowApply.getId(), address, imageList);
            houseFlowApplyDTO.setImageList(imageList);
            houseFlowApplyDTO.setDate(DateUtil.dateToString(houseFlowApply.getModifyDate(), "yyyy-MM-dd HH:mm"));
            return ServerResponse.createBySuccess("查询成功", houseFlowApplyDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    private void setImageList(String hfId, String address, List<String> imageList) {
        Example example = new Example(TechnologyRecord.class);
        example.createCriteria().andEqualTo(TechnologyRecord.HOUSE_FLOW_APPLY_ID, hfId);
        example.orderBy(TechnologyRecord.CREATE_DATE).desc();
        //已验收节点
        List<TechnologyRecord> recordList = technologyRecordMapper.selectByExample(example);
        for (TechnologyRecord technologyRecord : recordList) {
            if (!CommonUtil.isEmpty(technologyRecord.getImage())) {
                String[] imgArr = technologyRecord.getImage().split(",");
                for (String s : imgArr) {
                    imageList.add(address + s);
                }
            }
        }
    }

    /**
     * 管家端验收详情
     */
    public ServerResponse stewardCheckDetail(String houseFlowApplyId) {
        try {
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            House house = houseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());
            HouseFlow supervisorHF = houseFlowMapper.getHouseFlowByHidAndWty(houseFlowApply.getHouseId(), 3);//大管家的hf
            HouseFlowApplyDTO houseFlowApplyDTO = new HouseFlowApplyDTO();
            if (supervisorHF != null) {
                houseFlowApplyDTO.setSupervisorHouseFlowId(supervisorHF.getId());
            }
            if (house != null) {
                houseFlowApplyDTO.setHouseName(house.getHouseName());
            }
            Member worker = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
            houseFlowApplyDTO.setHouseFlowApplyId(houseFlowApplyId);
            houseFlowApplyDTO.setApplyType(houseFlowApply.getApplyType());
            houseFlowApplyDTO.setWorkerTypeId(houseFlowApply.getWorkerTypeId());
            houseFlowApplyDTO.setWorkerTypeName(workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId()).getName());
            Example example = new Example(HouseFlowApplyImage.class);
            example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, houseFlowApplyId);
            List<HouseFlowApplyImage> houseFlowApplyImageList = houseFlowApplyImageMapper.selectByExample(example);
            List<String> imageList = new ArrayList<>();
            for (HouseFlowApplyImage houseFlowApplyImage : houseFlowApplyImageList) {
                imageList.add(address + houseFlowApplyImage.getImageUrl());
            }
            setImageList(houseFlowApply.getId(), address, imageList);
            houseFlowApplyDTO.setImageList(imageList);
            houseFlowApplyDTO.setDate(DateUtil.dateToString(houseFlowApply.getModifyDate(), "yyyy-MM-dd HH:mm"));
            if (houseFlowApply.getStartDate() != null) {
                houseFlowApplyDTO.setStartDate(houseFlowApply.getStartDate().getTime() - new Date().getTime()); //自动审核时间
            }
            return ServerResponse.createBySuccess("查询成功", houseFlowApplyDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}