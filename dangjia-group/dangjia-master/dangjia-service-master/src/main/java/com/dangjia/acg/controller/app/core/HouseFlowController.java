package com.dangjia.acg.controller.app.core;

import com.dangjia.acg.api.app.core.HouseFlowAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.service.core.HouseFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 17:51
 */
@RestController
public class HouseFlowController implements HouseFlowAPI {

    @Autowired
    private HouseFlowService houseFlowService;

    /**
     * 抢单列表
     *
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getGrabList(String userToken, String cityId) {
        return houseFlowService.getGrabList(userToken, cityId);
    }

    /**
     * 抢单验证
     *
     * @param userToken
     * @param houseFlowId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setGrabVerification(String userToken, String cityId, String houseFlowId) {
        return houseFlowService.setGrabVerification(userToken, cityId, houseFlowId);
    }

    /**
     * 放弃此单
     *
     * @param userToken
     * @param houseFlowId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setGiveUpOrder(String userToken, String houseFlowId) {
        return houseFlowService.setGiveUpOrder(userToken, houseFlowId);
    }

    /**
     * 工人30分钟自动放弃抢单任务，工人未购买保险或者保险服务剩余天数小于等于60天则自动放弃订单
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse autoGiveUpOrder() {
        return houseFlowService.autoGiveUpOrder();
    }

    /**
     * 工匠自动续保
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse autoRenewOrder() {
        return houseFlowService.autoRenewOrder();
    }
    /**
     * 拒单
     *
     * @param userToken
     * @param houseFlowId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setRefuse(String userToken, String cityId, String houseFlowId) {
        return houseFlowService.setRefuse(userToken, cityId, houseFlowId);
    }

    /**
     * 确认开工
     *
     * @param userToken
     * @param houseFlowId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setConfirmStart(HttpServletRequest request,String userToken, String houseFlowId) {
        return houseFlowService.setConfirmStart(request,userToken, houseFlowId);
    }

    /**
     * 根据houseId查询除设计精算外的可用工序
     *
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public List<HouseFlow> getWorkerFlow(String houseId) {
        return houseFlowService.getWorkerFlow(houseId);
    }

    /**
     * 根据houseId和工种类型查询HouseFlow
     */
    @Override
    @ApiMethod
    public HouseFlow getHouseFlowByHidAndWty(String houseId, Integer workerType) {
        return houseFlowService.getHouseFlowByHidAndWty(houseId, workerType);
    }
}
