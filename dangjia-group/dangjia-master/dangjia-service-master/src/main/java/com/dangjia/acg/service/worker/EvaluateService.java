package com.dangjia.acg.service.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.worker.WorkIntegralDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyImageMapper;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IMaterialRecordMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IEvaluateMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseFlowApplyImage;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.MaterialRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.Evaluate;
import com.dangjia.acg.modle.worker.WorkIntegral;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.HouseFlowApplyService;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.repair.MendOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/27 0027
 * Time: 14:22
 * 评价积分系统
 */
@Service
public class EvaluateService {


    @Autowired
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IEvaluateMapper evaluateMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkIntegralMapper workIntegralMapper;
    @Autowired
    private HouseFlowApplyService houseFlowApplyService;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseMapper houseMapper;

    @Autowired
    private MendOrderService mendOrderService;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private IMaterialRecordMapper materialRecordMapper;
    @Autowired
    private ITechnologyRecordMapper technologyRecordMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IWorkerDetailMapper iWorkerDetailMapper;
    @Autowired
    private HouseService houseService;
    @Autowired
    private CraftsmanConstructionService constructionService;


    /**
     * 获取积分记录
     * @param userToken
     * @return
     */
    public ServerResponse queryWorkIntegral(HttpServletRequest request, PageDTO pageDTO, String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<WorkIntegralDTO> list = workIntegralMapper.queryWorkIntegral(worker.getId());
        PageInfo pageResult = new PageInfo(list);
        return ServerResponse.createBySuccess("ok", pageResult);
    }

    /**
     * 获取评价记录
     *
     * @param evaluate
     * @return
     */
    public ServerResponse queryEvaluates(HttpServletRequest request, String userToken, Evaluate evaluate) {
        Example example = new Example(Evaluate.class);
        Example.Criteria criteria = example.createCriteria();
        Object object = constructionService.getMember(userToken);
        if (object instanceof Member) {
            Member worker = (Member) object;
            criteria.andEqualTo(Evaluate.WORKER_ID, worker.getId());
        }
        if (!CommonUtil.isEmpty(evaluate.getHouseId())) {
            criteria.andEqualTo(Evaluate.HOUSE_ID, evaluate.getHouseId());
        }
        example.orderBy(Evaluate.MODIFY_DATE).desc();
        List<Evaluate> list = evaluateMapper.selectByExample(example);
        List<Map> listMap = new ArrayList<>();
        for (Evaluate evaluate1 : list) {
            Map map = BeanUtils.beanToMap(evaluate1);
            String memberId;
            if (evaluate1.getState() == 3) {
                memberId = evaluate1.getButlerId();
            } else {
                memberId = evaluate1.getMemberId();
            }
            Member member = memberMapper.selectByPrimaryKey(memberId);
            member.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            map.put(Member.HEAD, member.getHead());
            map.put("memberName", member.getNickName());
            if (evaluate1.getState() == 1) {
                map.put("memberName", "业主 " + member.getNickName());
            }
            if (evaluate1.getState() == 3) {
                map.put("memberName", "大管家 " + member.getNickName());
            }
            listMap.add(map);
        }
        return ServerResponse.createBySuccess("ok", listMap);
    }

