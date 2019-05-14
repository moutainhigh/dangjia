package com.dangjia.acg.controller.app.core;

import com.dangjia.acg.api.app.core.HouseWorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.HouseWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 18:57
 */
@RestController
public class HouseWorkerController implements HouseWorkerAPI {

    @Autowired
    private HouseWorkerService houseWorkerService;
    @Autowired
    private CraftsmanConstructionService constructionService;

    /**
     * 根据工人id查询所有房子任务
     */
    @Override
    @ApiMethod
    public ServerResponse queryWorkerHouse(String userToken) {
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
    public ServerResponse getConstructionByWorkerId(HttpServletRequest request, String userToken, String cityId) {
        return constructionService.getConstructionView(request, userToken);
    }

    /**
     * 获取我的界面
     */
    @Override
    @ApiMethod
    public ServerResponse getMyHomePage(String userToken, String cityId) {
        return houseWorkerService.getMyHomePage(userToken);
    }

    /**
     * 获取申请单明细
     */

    @Override
    @ApiMethod
    public ServerResponse getHouseFlowApply(String userToken, String houseFlowApplyId) {
        return houseWorkerService.getHouseFlowApply(houseFlowApplyId);
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
        return houseWorkerService.getAdvanceInAdvance(houseFlowId);
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
}
