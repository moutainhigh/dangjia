package com.dangjia.acg.controller.web.deliver;

import com.dangjia.acg.api.web.deliver.WebOrderSplitAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.deliver.OrderSplitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class WebOrderSplitController implements WebOrderSplitAPI {

    @Autowired
    private OrderSplitService orderSplitService;//生成发货单


    /**
     * 供应商发货
     */
    @Override
    @ApiMethod
    public ServerResponse sentSplitDeliver(String splitDeliverId){
        return orderSplitService.sentSplitDeliver(splitDeliverId);
    }

    @Override
    @ApiMethod
    public ServerResponse splitDeliverList(String supplierId, int shipState){
        return orderSplitService.splitDeliverList(supplierId, shipState);
    }

    /**
     * 发送供应商
     * 分发不同供应商
     */
    @Override
    @ApiMethod
    public ServerResponse sentSupplier(String orderSplitId, String splitItemList){
        return orderSplitService.sentSupplier(orderSplitId, splitItemList);
    }

    @Override
    @ApiMethod
    public ServerResponse cancelOrderSplit(String orderSplitId){
        return orderSplitService.cancelOrderSplit(orderSplitId);
    }

    /**
     * 要货单看明细
     */
    @Override
    @ApiMethod
    public ServerResponse orderSplitItemList(String orderSplitId){
        return orderSplitService.orderSplitItemList(orderSplitId);
    }

    @Override
    @ApiMethod
    public ServerResponse getHouseList(Integer pageNum,Integer pageSize){
        return orderSplitService.getHouseList(pageNum,pageSize);
    }

    @Override
    @ApiMethod
    public ServerResponse getOrderSplitList(String houseId){
        return orderSplitService.getOrderSplitList(houseId);
    }
}
