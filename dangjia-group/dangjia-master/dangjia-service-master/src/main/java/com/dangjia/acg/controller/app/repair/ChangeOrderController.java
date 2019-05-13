package com.dangjia.acg.controller.app.repair;

import com.dangjia.acg.api.app.repair.ChangeOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.ChangeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2019/1/8 0008
 * Time: 14:03
 */
@RestController
public class ChangeOrderController implements ChangeOrderAPI {
    @Autowired
    private ChangeOrderService changeOrderService;


    /**
     * 管家审核变更单
     *  check 1通过 2不通过
     */
    @Override
    @ApiMethod
    public ServerResponse supCheckChangeOrder(String userToken,String changeOrderId,Integer check){
        return changeOrderService.supCheckChangeOrder(userToken,changeOrderId,check);
    }

    /**
     * 变更单详情
     */
    @Override
    @ApiMethod
    public ServerResponse changeOrderDetail(String changeOrderId){
        return changeOrderService.changeOrderDetail(changeOrderId);
    }

    /**
     * 查询变更单列表
     * type 1工匠 2业主 3管家
     */
    @Override
    @ApiMethod
    public ServerResponse queryChangeOrder(String userToken,String houseId,Integer type){
        return changeOrderService.queryChangeOrder(userToken,houseId,type);
    }

    /**
     * 提交变更单
     * type 1工匠补  2业主退
     */
    @Override
    @ApiMethod
    public ServerResponse workerSubmit(String userToken,String houseId,Integer type,String contentA,String contentB,String workerTypeId){
        return changeOrderService.workerSubmit(userToken,houseId,type,contentA,contentB,workerTypeId);
    }

    /**
     * 申请退人工或业主验收检测
     * type 1阶段验收检测  2退人工检测
     */
    public ServerResponse checkHouseFlowApply(String userToken,String houseId,Integer type,String workerTypeId){
        return changeOrderService.checkHouseFlowApply(userToken,houseId,type,workerTypeId);
    }
}
