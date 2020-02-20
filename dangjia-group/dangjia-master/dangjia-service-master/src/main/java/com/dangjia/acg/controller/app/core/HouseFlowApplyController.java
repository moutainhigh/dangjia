package com.dangjia.acg.controller.app.core;

import com.dangjia.acg.api.app.core.HouseFlowApplyAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.core.HouseFlowApplyService;
import com.dangjia.acg.service.core.TimingApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/11/26 0026
 * Time: 20:18
 */
@RestController
public class HouseFlowApplyController implements HouseFlowApplyAPI {

    @Autowired
    private HouseFlowApplyService houseFlowApplyService;
    @Autowired
    private TimingApplyService timingApplyService;

    @Override
    @ApiMethod
    public void supCouponApply() {
        timingApplyService.supCouponApply();
    }

    @Override
    @ApiMethod
    public void couponApply() {
        timingApplyService.couponApply();
    }

    @Override
    @ApiMethod
    public void absenteeism() {
        timingApplyService.absenteeism();
    }

    /**
     * 工匠端工地记录
     */
    @Override
    @ApiMethod
    public ServerResponse houseRecord(String userToken, String houseId, PageDTO pageDTO) {
        return houseFlowApplyService.houseRecord(userToken, houseId, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse checkWorker(String userToken, String houseFlowApplyId) {
        return houseFlowApplyService.checkWorker(houseFlowApplyId, false);
    }

    @Override
    @ApiMethod
    public ServerResponse checkDetail(String userToken, String houseFlowApplyId) {
        return houseFlowApplyService.checkDetail(houseFlowApplyId);
    }

    @Override
    @ApiMethod
    public ServerResponse stewardCheckDetail(String userToken, String houseFlowApplyId) {
        return houseFlowApplyService.stewardCheckDetail(houseFlowApplyId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryAcceptanceTrend(String userToken,String houseId) {
        return houseFlowApplyService.queryAcceptanceTrend( userToken,houseId);
    }
}
