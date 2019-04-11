package com.dangjia.acg.service.core;

import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.modle.core.HouseFlowApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private HouseFlowApplyService houseFlowApplyService;


    /**
     * 查询到时业主未审核申请
     */
    public void couponApply(){
        List<HouseFlowApply> houseFlowApplyList =  houseFlowApplyMapper.couponApply();
        for (HouseFlowApply houseFlowApply : houseFlowApplyList){
            houseFlowApplyService.checkWorker(houseFlowApply.getId());
        }
    }
}