    /**
     * 管家不通过工匠完工申请
     */
    public ServerResponse checkNo(String houseFlowApplyId, String content) {
        try {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            houseFlowApply.setApplyDec(content);
            houseFlowApply.setSupervisorCheck(2);
            houseFlowApply.setModifyDate(new Date());
            houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
            /*
            验收节点不通过
             */
            technologyRecordMapper.passNoTecRecord(houseFlowApply.getHouseId(), houseFlowApply.getWorkerTypeId());
            //业主不通过工匠发起阶段/整体完工申请驳回次数超过两次后将扣工人钱
            List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.noPassList(houseFlowApply.getHouseFlowId());
            if (houseFlowApplyList.size() > 2) {
                BigDecimal money = new BigDecimal(100);
                Member member = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("阶段/整体完工第" + houseFlowApplyList.size() + "次驳回,次数超过两次，工钱扣除");
                workerDetail.setWorkerId(member.getId());
                workerDetail.setWorkerName(member.getName());
                workerDetail.setHouseId(houseFlowApply.getHouseId());
                workerDetail.setMoney(money);
                workerDetail.setState(3);
                iWorkerDetailMapper.insert(workerDetail);
                BigDecimal surplusMoney = member.getSurplusMoney().subtract(money);
                BigDecimal haveMoney = member.getHaveMoney().subtract(money);
                member.setSurplusMoney(surplusMoney);
                member.setHaveMoney(haveMoney);
                memberMapper.updateByPrimaryKeySelective(member);
            }
            House house = houseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());
            configMessageService.addConfigMessage(null, "gj", houseFlowApply.getWorkerId(),
                    "0", "完工申请结果", String.format(DjConstants.PushMessage.STEWARD_APPLY_FINISHED_NOT_PASS,
                            house.getHouseName()), "5");
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    //    管家审核通过工匠完工申请，工匠扣钱
    public void supervisorOvertime(HouseFlowApply houseFlowApply) {
        //每超时一天扣除一次余额，每扣除一次  结束时间将延后一天，继续等待管家审核
        if (houseFlowApply.getStartDate().getTime() <= new Date().getTime()) {

            House house = houseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());
            Member member = memberMapper.getSupervisor(houseFlowApply.getHouseId());//houseId获得大管家
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
            BigDecimal money = new BigDecimal(100);
            BigDecimal surplusMoney = member.getSurplusMoney().subtract(money);
            BigDecimal haveMoney = member.getHaveMoney().subtract(money);
            WorkerDetail workerDetail = new WorkerDetail();
            workerDetail.setName("阶段/整体完工申请，审核超时，工钱扣除");
            workerDetail.setWorkerId(member.getId());
            workerDetail.setWorkerName(member.getName());
            workerDetail.setHouseId(houseFlowApply.getHouseId());
            workerDetail.setMoney(money);
            workerDetail.setWalletMoney(surplusMoney);
            workerDetail.setHaveMoney(haveMoney);
            workerDetail.setState(3);
            iWorkerDetailMapper.insert(workerDetail);
            member.setSurplusMoney(surplusMoney);
            member.setHaveMoney(haveMoney);
            memberMapper.updateByPrimaryKeySelective(member);
            //当前时间延后一天等待审核
            houseFlowApply.setStartDate(DateUtil.addDateDays(new Date(), 1));
            houseFlowApply.setModifyDate(new Date());
            houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
            configMessageService.addConfigMessage(null, "gj", member.getId(), "0",
                    "阶段/整体审核超时扣钱提醒", String.format(DjConstants.PushMessage.STEWARD_SHENGHECHAOSHI, house.getHouseName(),
                            workerType.getName()), "0");
        }
    }

