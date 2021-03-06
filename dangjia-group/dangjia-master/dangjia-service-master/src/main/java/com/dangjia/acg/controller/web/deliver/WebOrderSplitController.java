package com.dangjia.acg.controller.web.deliver;

import com.dangjia.acg.api.web.deliver.WebOrderSplitAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.deliver.SplitDeliver;
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
    public ServerResponse sentSplitDeliver(String splitDeliverId) {
        return orderSplitService.sentSplitDeliver(splitDeliverId);
    }

    /**
     * 供应商发货
     */
    @Override
    @ApiMethod
    public ServerResponse rejectionSplitDeliver(String splitDeliverId) {
        return orderSplitService.rejectionSplitDeliver(splitDeliverId);
    }

    /**
     * 发货单明细
     */
    @Override
    @ApiMethod
    public ServerResponse splitDeliverDetail(String splitDeliverId) {
        return orderSplitService.splitDeliverDetail(splitDeliverId);
    }

    @Override
    @ApiMethod
    public ServerResponse splitDeliverList(String supplierId, Integer shipState) {
        return orderSplitService.splitDeliverList(supplierId, shipState);
    }

    /**
     * 撤回供应商待发货的订单（整单撤回）
     */
    @Override
    @ApiMethod
    public ServerResponse withdrawSupplier(String orderSplitId) {
        return orderSplitService.withdrawSupplier(orderSplitId);
    }

    /**
     * 发送供应商
     * 分发不同供应商
     */
    @Override
    @ApiMethod
    public ServerResponse sentSupplier(String orderSplitId, String splitItemList) {
        return orderSplitService.sentSupplier(orderSplitId, splitItemList);
    }

    @Override
    @ApiMethod
    public ServerResponse cancelOrderSplit(String orderSplitId) {
        return orderSplitService.cancelOrderSplit(orderSplitId);
    }
    @Override
    @ApiMethod
    public ServerResponse cancelSplitDeliver(String splitDeliverId) {
        return orderSplitService.cancelSplitDeliver(splitDeliverId);
    }


    /**
     * 要货单看明细
     */
    @Override
    @ApiMethod
    public ServerResponse orderSplitItemList(String orderSplitId) {
        return orderSplitService.orderSplitItemList(orderSplitId);
    }

    @Override
    @ApiMethod
    public ServerResponse getHouseList( String cityId,PageDTO pageDTO, String likeAddress,String startDate, String endDate) {
        return orderSplitService.getHouseList(cityId,pageDTO, likeAddress, startDate,  endDate);
    }

    @Override
    @ApiMethod
    public ServerResponse getOrderSplitList(String houseId) {
        return orderSplitService.getOrderSplitList(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse setSplitDeliver(SplitDeliver splitDeliver) {
        return orderSplitService.setSplitDeliver(splitDeliver);
    }
}
