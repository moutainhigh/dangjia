package com.dangjia.acg.controller.app.core;

import com.dangjia.acg.api.app.core.HouseWorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.core.HouseWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 18:57
 */
@RestController
public class HouseWorkerController implements HouseWorkerAPI {

    @Autowired
    private HouseWorkerService houseWorkerService;

    /**
     *  根据工人id查询所有房子任务
     */
    @Override
    @ApiMethod
    public ServerResponse queryWorkerHouse(String userToken){
        return houseWorkerService.queryWorkerHouse(userToken);
    }
    /**
     * 抢单
     */
    @Override
    @ApiMethod
    public ServerResponse setWorkerGrab(String userToken, String cityId, String houseFlowId) {
        return houseWorkerService.setWorkerGrab(userToken, cityId, houseFlowId);
    }

    /**
     * 业主换人
     */
    @Override
    @ApiMethod
    public ServerResponse setChangeWorker(String userToken, String houseWorkerId) {
        return houseWorkerService.setChangeWorker(userToken, houseWorkerId);
    }

    /**
     * 根据工人id查询自己的施工界面
     */
    @Override
    @ApiMethod
    public ServerResponse getConstructionByWorkerId(String userToken, String cityId) {
        return houseWorkerService.getConstructionByWorkerId(userToken, cityId);
    }

    /**
     * 获取我的界面
     */
    @Override
    @ApiMethod
    public ServerResponse getMyHomePage(String userToken, String cityId) {
        return houseWorkerService.getMyHomePage(userToken, cityId);
    }

    /**
     * 提现列表
     */
    @Override
    @ApiMethod
    public ServerResponse getExtractMoney(String userToken) {
        return houseWorkerService.getExtractMoney(userToken);
    }

    /**
     * 提现详情
     */
    @Override
    @ApiMethod
    public ServerResponse getExtractMoneyDetail(String userToken, String workerDetailId) {
        return houseWorkerService.getExtractMoneyDetail(userToken, workerDetailId);
    }

    /**
     * 获取验提现证码
     */
    @Override
    @ApiMethod
    public ServerResponse getPaycode(String userToken, String phone) {
        return houseWorkerService.getPaycode(userToken, phone);
    }

    /**
     * 验证并提现
     */
    @Override
    @ApiMethod
    public ServerResponse checkFinish(String userToken, String smscode, String money) {
        return houseWorkerService.checkFinish(userToken, smscode, money);
    }

    /**
     * 提交审核、停工
     *
     * @param userToken
     * @param applyType
     * @param houseFlowId
     * @param suspendDay
     * @param applyDec
     * @param imageList
     * @param houseFlowId2
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setHouseFlowApply(String userToken, Integer applyType, String houseFlowId, Integer suspendDay,
                                            String applyDec, String imageList, String houseFlowId2) {
        try {
            return houseWorkerService.setHouseFlowApply(userToken, applyType, houseFlowId, suspendDay, applyDec, imageList, houseFlowId2);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交审核失败,图片数组转换失败");
        }
    }

    /**
     * 提前进场
     */
    @Override
    @ApiMethod
    public ServerResponse getAdvanceInAdvance(String userToken, String houseFlowId) {
        return houseWorkerService.getAdvanceInAdvance(userToken, houseFlowId);
    }

    /**
     * 查询工地列表
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseFlowList(String userToken) {
        return houseWorkerService.getHouseFlowList(userToken);
    }

    /**
     * 切换工地
     */
    @Override
    @ApiMethod
    public ServerResponse setSwitchHouseFlow(String userToken, String houseFlowId) {
        return houseWorkerService.setSwitchHouseFlow(userToken, houseFlowId);
    }

    /**
     * 大管家申请验收
     */
    @Override
    @ApiMethod
    public ServerResponse setSupervisorApply(String userToken, String houseFlowId) {
        return houseWorkerService.setSupervisorApply(userToken, houseFlowId);
    }

    /*
     * 获取提现信息
     */
    @Override
    @ApiMethod
    public ServerResponse getWithdrawalInformation(String userToken) {
        return houseWorkerService.getWithdrawalInformation(userToken);
    }

}