    //工匠今日未开工，将扣除100
    public void absenteeismOvertime(HouseFlow houseFlow) {
        House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
        Member member = memberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
        if (member != null) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            BigDecimal money = new BigDecimal(100);
            BigDecimal surplusMoney = member.getSurplusMoney().subtract(money);
            BigDecimal haveMoney = member.getHaveMoney().subtract(money);
            WorkerDetail workerDetail = new WorkerDetail();
            workerDetail.setName(workerType.getName() + "旷工扣钱");
            workerDetail.setWorkerId(member.getId());
            workerDetail.setWorkerName(member.getName());
            workerDetail.setHouseId(houseFlow.getHouseId());
            workerDetail.setMoney(money);
            workerDetail.setWalletMoney(surplusMoney);
            workerDetail.setHaveMoney(haveMoney);
            workerDetail.setState(3);
            iWorkerDetailMapper.insert(workerDetail);
            member.setSurplusMoney(surplusMoney);
            member.setHaveMoney(haveMoney);
            memberMapper.updateByPrimaryKeySelective(member);
            configMessageService.addConfigMessage(null, "gj", member.getId(), "0",
                    workerType.getName() + "旷工扣钱",
                    String.format(DjConstants.PushMessage.CRAFTSMAN_ABSENTEEISM, house.getHouseName()), "0");
        }
    }

    /**
     * 管家审核通过工匠完工申请
     * 1.31 增加 剩余材料登记
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse materialRecord(String houseFlowApplyId, String content, int star, String productArr) {
        try {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            House house = houseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());
            //登记剩余材料
            JSONArray jsonArray = JSONArray.parseArray(productArr);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String productId = obj.getString("productId");
                double num = Double.parseDouble(obj.getString("num"));
                Product product = forMasterAPI.getProduct(house.getCityId(), productId);
                MaterialRecord materialRecord = new MaterialRecord();
                materialRecord.setHouseId(houseFlowApply.getHouseId());
                materialRecord.setWorkerTypeId(houseFlowApply.getWorkerTypeId());
                materialRecord.setApplyType(houseFlowApply.getApplyType());
                materialRecord.setNum(num);
                materialRecord.setProductId(productId);
                materialRecord.setCityId(house.getCityId());
                materialRecord.setProductSn(product.getProductSn());
                materialRecord.setProductName(product.getName());
                materialRecordMapper.insert(materialRecord);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return checkOk(houseFlowApplyId, content, star);
    }

    /**
     * 管家审核通过工匠完工申请
     * 1.30
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse checkOk(String houseFlowApplyId, String content, int star) {
        try {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            Member worker = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
            House house = houseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());
            if (houseFlowApply.getSupervisorCheck() == 1) {//大管家已审核通过过 不要重复
                return ServerResponse.createByErrorMessage("重复审核");
            }
            Member supervisor = memberMapper.getSupervisor(houseFlowApply.getHouseId());//houseId获得大管家
            Evaluate evaluate = new Evaluate();
            evaluate.setContent(content);
            evaluate.setMemberId(house.getMemberId());
            evaluate.setHouseId(houseFlowApply.getHouseId());
            evaluate.setButlerId(supervisor.getId());//存管家id
            if (star == 0) {
                evaluate.setStar(5);//0星为5星
            } else {
                evaluate.setStar(star);
            }
            evaluate.setHouseFlowApplyId(houseFlowApply.getId());
            evaluate.setHouseFlowId(houseFlowApply.getHouseFlowId());
            evaluate.setWorkerId(houseFlowApply.getWorkerId());
            evaluate.setWorkerName(worker.getName());
            evaluate.setState(3);//管家对工人的评价
            evaluate.setApplyType(houseFlowApply.getApplyType());
            evaluateMapper.insert(evaluate);
            updateIntegral(evaluate);//工人积分

            houseFlowApply.setSupervisorCheck(1);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 7);//业主倒计时
            houseFlowApply.setEndDate(calendar.getTime());
            houseFlowApply.setModifyDate(new Date());
            houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
            /*
             * 大管家每次审核拿钱 新算法 2018.08.03
             */
            if (houseFlowApply.getApplyType() == 1 || houseFlowApply.getApplyType() == 2) {
                Example example = new Example(HouseFlowApplyImage.class);
                example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, houseFlowApplyId);
                List<HouseFlowApplyImage> hfaiList = houseFlowApplyImageMapper.selectByExample(example);
                //算管家每次审核该拿的钱数
                //大管家的hf
                HouseFlow supervisorHF = houseFlowMapper.getHouseFlowByHidAndWty(houseFlowApply.getHouseId(), 3);
                houseFlowApply.setSupervisorMoney(supervisorHF.getCheckMoney());
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());

                //添加一条记录
                HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
                hfa.setHouseFlowId(houseFlowApply.getHouseFlowId());//工序id
                hfa.setWorkerId(supervisor.getId());//工人id
                hfa.setOperator(supervisor.getId());
                hfa.setWorkerTypeId(supervisor.getWorkerTypeId());//工种id
                hfa.setWorkerType(supervisor.getWorkerType());//工种类型
                hfa.setHouseId(houseFlowApply.getHouseId());//房子id
                hfa.setApplyType(houseFlowApply.getApplyType());//申请类型0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5巡查,6无人巡查
                hfa.setApplyMoney(new BigDecimal(0));//申请得钱
                hfa.setSupervisorMoney(new BigDecimal(0));
                hfa.setOtherMoney(new BigDecimal(0));
                hfa.setMemberCheck(1);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
                hfa.setSupervisorCheck(1);//大管家审核状态0未审核，1审核通过，2审核不通过
                hfa.setPayState(0);//是否付款
                hfa.setApplyDec("尊敬的业主，您好！<br/>" +
                        "当家大管家【"+supervisor.getName()+"】为您新家质量保驾护航，工地【"+workerType.getName()+"】已阶段完工，已经根据平台施工验收标准进行验收，未发现漏项及施工不合格情况，请您查收。<br/>" +
                        "【配图"+hfaiList.size()+"张以上】");//描述
