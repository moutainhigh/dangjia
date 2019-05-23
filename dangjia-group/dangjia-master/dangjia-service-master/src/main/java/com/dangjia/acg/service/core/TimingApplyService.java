package com.dangjia.acg.service.core;

import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.service.worker.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private EvaluateService evaluateService;

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
     * 查询到时业主未审核申请
     */
    public void couponApply(){
        List<HouseFlowApply> houseFlowApplyList =  houseFlowApplyMapper.couponApply(new Date());
        for (HouseFlowApply houseFlowApply : houseFlowApplyList){
            if(houseFlowApply.getWorkerType() == 3){
                evaluateService.saveEvaluateSupervisor(houseFlowApply.getId(),"",5,true);
            }else {
                evaluateService.saveEvaluate(houseFlowApply.getId(),"",5,"",5,true);
            }
        }
    }
}
