package com.dangjia.acg.controller.app.core;

import com.dangjia.acg.api.app.core.HouseWorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
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
    public ServerResponse setWorkerGrab(String userToken, String cityId, String houseFlowId, Integer type) {
        return houseWorkerService.setWorkerGrab( userToken, cityId, houseFlowId,  type);
    }

    /**
     * 业主换人
     */
    @Override
    @ApiMethod
    public ServerResponse setChangeWorker(String userToken, String houseWorkerId) {
        return houseWorkerService.setChangeWorker(userToken, houseWorkerId);
    }

    @Override
    @ApiMethod
    public ServerResponse getHouseWorker(String userToken, String houseFlowId) {
        return houseWorkerService.getHouseWorker(userToken, houseFlowId);
    }


    @Override
    @ApiMethod
    public ServerResponse getWorkerInFo(String userToken, String houseFlowId) {
        return houseWorkerService.getWorkerInFo(userToken, houseFlowId);
    }

    @Override
    @ApiMethod
    public  ServerResponse getWorkerComplainInFo(String userToken, String houseFlowId){
        return houseWorkerService.getWorkerComplainInFo( userToken,houseFlowId);
    }

    /**
     * 根据工人id查询自己的施工界面
     */
    @Override
    @ApiMethod
    public ServerResponse getConstructionByWorkerId(HttpServletRequest request, String userToken,  String houseWorkerId,String cityId) {
        return constructionService.getConstructionView(request, userToken,  houseWorkerId);
    }

    @Override
    @ApiMethod
    public ServerResponse getConstructionInfo(HttpServletRequest request, String userToken, String houseId,String houseFlowId){
        return constructionService.getConstructionInfo(request,userToken,houseId,houseFlowId);
    }

    @Override
    @ApiMethod
    public ServerResponse getJobsInfo(HttpServletRequest request, String userToken, String houseId, String productId){
        return constructionService.getJobsInfo(request,userToken,houseId,productId);
    }
    /**
     * 获取申请单明细
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseFlowApply(String userToken, String houseFlowApplyId) {
        return houseWorkerService.getHouseFlowApply(houseFlowApplyId);
    }

    @Override
    @ApiMethod
    public ServerResponse getHouseDetectionTimeout(String userToken) {
        return houseWorkerService.getHouseDetectionTimeout(userToken);
    }
    /**
     * 提交审核、停工
     */
    @Override
    @ApiMethod
    public ServerResponse setHouseFlowApply(String userToken, Integer applyType, String houseFlowId,
                                            String applyDec, String imageList,
                                            String latitude, String longitude,String returnableMaterial,
                                            String materialProductArr) {
        return houseWorkerService.setHouseFlowApply(userToken, applyType, houseFlowId, applyDec,
                imageList, latitude, longitude,returnableMaterial,materialProductArr);
    }

    /**
     * 提前进场
     */
    @Override
    @ApiMethod
    public ServerResponse getAdvanceInAdvance(String userToken, String houseFlowId) {
        return houseWorkerService.getAdvanceInAdvance(houseFlowId);
    }

    @Override
    @ApiMethod
    public ServerResponse getMyHouseFlowList(PageDTO pageDTO, String userToken) {
        return houseWorkerService.getMyHouseFlowList(pageDTO, userToken);
    }

    /**
     * 切换工地
     */
    @Override
    @ApiMethod
    public ServerResponse setSwitchHouseFlow(String userToken, String houseWorkerId) {
        return houseWorkerService.setSwitchHouseFlow(userToken, houseWorkerId);
    }

    /**
     * 大管家申请验收
     */
    @Override
    @ApiMethod
    public ServerResponse setSupervisorApply(String userToken, String houseFlowId) {
        return houseWorkerService.setSupervisorApply(userToken, houseFlowId);
    }

    /**
     * 大管家申请验收
     */
    @Override
    @ApiMethod
    public ServerResponse autoDistributeHandle(String houseFlowId) {
        return houseWorkerService.autoDistributeHandle(houseFlowId);
    }
}
