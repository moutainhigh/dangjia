package com.dangjia.acg.controller.delivery;

import com.dangjia.acg.api.delivery.DjDeliverOrderItemAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.delivery.DjDeliverOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DjDeliverOrderItemController implements DjDeliverOrderItemAPI {

    @Autowired
    private DjDeliverOrderItemService djDeliverOrderItemService;


    @Override
    @ApiMethod
    public ServerResponse queryPaymentToBeMade(String orderId) {
        return djDeliverOrderItemService.queryPaymentToBeMade(orderId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryHumpDetail(String orderId) {
        return djDeliverOrderItemService.queryHumpDetail(orderId);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteOrder(String orderId) {
        return djDeliverOrderItemService.deleteOrder(orderId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryOrderSnapshot(String orderId) {
        return djDeliverOrderItemService.queryOrderSnapshot(orderId);
    }

    @Override
    @ApiMethod
    public ServerResponse setCancellationOrder(String orderId) {
        return djDeliverOrderItemService.setCancellationOrder(orderId);
    }

    @Override
    @ApiMethod
    public ServerResponse setAcceptanceEvaluation(String userToken, String jsonStr) {
        return djDeliverOrderItemService.setAcceptanceEvaluation(userToken,jsonStr);
    }
}