//                hfa.setApplyDec("业主您好，我是大管家，我已验收了" + worker.getName() + (houseFlowApply.getApplyType() == 1 ? "的阶段完工" : "的整体完工"));//描述
                houseFlowApplyMapper.insert(hfa);
                houseService.insertConstructionRecord(hfa);
            }
            if (houseFlowApply.getApplyType() == 1) {
                //阶段审核
                HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowApply.getHouseFlowId());
                hf.setPause(1);
                houseFlowMapper.updateByPrimaryKeySelective(hf);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 业主评价管家完工 最后完工
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveEvaluateSupervisor(String userToken,String houseFlowApplyId, String content, int star, boolean isAuto,String onekey) {
        try {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            House house = houseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());
            if (houseFlowApply.getMemberCheck() == 1 || houseFlowApply.getMemberCheck() == 3) {
                return ServerResponse.createByErrorMessage("重复审核");
            }
            //业主同意一键退款
            if(!CommonUtil.isEmpty(onekey)&&"1".equals(onekey)) {
                ServerResponse response=mendOrderService.landlordOnekeyBack(userToken, house.getId());
                if (!response.isSuccess()) {
                    return response;
                }
            }
            Member worker = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
            Evaluate evaluate = new Evaluate();
            evaluate.setContent(content);
            evaluate.setMemberId(house.getMemberId());
            evaluate.setHouseId(houseFlowApply.getHouseId());
            evaluate.setButlerId("");
            if (star == 0) {
                evaluate.setStar(1);//0星为5星
            } else {
                evaluate.setStar(star);
            }
            evaluate.setHouseFlowApplyId(houseFlowApplyId);
            evaluate.setHouseFlowId(houseFlowApply.getHouseFlowId());
            evaluate.setWorkerId(worker.getId());
            evaluate.setWorkerName(worker.getName());
            evaluate.setState(1);//业主对工人
            evaluate.setApplyType(houseFlowApply.getApplyType());
            evaluateMapper.insert(evaluate);

            updateIntegral(evaluate);//工人积分
            updateCrowned(worker);//皇冠
            //评价之后修改工人的好评率
            updateFavorable(worker.getId());

            //业主审核管家
            houseFlowApplyService.checkSupervisor(houseFlowApplyId, isAuto);


            configMessageService.addConfigMessage(null, "gj", houseFlowApply.getWorkerId(), "0", "业主评价", String.format(DjConstants.PushMessage.CRAFTSMAN_EVALUATE, house.getHouseName()), "6");


            //短信通知业务本门
            Map<String, String> temp_para = new HashMap();
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
            temp_para.put("house_name", house.getHouseName());
            temp_para.put("worker_name", workerType.getName());
            JsmsUtil.sendSMS("15675101794", "164425", temp_para);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 保存业主端对管家对工人评价
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveEvaluate(String houseFlowApplyId, String wContent, int wStar
            , String sContent, int sStar, boolean isAuto) {
        try {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            House house = houseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());
            if (houseFlowApply.getMemberCheck() == 1 || houseFlowApply.getMemberCheck() == 3) {
                return ServerResponse.createByErrorMessage("重复审核");
            }
            //在worker中根据评论星数修改工人的积分
            Member worker = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
            Member supervisor = memberMapper.getSupervisor(houseFlowApply.getHouseId());//houseId获得大管家
            Evaluate evaluate;
            //查工匠被业主的评价
            evaluate = evaluateMapper.getForCountMoney(houseFlowApply.getHouseFlowId(), houseFlowApply.getApplyType(), worker.getId());
            if (evaluate == null) {
                evaluate = new Evaluate();
                evaluate.setContent(wContent);
                evaluate.setMemberId(house.getMemberId());
                evaluate.setHouseId(houseFlowApply.getHouseId());
                evaluate.setButlerId(supervisor.getId());
                if (wStar == 0) {
                    evaluate.setStar(1);//0星为5星
                } else {
                    evaluate.setStar(wStar);//工人
                }
                evaluate.setHouseFlowApplyId(houseFlowApplyId);
                evaluate.setHouseFlowId(houseFlowApply.getHouseFlowId());
                evaluate.setWorkerId(worker.getId());
                evaluate.setWorkerName(worker.getName());
                evaluate.setState(1);//业主对工人
                evaluate.setApplyType(houseFlowApply.getApplyType());
                evaluateMapper.insert(evaluate);
            } else {
                evaluate.setContent(wContent);
                evaluate.setStar(wStar);//工人
                evaluateMapper.updateByPrimaryKeySelective(evaluate);
            }
            updateIntegral(evaluate);//工人积分

            //查大管家被业主的评价
            evaluate = evaluateMapper.getForCountMoney(houseFlowApply.getHouseFlowId(), houseFlowApply.getApplyType(), supervisor.getId());
            if (evaluate == null) {
                evaluate = new Evaluate();
                evaluate.setContent(sContent);
                evaluate.setMemberId(house.getMemberId());
                evaluate.setHouseId(houseFlowApply.getHouseId());
                if (wStar == 0) {
                    evaluate.setStar(1);//0星为5星
                } else {
                    evaluate.setStar(sStar);//管家
                }
                evaluate.setHouseFlowApplyId(houseFlowApplyId);
                evaluate.setHouseFlowId(houseFlowApply.getHouseFlowId());
                evaluate.setWorkerId(supervisor.getId());
                evaluate.setWorkerName(supervisor.getName());
                evaluate.setState(1);//业主对工人
                evaluate.setApplyType(houseFlowApply.getApplyType());
                evaluateMapper.insert(evaluate);
            } else {
                evaluate.setContent(sContent);
                evaluate.setStar(sStar);//管家
                evaluateMapper.updateByPrimaryKeySelective(evaluate);
            }
            updateIntegral(evaluate);//管家积分

            updateCrowned(worker);//皇冠
            updateCrowned(supervisor);//皇冠
            //评价之后修改工人的好评率
            updateFavorable(worker.getId());
            updateFavorable(supervisor.getId());

            //业主审核
            ServerResponse serverResponse = houseFlowApplyService.checkWorker(houseFlowApplyId, isAuto);
            if (!serverResponse.isSuccess()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return serverResponse;
            }
            configMessageService.addConfigMessage(null, "gj", worker.getId(), "0", "业主评价", String.format(DjConstants.PushMessage.CRAFTSMAN_EVALUATE, house.getHouseName()), "6");
            configMessageService.addConfigMessage(null, "gj", supervisor.getId(), "0", "业主评价", String.format(DjConstants.PushMessage.STEWARD_EVALUATE, house.getHouseName()), "6");

            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 业主对工人评价之后计算积分
     */
    public void updateIntegral(Evaluate evaluate) {
        Member worker = memberMapper.selectByPrimaryKey(evaluate.getWorkerId());
        String desc = "";
        if (evaluate.getApplyType() == 1) {
            desc = "阶段完工";
        }
        if (evaluate.getApplyType() == 2) {
            desc = "整体完工";
        }
        if (evaluate.getState() == 1) {
            desc = desc + " 业主";
        }
        if (evaluate.getState() == 2) {
            desc = desc + " 商品";
        }
        if (evaluate.getState() == 3) {
            desc = desc + " 大管家";
        }

        BigDecimal evaluationXA = new BigDecimal("1.0");
        BigDecimal score = new BigDecimal(0);

        if (worker.getWorkerType() == 3) {//管家加分
            if (worker.getEvaluationScore().compareTo(new BigDecimal("70")) == -1) {
                score = evaluationXA.multiply(new BigDecimal("0.6"));
            } else if (worker.getEvaluationScore().compareTo(new BigDecimal("70")) >= 0 &&
                    worker.getEvaluationScore().compareTo(new BigDecimal("80")) == -1) {
                score = evaluationXA.multiply(new BigDecimal("0.6"));
            } else if (worker.getEvaluationScore().compareTo(new BigDecimal("80")) >= 0 &&
                    worker.getEvaluationScore().compareTo(new BigDecimal("90")) == -1) {
                score = evaluationXA.multiply(new BigDecimal("0.15"));
            } else if (worker.getEvaluationScore().compareTo(new BigDecimal("90")) >= 0) {
                score = evaluationXA.multiply(new BigDecimal("0.07"));
            }
        } else {
            if (worker.getEvaluationScore().compareTo(new BigDecimal("70")) == -1) {

                score = evaluationXA.multiply(new BigDecimal("1.6"));
            } else if (worker.getEvaluationScore().compareTo(new BigDecimal("70")) >= 0 &&
                    worker.getEvaluationScore().compareTo(new BigDecimal("80")) == -1) {

                score = evaluationXA.multiply(new BigDecimal("0.8"));
            } else if ((worker.getEvaluationScore().compareTo(new BigDecimal("80")) == 1 ||
                    worker.getEvaluationScore().compareTo(new BigDecimal("80")) == 0) &&
                    worker.getEvaluationScore().compareTo(new BigDecimal("90")) == -1) {

                score = evaluationXA.multiply(new BigDecimal("0.4"));
            } else if (worker.getEvaluationScore().compareTo(new BigDecimal("90")) == 1 ||
                    worker.getEvaluationScore().compareTo(new BigDecimal("90")) == 0) {

                score = evaluationXA.multiply(new BigDecimal("0.2"));
            }
        }

        if (worker.getEvaluationScore() == null) {
            worker.setEvaluationScore(new BigDecimal("60.0"));
        }
        WorkIntegral workIntegral = new WorkIntegral();

        if (evaluate.getStar() == 5) {
            BigDecimal evaluationScore = worker.getEvaluationScore().add(score);
            worker.setEvaluationScore(evaluationScore);
            workIntegral.setIntegral(score);
        } else if (evaluate.getStar() == 1 || evaluate.getStar() == 2) {
            BigDecimal evaluationScore = worker.getEvaluationScore().subtract((score.multiply(new BigDecimal(2))));
            worker.setEvaluationScore(evaluationScore);//减双倍
            workIntegral.setIntegral(score.multiply(new BigDecimal(-2)));
        } else {
            workIntegral.setIntegral(new BigDecimal(0));  //不增不减
        }
        workIntegral.setWorkerId(worker.getId());
        workIntegral.setMemberId(evaluate.getMemberId());
        workIntegral.setButlerId(evaluate.getButlerId());
        workIntegral.setStar(evaluate.getStar());
        workIntegral.setStatus(1);
        workIntegral.setHouseId(evaluate.getHouseId());

        workIntegral.setBriefed(desc + evaluate.getStar() + "星评价");
        workIntegralMapper.insert(workIntegral);

        memberMapper.updateByPrimaryKeySelective(worker);
    }

    /**
     * 扣除指定用户的积分
     */
    public void updateMemberIntegral(String workerId, String houseId, BigDecimal score, String desc) {
        Member worker = memberMapper.selectByPrimaryKey(workerId);
        WorkIntegral workIntegral = new WorkIntegral();
        BigDecimal evaluationScore = worker.getEvaluationScore().subtract(score);
        worker.setEvaluationScore(evaluationScore);//减积分
        workIntegral.setIntegral(new BigDecimal("-" + score.doubleValue()));
        workIntegral.setWorkerId(worker.getId());
        workIntegral.setMemberId(workerId);
        workIntegral.setButlerId(workerId);
        workIntegral.setStar(0);
        workIntegral.setStatus(0);
        workIntegral.setHouseId(houseId);
        workIntegral.setBriefed(desc);
        workIntegralMapper.insert(workIntegral);
        memberMapper.updateByPrimaryKeySelective(worker);
    }

    /**
     * 用于在工人被评价之后修改好评率
     */
    private void updateFavorable(String workerId) {
        Member worker = memberMapper.selectByPrimaryKey(workerId);
        Example example = new Example(Evaluate.class);
        example.createCriteria().andEqualTo(Evaluate.WORKER_ID, worker.getId());
        List<Evaluate> evaluateList = evaluateMapper.selectByExample(example);
        int astar = 0;
        for (Evaluate el : evaluateList) {
            astar += el.getStar();
        }
        BigDecimal praiseRate = new BigDecimal(astar).divide(new BigDecimal(5 * evaluateList.size()), 2, BigDecimal.ROUND_HALF_UP);
        worker.setPraiseRate(praiseRate);
        memberMapper.updateByPrimaryKeySelective(worker);
    }

    /**
     * 皇冠规则
     */
    private void updateCrowned(Member worker) {
        try {
            if (worker.getEvaluationScore().compareTo(new BigDecimal("90")) >= 0) {
                Example example = new Example(Evaluate.class);
                example.createCriteria().andEqualTo(Evaluate.WORKER_ID, worker.getId());
                List<Evaluate> evaluateList = evaluateMapper.selectByExample(example);
                if (evaluateList.size() >= 3) {
                    boolean flag = true;
                    for (int i = 0; i < 3; i++) {
                        if (evaluateList.get(i).getStar() != 5) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        worker.setIsCrowned(1);
                        memberMapper.updateByPrimaryKeySelective(worker);
                    }
                }
                if (evaluateList.size() > 0) {
                    if (evaluateList.get(0).getStar() < 3) {
                        worker.setIsCrowned(0);
                        memberMapper.updateByPrimaryKeySelective(worker);
                    }
                }
                if (evaluateList.size() >= 2) {
                    if ((evaluateList.get(0).getStar() == 3 || evaluateList.get(0).getStar() == 4) && (evaluateList.get(1).getStar() == 3 || evaluateList.get(1).getStar() == 4)) {
                        worker.setIsCrowned(0);
                        memberMapper.updateByPrimaryKeySelective(worker);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
