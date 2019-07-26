package com.dangjia.acg.service.core;

import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.service.matter.TechnologyRecordService;
import com.dangjia.acg.service.worker.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/4/11 0011
 * Time: 17:08
 * 定时审核完工申请
 */
@Service
public class TimingApplyService {
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private EvaluateService evaluateService;

    @Autowired
    private TechnologyRecordService technologyRecordService;
    /**
     * 管家自动审核
     */
    public void supCouponApply(){
//        List<HouseFlowApply> houseFlowApplyList =  houseFlowApplyMapper.supCouponApply(new Date());
//        for (HouseFlowApply houseFlowApply : houseFlowApplyList){
            //取消自动审核，改为超时扣钱
//            evaluateService.checkOk(houseFlowApply.getId(),"",5);
//            evaluateService.supervisorOvertime(houseFlowApply);
//        }
    }

    /**
     * 旷工自动扣钱100
     */
    public void absenteeism(){
//        List<HouseFlow> houseFlows=technologyRecordService.unfinishedFlow(null);
//        for (HouseFlow houseFlow : houseFlows){
//            //今日是否开工超时扣旷工钱
//            evaluateService.absenteeismOvertime(houseFlow);
//        }

        Example example =new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.PAUSE,1);
        List<HouseFlow> houseFlowList=houseFlowMapper.selectByExample(example);
        for (HouseFlow houseFlow : houseFlowList) {
            //申请停工超过2天的，第3天起每天扣除1积分
            if(houseFlow.getPause()==1) {
                example = new Example(HouseFlowApply.class);
                example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, houseFlow.getId())
                        .andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                        .andCondition(" member_check in (1,3) ")
                        .andCondition(" (operator is null or operator=worker_id) ")
                        .andCondition("  to_days(start_date) <= to_days('" + DateUtil.getDateString(new Date().getTime()) + "') AND to_days(end_date) >= to_days('" + DateUtil.getDateString(new Date().getTime()) + "') ");


                List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.selectByExample(example);
                if (houseFlowApplyList.size() > 0) {
                    HouseFlowApply houseFlowApply = houseFlowApplyList.get(0);
                    Date start = houseFlowApply.getStartDate();
                    Date end = new Date();
                    int suspendDay = DateUtil.daysofTwo(start, end);
                    if (suspendDay > 1) {
                        evaluateService.updateMemberIntegral(houseFlow.getWorkerId(), houseFlow.getHouseId(), new BigDecimal(1), "申请停工超过2天，积分扣除");
                    }
                }
            }
            if(houseFlow.getPause()!=0) {
                //停工完结，状态变回
                Example example1 = new Example(HouseFlowApply.class);
                example1.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, houseFlow.getId())
                        .andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                        .andCondition(" member_check in (1,3) ")
                        .andCondition(" to_days(end_date) > to_days('" + DateUtil.getDateString(new Date().getTime()) + "') ");
                List list = houseFlowApplyMapper.selectByExample(example1);
                if (list.size() == 0) {
                    houseFlow.setPause(0);
                    houseFlow.setModifyDate(new Date());
                    houseFlowMapper.updateByPrimaryKey(houseFlow);
                }
            }

            if(houseFlow.getPause()==0) {
                if(houseFlow.getWorkSteta()!=1&&houseFlow.getWorkSteta()!=2&&houseFlow.getWorkSteta()!=6) {
                    //达到停工日期内，状态变为停工
                    Example example1 = new Example(HouseFlowApply.class);

                    example1.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, houseFlow.getId())
                            .andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                            .andCondition(" member_check in (1,3) ")
                            .andCondition("  to_days(start_date) <= to_days('" + DateUtil.getDateString(DateUtil.getNextDay().getTime()) + "') AND to_days(end_date) >= to_days('" + DateUtil.getDateString(DateUtil.getNextDay().getTime()) + "') ");
                    List list = houseFlowApplyMapper.selectByExample(example1);
                    if (list.size() > 0) {
                        houseFlow.setPause(1);
                        houseFlow.setModifyDate(new Date());
                        houseFlowMapper.updateByPrimaryKey(houseFlow);
                    }
                }
            }
        }
    }

    /**
     * 查询到时业主未审核申请
     */
    public void couponApply(){
        List<HouseFlowApply> houseFlowApplyList =  houseFlowApplyMapper.couponApply(new Date());
        for (HouseFlowApply houseFlowApply : houseFlowApplyList){
            if(houseFlowApply.getWorkerType() == 3){
                evaluateService.saveEvaluateSupervisor(null,houseFlowApply.getId(),"",5,true,false);
            }else {
                evaluateService.saveEvaluate(houseFlowApply.getId(),"",5,"",5,true);
            }
        }
    }
}
